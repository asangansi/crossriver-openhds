package org.openhds.dao.service.impl;

import java.util.List;

import org.openhds.dao.service.Dao;
import org.openhds.domain.annotations.Authorized;
import org.openhds.domain.model.AuditableCollectedEntity;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.Location;
import org.openhds.domain.model.PrivilegeConstants;
import org.openhds.domain.model.Residency;

public interface ResidencyDao extends Dao<Residency, String> {

    List<Residency> getAllResidencies(Individual individual);

    /**
     * Return the associated events for a given residency (e.g. Pregnancy outcome, in migration, out migration, death).
     * 
     * @return the associated events for this residency
     */
    List<AuditableCollectedEntity> getResidencyAssociatedEvents(Residency residency);

    /**
     * Retrieve a list of individual who have their last known residency (current residency) for a given location
     * 
     * @param location
     *            The location to look for residencies at
     * @return a list of individuals who have a current residency at the location
     */
    @Authorized({ PrivilegeConstants.VIEW_ENTITY, PrivilegeConstants.ACCESS_UPDATE, PrivilegeConstants.ACCESS_BASELINE })
    List<Individual> getIndividualsByLocation(Location location, boolean includeEndedResidency);
}
