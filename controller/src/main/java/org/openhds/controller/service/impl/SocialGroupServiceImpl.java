package org.openhds.controller.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.controller.idgeneration.SocialGroupGenerator;
import org.openhds.controller.service.EntityService;
import org.openhds.controller.service.IndividualService;
import org.openhds.controller.service.SocialGroupService;
import org.openhds.dao.service.GenericDao;
import org.openhds.domain.model.Death;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.Membership;
import org.openhds.domain.model.SocialGroup;
import org.openhds.domain.service.SitePropertiesService;
import org.springframework.transaction.annotation.Transactional;

public class SocialGroupServiceImpl implements SocialGroupService {

    private EntityService service;
    private GenericDao genericDao;
    private IndividualService individualService;
    private SocialGroupGenerator generator;
    private SitePropertiesService properties;

    public SocialGroupServiceImpl(GenericDao genericDao, IndividualService individualService, EntityService service,
            SocialGroupGenerator generator, SitePropertiesService properties) {
        this.genericDao = genericDao;
        this.individualService = individualService;
        this.service = service;
        this.generator = generator;
        this.properties = properties;
    }

    /**
     * Creates a social group. This required be in its own method because its possible that a social group refers to a
     * nonexistent (temporary) individual (head group and/or respondent). In this case the temporary individual is
     * created first, assigned to the social group, and the social group created.
     * 
     * If the head of group and/or respondent refer to persisted entities, then they are used and the social group is
     * saved normally.
     * 
     * @param entityItem
     */
    @Transactional(rollbackFor = Exception.class)
    public SocialGroup createSocialGroup(SocialGroup entityItem) throws Exception {
        String headExtId = entityItem.getGroupHead().getExtId();
        String respondentId = entityItem.getRespondent().getExtId();

        Individual targetHead = individualService.findIndivById(headExtId);
        Individual targetRespondent = individualService.findIndivById(respondentId);

        if (targetHead == null) {
            targetHead = individualService.createTemporaryIndividualWithExtId(headExtId, entityItem.getCollectedBy());
        }

        if (targetRespondent == null) {
            if (headExtId.equals(respondentId)) {
                targetRespondent = targetHead;
            } else {
                targetRespondent = individualService.createTemporaryIndividualWithExtId(respondentId,
                        entityItem.getCollectedBy());
            }
        }

        entityItem.setGroupHead(targetHead);
        entityItem.setRespondent(targetRespondent);
        Membership membership = createGroupHeadMembership(entityItem);

        service.create(entityItem);
        service.create(membership);

        return entityItem;
    }

    private Membership createGroupHeadMembership(SocialGroup sg) {
        Membership membership = new Membership();
        membership.setEndType(properties.getNotApplicableCode());
        membership.setStartType(properties.getEnumerationCode());
        membership.setIndividual(sg.getGroupHead());
        membership.setbIsToA("01");
        membership.setCollectedBy(sg.getCollectedBy());
        membership.setInsertDate(Calendar.getInstance());
        membership.setSocialGroup(sg);
        membership.setStartDate(sg.getDateOfInterview());

        return membership;
    }

    public SocialGroup evaluateSocialGroup(SocialGroup entityItem, boolean overrideIdGeneration)
            throws ConstraintViolations {

        if (entityItem.getGroupHead().getExtId() == null)
            entityItem.setGroupHead(null);

        if (individualService.getLatestEvent(entityItem.getGroupHead()) instanceof Death)
            throw new ConstraintViolations("A Social Group cannot be created for an Individual who has a Death event.");

        if (generator.generated && !overrideIdGeneration)
            return generateId(entityItem);

        if (findSocialGroupById(entityItem.getExtId()) != null)
            throw new ConstraintViolations("The social group external id already exists");

        generator.validateIdLength(entityItem.getExtId(), generator.getIdScheme());

        return entityItem;
    }

    public SocialGroup generateId(SocialGroup entityItem) throws ConstraintViolations {
        entityItem.setExtId(generator.generateId(entityItem));
        return entityItem;
    }

