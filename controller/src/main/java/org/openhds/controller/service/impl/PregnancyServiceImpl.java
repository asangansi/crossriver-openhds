package org.openhds.controller.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.controller.service.EntityService;
import org.openhds.controller.service.IndividualService;
import org.openhds.controller.service.PregnancyService;
import org.openhds.dao.service.GenericDao;
import org.openhds.dao.service.GenericDao.ValueProperty;
import org.openhds.domain.model.Death;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.Membership;
import org.openhds.domain.model.PregnancyObservation;
import org.openhds.domain.model.PregnancyOutcome;
import org.openhds.domain.model.Residency;
import org.openhds.domain.service.SitePropertiesService;
import org.openhds.domain.util.CalendarUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the pregnancy service interface
 * 
 * @author Dave
 * 
 */
public class PregnancyServiceImpl implements PregnancyService {

    private EntityService entityService;
    private IndividualService individualService;
    private GenericDao genericDao;
    private SitePropertiesService siteProperties;

    public PregnancyServiceImpl(EntityService entityService, IndividualService individualService,
            GenericDao genericDao, SitePropertiesService siteProperties) {
        this.entityService = entityService;
        this.individualService = individualService;
        this.genericDao = genericDao;
        this.siteProperties = siteProperties;
    }

    public PregnancyObservation evaluatePregnancyObservation(PregnancyObservation entityItem)
            throws ConstraintViolations {

        int age = (int) (CalendarUtil.daysBetween(entityItem.getMother().getDob(), entityItem.getRecordedDate()) / 365.25);
        if (age < siteProperties.getMinimumAgeOfPregnancy())
            throw new ConstraintViolations(
                    "The Mother specified is younger than the minimum age required to have a Pregnancy Observation.");
        if (!checkDuplicatePregnancyObservation(entityItem.getMother()))
            throw new ConstraintViolations("The Mother specified already has a pending Pregnancy Observation.");
        if (individualService.getLatestEvent(entityItem.getMother()) instanceof Death)
            throw new ConstraintViolations(
                    "A Pregnancy Observation cannot be created for a Mother who has a Death event.");

        return entityItem;
    }

    public void validateGeneralPregnancyObservation(PregnancyObservation entityItem) throws ConstraintViolations {
        List<PregnancyObservation> list = genericDao.findListByMultiProperty(PregnancyObservation.class,
                getValueProperty("mother", entityItem.getMother()),
                getValueProperty("status", siteProperties.getDataStatusPendingCode()));
        if (list.size() > 1)
            throw new ConstraintViolations("The Mother specified already has a pending Pregnancy Observation.");
    }

    @Transactional(rollbackFor = Exception.class)
    public void closePregnancyObservation(Individual mother) {
        List<PregnancyObservation> obs = genericDao.findListByProperty(PregnancyObservation.class, "mother", mother);

        for (PregnancyObservation ob : obs) {
            if (ob.getStatus().equals(siteProperties.getDataStatusPendingCode())) {
                // found the corresponding pregnancy observation
                // now close it
                ob.setStatus(siteProperties.getDataStatusClosedCode());
                genericDao.update(ob);
                break;
            }
        }
    }

    public boolean checkDuplicatePregnancyObservation(Individual mother) {
        List<PregnancyObservation> list = genericDao.findListByProperty(PregnancyObservation.class, "mother", mother);
        for (PregnancyObservation item : list) {
            if (item.getStatus().equals(siteProperties.getDataStatusPendingCode()))
                return false;
        }
        return true;
    }

    public PregnancyOutcome evaluatePregnancyOutcome(PregnancyOutcome entityItem) throws ConstraintViolations {
        if (individualService.getLatestEvent(entityItem.getMother()) instanceof Death)
            throw new ConstraintViolations("A Pregnancy Outcome cannot be created for a Mother who has a Death event.");
        return entityItem;
    }

    public List<PregnancyOutcome> getPregnancyOutcomesByIndividual(Individual individual) {
        return genericDao.findListByProperty(PregnancyOutcome.class, "mother", individual, true);
    }

