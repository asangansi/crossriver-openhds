package org.openhds.controller.service.impl;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.controller.service.BaselineService;
import org.openhds.controller.service.EntityService;
import org.openhds.domain.model.AuditableCollectedEntity;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.Location;
import org.openhds.domain.model.Membership;
import org.openhds.domain.model.Relationship;
import org.openhds.domain.model.Residency;
import org.openhds.domain.model.SocialGroup;
import org.openhds.domain.service.SitePropertiesService;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor=Exception.class)
public class BaselineServiceImpl implements BaselineService {

	private EntityService entityService;
	private SitePropertiesService siteProperties;
	
	public BaselineServiceImpl(EntityService entityService, SitePropertiesService siteProperties) {
		this.entityService = entityService;
		this.siteProperties = siteProperties;
	}

	public void createResidencyAndMembershipForIndividual(Individual individual, Membership membership, Location currentLocation, FieldWorker collectedBy, Calendar startDate) throws SQLException, ConstraintViolations, IllegalArgumentException {
	
		 Residency residency = createResidency(individual, currentLocation, collectedBy, startDate);
	     individual.setInsertDate(Calendar.getInstance());
	     membership.setInsertDate(Calendar.getInstance());
	     individual.getAllResidencies().add(residency);
	     
	     entityService.create(individual);
	     entityService.create(membership);
	     entityService.create(residency);
	}

	private Residency createResidency(Individual individual, Location currentLocation, FieldWorker collectedBy, Calendar startDate) {
		Residency residency = new Residency();
	     residency.setIndividual(individual);
	     residency.setLocation(currentLocation);
	     residency.setStartType(siteProperties.getEnumerationCode());
	     residency.setEndType(siteProperties.getNotApplicableCode());
	     residency.setStartDate(startDate);
	     residency.setCollectedBy(collectedBy);
	     residency.setInsertDate(Calendar.getInstance());
		return residency;
	}
	
	public void createSocialGroupAndResidencyForIndividual(Individual individual, SocialGroup socialGroup, Location currentLocation, FieldWorker collectedBy, Calendar startDate) throws SQLException, ConstraintViolations, IllegalArgumentException {
		
		Residency residency = createResidency(individual, currentLocation, collectedBy, startDate);
	    individual.setInsertDate(Calendar.getInstance());
	    socialGroup.setInsertDate(Calendar.getInstance());
	    individual.getAllResidencies().add(residency);
		
	    entityService.create(individual);
	    entityService.create(socialGroup);
	    entityService.create(residency);
	}
		
	public void createEntities(List<AuditableCollectedEntity> list) throws IllegalArgumentException, ConstraintViolations, SQLException {
		for (AuditableCollectedEntity entity : list) {
			entityService.create(entity);
		}
	}
		
	public void registerHouseholdMember(Individual individual, Residency residency, Membership membership) throws IllegalArgumentException, ConstraintViolations, SQLException {
		entityService.create(individual);
		entityService.create(residency);
		entityService.create(membership);
	}

	public void registerHouseholdMemberWithRelationship(Individual individual, Residency residency,
			Membership membership, Relationship relationship) throws IllegalArgumentException, ConstraintViolations,
			SQLException {
		registerHouseholdMember(individual, residency, membership);
		entityService.create(relationship);
	}
}