    public SocialGroup checkSocialGroup(SocialGroup persistedItem, SocialGroup entityItem) throws ConstraintViolations {
        if (!compareDeathInSocialGroup(persistedItem, entityItem))
            throw new ConstraintViolations(
                    "A Social Group cannot be saved because an attempt was made to set the Group Head on an Individual who has a Death event.");
        return entityItem;
    }

    /**
     * Retrieves all Social Group extId's that contain the term provided. Used in performing autocomplete.
     */
    public List<String> getSocialGroupExtIds(String term) {
        List<String> ids = new ArrayList<String>();
        List<SocialGroup> list = genericDao.findListByPropertyPrefix(SocialGroup.class, "extId", term, 10, true);
        for (SocialGroup sg : list) {
            ids.add(sg.getExtId());
        }

        return ids;
    }

    /**
     * Compares the persisted and (soon to be persisted) SocialGroup items. If the persisted item and entity item has a
     * mismatch of an end event type and the persisted item has a end type of death, the edit cannot be saved.
     */
    public boolean compareDeathInSocialGroup(SocialGroup persistedItem, SocialGroup entityItem) {
        if (individualService.getLatestEvent(persistedItem.getGroupHead()) instanceof Death
                || individualService.getLatestEvent(entityItem.getGroupHead()) instanceof Death)
            return false;

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteSocialGroup(SocialGroup group) throws SQLException {

        if (group.getMemberships() != null) {

            Set<Membership> mems = group.getMemberships();
            for (Membership item : mems)
                service.delete(item);
        }
        service.delete(group);
    }

    public List<Individual> getAllIndividualsOfSocialGroup(SocialGroup group) {

        List<Individual> list = new ArrayList<Individual>();
        List<Membership> mems = genericDao.findListByProperty(Membership.class, "socialGroup", group);

        for (Membership item : mems) {
            if (item.getEndDate() == null && !item.isDeleted())
                list.add(item.getIndividual());
        }
        return list;
    }

    public List<SocialGroup> getAllSocialGroups(Individual individual) {
        List<SocialGroup> list = genericDao.findListByProperty(SocialGroup.class, "groupHead", individual, true);
        return list;
    }

    public SocialGroup getSocialGroupForIndividualByType(Individual individual, String groupType) {
        Set<Membership> memberships = individual.getAllMemberships();

        for (Membership membership : memberships) {
            if (membership.getSocialGroup().getGroupType() == null) {
                return null;
            } else {
                if (membership.getSocialGroup().getGroupType().equals("FAM")) {
                    return membership.getSocialGroup();
                }
            }
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public void modifySocialGroupHead(SocialGroup group, Individual selectedSuccessor, List<Membership> memberships)
            throws ConstraintViolations, SQLException, Exception {

        group.setGroupHead(selectedSuccessor);

        // Remove all Memberships from the Social Group
        Set<Membership> mems = group.getMemberships();
        Iterator<Membership> itr = mems.iterator();

        while (itr.hasNext()) {
            Membership item = itr.next();
            item.setDeleted(true);
            service.save(item);
        }

        // Create new Memberships
        itr = memberships.iterator();
        for (Membership item : memberships)
            service.create(item);

        service.save(group);
    }

    public SocialGroup findSocialGroupById(String socialGroupId, String msg) throws Exception {
        SocialGroup sg = genericDao.findByProperty(SocialGroup.class, "extId", socialGroupId);
        if (sg == null) {
            throw new Exception(msg);
        }
        return sg;
    }

    public SocialGroup findSocialGroupById(String sgExtId) {
        SocialGroup sg = genericDao.findByProperty(SocialGroup.class, "extId", sgExtId);
        return sg;
    }

    @Transactional(readOnly = true)
    public List<SocialGroup> getSocialGroupsPrefixedWith(String substring) {
        return genericDao.findListByPropertyPrefix(SocialGroup.class, "extId", substring, 0, true);
    }
}
