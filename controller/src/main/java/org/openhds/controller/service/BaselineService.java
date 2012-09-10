package org.openhds.controller.service;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.domain.annotations.Authorized;
import org.openhds.domain.model.AuditableCollectedEntity;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.Location;
import org.openhds.domain.model.Membership;
import org.openhds.domain.model.PrivilegeConstants;
import org.openhds.domain.model.Relationship;
import org.openhds.domain.model.Residency;
import org.openhds.domain.model.SocialGroup;

public interface BaselineService {

    @Authorized({ PrivilegeConstants.CREATE_ENTITY })
    public void createResidencyAndMembershipForIndividual(Individual individual, Membership membership,
            Location currentLocation, FieldWorker collectedBy, Calendar startDate) throws SQLException,
            ConstraintViolations, IllegalArgumentException, ConstraintViolations;

    @Authorized({ PrivilegeConstants.CREATE_ENTITY })
    public void createSocialGroupAndResidencyForIndividual(Individual individual, SocialGroup socialGroup,
            Location currentLocation, FieldWorker collectedBy, Calendar startDate) throws SQLException,
            ConstraintViolations, IllegalArgumentException, ConstraintViolations;

    @Authorized({ PrivilegeConstants.CREATE_ENTITY })
    public void registerHouseholdMember(Individual individual, Residency residency, Membership membership)
            throws IllegalArgumentException, ConstraintViolations, SQLException;

    @Authorized({ PrivilegeConstants.CREATE_ENTITY })
    public void registerHouseholdMemberWithRelationship(Individual individual, Residency residency,
            Membership membership, Relationship relationship) throws IllegalArgumentException, ConstraintViolations,
            SQLException;

    @Authorized({ PrivilegeConstants.CREATE_ENTITY })
    public void createEntities(List<AuditableCollectedEntity> list) throws IllegalArgumentException,
            ConstraintViolations, SQLException;
}