    public List<PregnancyObservation> getPregnancyObservationByIndividual(Individual individual) {
        return genericDao.findListByProperty(PregnancyObservation.class, "mother", individual, true);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createPregnancyOutcome(PregnancyOutcome pregOutcome) throws IllegalArgumentException, SQLException,
            ConstraintViolations {
        // the algorithm for completing a pregnancy outcome is as follows:
        // (Please note, this may change in the future)
        // - create the new child
        // - create new residency for child and set the location to the mothers current residency location
        // - create a membership to the mothers social group for which the social group is of type family

        Individual child1 = pregOutcome.getChild1();
        child1.setDob(pregOutcome.getDobChild());
        child1.setMother(pregOutcome.getMother());
        child1.setFather(pregOutcome.getFather());
        child1.setCollectedBy(pregOutcome.getCollectedBy());
        child1.setMaritalStatus(1);
        child1.setEducationalStatus(0);
        child1.setOccupationalStatus(1);
        pregOutcome.setChild1(child1);
        entityService.create(child1);

        Residency residency1 = new Residency();
        residency1.setStartDate(pregOutcome.getRecordedDate());
        residency1.setIndividual(child1);
        residency1.setStartType(siteProperties.getBirthCode());
        residency1.setLocation(pregOutcome.getHouse());
        residency1.setCollectedBy(pregOutcome.getCollectedBy());
        residency1.setEndType(siteProperties.getNotApplicableCode());
        child1.getAllResidencies().add(residency1);
        entityService.create(residency1);

        Membership membership1 = new Membership();
        membership1.setIndividual(child1);
        membership1.setStartDate(child1.getDob());
        membership1.setStartType(siteProperties.getBirthCode());
        membership1.setEndType(siteProperties.getNotApplicableCode());
        membership1.setSocialGroup(pregOutcome.getHousehold());
        membership1.setbIsToA("99");
        membership1.setCollectedBy(pregOutcome.getCollectedBy());
        entityService.create(membership1);

        Individual child2 = pregOutcome.getChild2();

        if (child2 != null) {

            child2.setDob(pregOutcome.getDobChild());
            child2.setMother(pregOutcome.getMother());
            child2.setFather(pregOutcome.getFather());
            child2.setCollectedBy(pregOutcome.getCollectedBy());
            child2.setMaritalStatus(1);
            child2.setEducationalStatus(0);
            child2.setOccupationalStatus(1);
            pregOutcome.setChild2(child2);
            entityService.create(child2);

            Residency residency2 = new Residency();
            residency2.setStartDate(pregOutcome.getRecordedDate());
            residency2.setIndividual(child2);
            residency2.setStartType(siteProperties.getBirthCode());
            residency2.setLocation(pregOutcome.getHouse());
            residency2.setCollectedBy(pregOutcome.getCollectedBy());
            residency2.setEndType(siteProperties.getNotApplicableCode());
            child2.getAllResidencies().add(residency2);
            entityService.create(residency2);

            Membership membership2 = new Membership();
            membership2.setIndividual(child2);
            membership2.setStartDate(child2.getDob());
            membership2.setStartType(siteProperties.getBirthCode());
            membership2.setEndType(siteProperties.getNotApplicableCode());
            membership2.setSocialGroup(pregOutcome.getHousehold());
            membership2.setCollectedBy(pregOutcome.getCollectedBy());
            membership2.setbIsToA("99");
            entityService.create(membership2);
        }

        // close any pregnancy observation
        closePregnancyObservation(pregOutcome.getMother());
        entityService.create(pregOutcome);
    }

    public List<PregnancyOutcome> findAllLiveBirthsBetweenInterval(Calendar startDate, Calendar endDate) {

        List<PregnancyOutcome> output = new ArrayList<PregnancyOutcome>();
        List<PregnancyOutcome> outcomes = genericDao.findAll(PregnancyOutcome.class, true);

        for (PregnancyOutcome outcome : outcomes) {
            Calendar outcomeDate = outcome.getRecordedDate();
            if ((outcomeDate.after(startDate) || outcomeDate.equals(startDate)) && (outcomeDate.before(endDate)))
                output.add(outcome);
        }
        return output;
    }

    public int findAllBirthsBetweenIntervalByGender(Calendar startDate, Calendar endDate, int flag) {

        int count = 0;
        List<PregnancyOutcome> outcomes = genericDao.findAll(PregnancyOutcome.class, true);

        for (PregnancyOutcome outcome : outcomes) {
            Calendar outcomeDate = outcome.getRecordedDate();
            if ((outcomeDate.after(startDate) || outcomeDate.equals(startDate)) && (outcomeDate.before(endDate))) {

                if (flag == 0) {
                    if (outcome.getChild1() != null) {
                        if (outcome.getChild1().getGender().equals(siteProperties.getMaleCode()))
                            count++;
                    }
                    if (outcome.getChild2() != null) {
                        if (outcome.getChild2().getGender().equals(siteProperties.getMaleCode()))
                            count++;
                    }
                } else {
                    if (outcome.getChild1() != null) {
                        if (outcome.getChild1().getGender().equals(siteProperties.getFemaleCode()))
                            count++;
                    }
                    if (outcome.getChild2() != null) {
                        if (outcome.getChild2().getGender().equals(siteProperties.getFemaleCode()))
                            count++;
                    }
                }
            }
        }
        return count;
    }

    private ValueProperty getValueProperty(final String propertyName, final Object propertyValue) {
        return new ValueProperty() {

            public String getPropertyName() {
                return propertyName;
            }

            public Object getValue() {
                return propertyValue;
            }
        };
    }
}