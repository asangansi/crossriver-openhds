package org.openhds.controller.service.impl;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openhds.dao.service.GenericDao;
import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.controller.service.EntityService;
import org.openhds.controller.service.InMigrationService;
import org.openhds.controller.service.IndividualService;
import org.openhds.controller.service.MembershipService;
import org.openhds.controller.service.ResidencyService;
import org.openhds.domain.model.InMigration;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.Membership;
import org.openhds.domain.model.MigrationType;
import org.openhds.domain.model.Residency;
import org.openhds.domain.service.SitePropertiesService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the InMigraiton Service
 * 
 * @author Dave
 *
 */
public class InMigrationServiceImpl implements InMigrationService {
	
	private static Logger log = Logger.getLogger(InMigrationServiceImpl.class);
	
	private MembershipService membershipService;
	private ResidencyService residencyService;
	private EntityService entityService;
	private IndividualService individualService;
	private GenericDao genericDao; 
	private SitePropertiesService siteProperties;
	
	public InMigrationServiceImpl(ResidencyService residencyService, EntityService entityService, IndividualService individualService, MembershipService membershipService, GenericDao genericDao, SitePropertiesService siteProperties) {
		this.residencyService = residencyService;
		this.entityService = entityService;
		this.individualService = individualService;
		this.membershipService = membershipService;
		this.genericDao = genericDao;
		this.siteProperties = siteProperties;
	}

	public InMigration evaluateInMigration(InMigration inMigration) throws ConstraintViolations {
		if (inMigration.getEverRegistered().equals("1") && !inMigration.isReferencesTemporaryIndividual()) {
			// an internal in migration with a known id
			// an individual MUST exist in this situation
			Individual indiv = individualService.findIndivById(inMigration.getIndividual().getExtId());
			if (indiv == null) {
				throw new ConstraintViolations("Could not find an Individual with Permanent Id: " + inMigration.getIndividual().getExtId());
			}
			checkIfIndividualIsDeceased(indiv);
			checkIfIndividualHasOpenResidency(indiv);
		}

		if (inMigration.getEverRegistered().equals("2") && !inMigration.isReferencesTemporaryIndividual()) {
			// an external in migration with a known id
			// in this case, the permanent id needs to verified its not in use
			// this is actually complicated by the fact that a temp individual 
			// may be created if a new household is registered. The only real
			// way to distinguish between them is by looking at the gender
			Individual indiv = individualService.findIndivById(inMigration.getIndividual().getExtId());
			if (indiv != null && !indiv.getGender().equals(siteProperties.getUnknownIdentifier())) {
				throw new ConstraintViolations("The Permanent Id: " + indiv.getExtId() + " is already in use");
			}
		}
		
		if (inMigration.getBIsToA() != null && inMigration.getBIsToA().equals("01")) {
			Individual groupHead = inMigration.getHousehold().getGroupHead();
			if (!groupHead.getGender().equals(siteProperties.getUnknownIdentifier())) {
				throw new ConstraintViolations("You cannot use the head of house relationship code because the Household already has a head of house");
			}
		}
		
		return inMigration;
	}

	private void checkIfIndividualIsDeceased(Individual indiv) throws ConstraintViolations {
		if (individualService.getLatestEvent(indiv).equals("Death")) {
    		throw new ConstraintViolations("An In Migration cannot be created for an Individual who has a Death event.");	
    	}
	}

	private void checkIfIndividualHasOpenResidency(Individual indiv) throws ConstraintViolations {
		if (residencyService.hasOpenResidency(indiv)) {
			throw new ConstraintViolations("The individual for this in migration has an open residency. Please close the residency before you create an in migration");
		}
	}
	
	public InMigration evaluateInMigrationOnEdit(InMigration inMigration) throws ConstraintViolations, Exception {
		checkIfResidencyIsClosed(inMigration.getResidency());
		setResidencyFieldsFromInMigration(inMigration);
		
		// not necessary for editing
		//checkIfResidencyIsValid(inMigration.getResidency());
		
		return inMigration;
	}

