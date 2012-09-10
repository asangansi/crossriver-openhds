package org.openhds.dao.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openhds.domain.model.AuditableCollectedEntity;
import org.openhds.domain.model.Death;
import org.openhds.domain.model.InMigration;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.Location;
import org.openhds.domain.model.OutMigration;
import org.openhds.domain.model.PregnancyOutcome;
import org.openhds.domain.model.Residency;
import org.openhds.domain.service.SitePropertiesService;

public class ResidencyDaoImpl extends BaseDaoImpl<Residency, String> implements ResidencyDao {
    private static final Logger logger = Logger.getLogger(ResidencyDaoImpl.class);
    private SitePropertiesService siteProperties;

    public ResidencyDaoImpl(Class<Residency> entityType, SitePropertiesService siteProperties) {
        super(entityType);
        this.siteProperties = siteProperties;
    }

    @Override
    public List<Residency> getAllResidencies(Individual individual) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<AuditableCollectedEntity> getResidencyAssociatedEvents(Residency residency) {
        boolean isEnumerationResidency = residency.getStartType().equals(siteProperties.getEnumerationCode());
        List<AuditableCollectedEntity> events = new ArrayList<AuditableCollectedEntity>();

        if (isEnumerationResidency && residency.getEndType() == null) {
            return events;
        }

        if (residency.getEndType() != null) {
            if (residency.getEndType().equals(siteProperties.getDeathCode())) {
                Criteria criteria = criteria(Death.class);
                criteria.add(Restrictions.eq("individual", residency.getIndividual()));

                AuditableCollectedEntity ae = (AuditableCollectedEntity) criteria.uniqueResult();
                if (ae == null) {
                    logger.warn("Residency has end type has death code, but does not have a corresponding death event entity");
                } else {
                    events.add(ae);
                }
            }

            if (residency.getEndType().equals(siteProperties.getOutmigrationCode())) {
                Criteria criteria = criteria(OutMigration.class);
                criteria.add(Restrictions.eq("individual", residency.getIndividual()));
                criteria.add(Restrictions.eq("recordedDate", residency.getEndDate()));
                AuditableCollectedEntity ae = (AuditableCollectedEntity) criteria.uniqueResult();
                if (ae == null) {
                    logger.warn("Residency has end type has outmigration code, but does not have a corresponding outmigration event entity");
                } else {
                    events.add(ae);
                }
            }
        }

        if (isEnumerationResidency) {
            return events;
        }

        if (residency.getStartType().equals(siteProperties.getInmigrationCode())) {
            Criteria criteria = criteria(InMigration.class);
            criteria.add(Restrictions.eq("individual", residency.getIndividual()));
            criteria.add(Restrictions.eq("recordedDate", residency.getStartDate()));
            AuditableCollectedEntity ae = (AuditableCollectedEntity) criteria.uniqueResult();
            if (ae == null) {
                logger.warn("Residency has start type has in migration code, but does not have a corresponding in migration event entity");
            } else {
                events.add(ae);
            }
        }

        if (residency.getStartType().equals(siteProperties.getBirthCode())) {
            Criteria criteria = criteria(PregnancyOutcome.class);
            criteria.add(Restrictions.eq("child1", residency.getIndividual()));
            criteria.add(Restrictions.eq("recordedDate", residency.getStartDate()));
            AuditableCollectedEntity ae = (AuditableCollectedEntity) criteria.uniqueResult();
            if (ae == null) {
                // try the second child
                criteria = criteria(PregnancyOutcome.class);
                criteria.add(Restrictions.eq("child2", residency.getIndividual()));
                criteria.add(Restrictions.eq("recordedDate", residency.getStartDate()));
                ae = (AuditableCollectedEntity) criteria.uniqueResult();
            }

            if (ae == null) {
                logger.warn("Residency has start type has birth code, but does not have a corresponding pregnancy outcome event entity");
            } else {
                events.add(ae);
            }
        }

        return events;
    }

    @Override
    public List<Individual> getIndividualsByLocation(Location location, boolean includeEndedResidency) {
        // TODO Auto-generated method stub
        return null;
    }

}
