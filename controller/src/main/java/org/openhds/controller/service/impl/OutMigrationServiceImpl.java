package org.openhds.controller.service.impl;

import java.sql.SQLException;
import java.util.List;

import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.controller.service.EntityService;
import org.openhds.controller.service.IndividualService;
import org.openhds.controller.service.OutMigrationService;
import org.openhds.controller.service.ResidencyService;
import org.openhds.dao.service.GenericDao;
import org.openhds.domain.model.Death;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.OutMigration;
import org.openhds.domain.model.Residency;
import org.openhds.domain.service.SitePropertiesService;
import org.springframework.transaction.annotation.Transactional;

public class OutMigrationServiceImpl implements OutMigrationService {

    private ResidencyService residencyService;
    private IndividualService individualService;
    private GenericDao genericDao;
    private SitePropertiesService siteProperties;
    private EntityService entityService;

    public OutMigrationServiceImpl(ResidencyService residencyService, IndividualService individualService,
            GenericDao genericDao, SitePropertiesService siteProperties, EntityService entityService) {
        this.residencyService = residencyService;
        this.individualService = individualService;
        this.genericDao = genericDao;
        this.siteProperties = siteProperties;
        this.entityService = entityService;
    }

    @Transactional(readOnly = true)
    public void evaluateOutMigration(OutMigration outMigration) throws ConstraintViolations {
        if (individualService.getLatestEvent(outMigration.getIndividual()) instanceof Death) {
            throw new ConstraintViolations(
                    "An Out Migration cannot be created for an Individual who has a Death event.");
        }

        // verify the individual has an open residency
        if (!residencyService.hasOpenResidency(outMigration.getIndividual())) {
            throw new ConstraintViolations(
                    "The Individual you entered does not have an open residency. In order to complete an out"
                            + " migration, the Individual must have an open residency.");
        }

        Residency currentResidence = outMigration.getIndividual().getCurrentResidency();

        // verify the date of the out migration is after the residency start date
        if (currentResidence.getStartDate().compareTo(outMigration.getRecordedDate()) > 0) {
            throw new ConstraintViolations(
                    "The Out Migration recorded date is before the start of the individual current residency.");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void editOutMigration(OutMigration entityItem) throws ConstraintViolations, SQLException {
        // its possible that the out migration has become detached from the session
        // this is a safety check to enforce that the out migration is in fact loaded into the session
        // if the out migration is detached, its possible that lazy init exceptions will occur because
        // of the logical constraint checks that occur
        entityItem = genericDao.merge(entityItem);

        entityItem.getResidency().setEndDate(entityItem.getRecordedDate());
        residencyService.evaluateResidency(entityItem.getResidency());
        entityService.save(entityItem.getResidency());
        entityService.save(entityItem);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOutMigration(OutMigration outMigration) throws ConstraintViolations, SQLException {
        // all residencies MUST be closed in order for the data to be in a correct state
        // otherwise, the individual would have 2 open residencies at the same time, which is against
        // the system rules
        for (Residency res : outMigration.getIndividual().getAllResidencies()) {
            if (res.getEndDate() == null) {
                throw new ConstraintViolations(
                        "You cannot delete an out migration for an individual with an open residency."
                                + " If you want to delete this out migration, you can 1) Close the current open residency, "
                                + "or 2) Delete the residency for this out migration using the Residency amendment form.");
            }
        }

        Residency res = outMigration.getResidency();
        res.setEndDate(null);
        res.setEndType(siteProperties.getNotApplicableCode());

        entityService.save(res);
        entityService.delete(outMigration);
    }

    public List<OutMigration> getOutMigrations(Individual individual) {
        return genericDao.findListByProperty(OutMigration.class, "individual", individual, true);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createOutMigration(OutMigration outMigration) throws ConstraintViolations, IllegalArgumentException,
            SQLException {
        Residency currentResidence = outMigration.getIndividual().getCurrentResidency();

        // configure out migration
        outMigration.setResidency(currentResidence);
        currentResidence.setEndType(siteProperties.getOutmigrationCode());
        currentResidence.setEndDate(outMigration.getRecordedDate());

        // run the residency through the residency service which provides additional integrity constraints
        residencyService.evaluateResidency(currentResidence);

        entityService.save(currentResidence);
        entityService.create(outMigration);
    }
}