	private void checkIfResidencyIsClosed(Residency residency) throws ConstraintViolations {
		if (residency.getEndDate() != null) {
			throw new ConstraintViolations("You cannot make changes to an in migration whose residency has been closed.");
		}
	}

	private void checkIfResidencyIsValid(Residency residency) throws ConstraintViolations {
		entityService.validateEntity(residency);
		residencyService.evaluateResidency(residency);
	}
		
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void createInMigration(InMigration inMigration) throws ConstraintViolations, SQLException, Exception {
		if (inMigration.getEverRegistered().equals("2")) {
			inMigration.setMigTypeExternal();
		} else {
			inMigration.setMigTypeInternal();
		}
		
		String extId = inMigration.getIndividual().getExtId();
		Individual persistedIndividual = individualService.findIndivById(extId);
		
		if (persistedIndividual != null) {
			// an individual would have already been persisted if:
			// already registered in HDS
			// a temp individual was created when registering a new household
			inMigration.setIndividual(persistedIndividual);
		} 

		if (inMigration.isReferencesTemporaryIndividual() || inMigration.getEverRegistered().equals("2")) {
			individualService.validateIdLength(inMigration.getIndividual());
			setIndividualFields(inMigration);
		} 

		if (StringUtils.isNotBlank(inMigration.getBIsToA())) {
			createMembership(inMigration);		
		}
		setResidencyFieldsFromInMigration(inMigration);
		checkIfResidencyIsValid(inMigration.getResidency());
		inMigration.getIndividual().getAllResidencies().add(inMigration.getResidency());

		entityService.create(inMigration.getResidency());
		entityService.save(inMigration.getIndividual());
		entityService.create(inMigration);
	}
	
	private void createMembership(InMigration migration) throws ConstraintViolations, IllegalArgumentException, SQLException {
		Membership membership = new Membership();
		membership.setbIsToA(migration.getBIsToA());
		membership.setSocialGroup(migration.getHousehold());
		membership.setIndividual(migration.getIndividual());
		membership.setCollectedBy(migration.getCollectedBy());
		membership.setStartDate(migration.getRecordedDate());
		membership.setStartType(siteProperties.getInmigrationCode());
		membership.setEndType(siteProperties.getNotApplicableCode());
			
		membershipService.evaluateMembership(membership);
		entityService.create(membership);
	}
	
	private void setIndividualFields(InMigration migration) throws IllegalArgumentException, SQLException, ConstraintViolations {
		Individual individual = migration.getIndividual();
		individual.setCollectedBy(migration.getCollectedBy());
		
		// since this individual is being registered in the system
		// they should not have any residencies. But, during the creation
		// of an in migration, a residency is set on the individual. If the overall
		// transaction of creating an in migration fails, then residency will still
		// be in the current set of residencies for the individual.
		// If you do not clear, Hibernate will throw exceptions
		individual.getAllResidencies().clear();
		entityService.create(individual);
	}
	
	private void setResidencyFieldsFromInMigration(InMigration migration) {
		Residency residency = migration.getResidency();
        residency.setIndividual(migration.getIndividual());
        residency.setStartDate(migration.getRecordedDate());
        residency.setStartType(siteProperties.getInmigrationCode());
        residency.setCollectedBy(migration.getCollectedBy());
        residency.setEndType(siteProperties.getNotApplicableCode());
        residency.setLocation(migration.getVisit().getVisitLocation());
	}
	
	public boolean generateIdForMigrant(InMigration migration) {
		migration.getIndividual().setExtId(migration.getHousehold().getExtId());
		
		try {
			migration.getIndividual().setExtId(individualService.generateIdWithBound(migration.getIndividual(), 1));
		} catch (ConstraintViolations e) {
			if (log.isDebugEnabled()) {
				log.debug("Could not set the external id for the individual", e);
			}
			return false;
		}		
		
		return true;
	}
	
	public List<InMigration> getInMigrationsByIndividual(Individual individual) {
		return genericDao.findListByProperty(InMigration.class, "individual", individual, true);
	}
}
