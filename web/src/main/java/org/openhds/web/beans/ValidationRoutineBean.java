package org.openhds.web.beans;

import java.sql.SQLException;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.openhds.controller.service.EntityValidationService;
import org.openhds.dao.service.GenericDao;
import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.controller.idgeneration.IndividualGenerator;
import org.openhds.controller.idgeneration.LocationGenerator;
import org.openhds.controller.idgeneration.SocialGroupGenerator;
import org.openhds.controller.idgeneration.VisitGenerator;
import org.openhds.domain.model.AuditableCollectedEntity;
import org.openhds.domain.model.Death;
import org.openhds.domain.model.InMigration;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.Location;
import org.openhds.domain.model.Membership;
import org.openhds.domain.model.OutMigration;
import org.openhds.domain.model.PregnancyObservation;
import org.openhds.domain.model.PregnancyOutcome;
import org.openhds.domain.model.Relationship;
import org.openhds.domain.model.Residency;
import org.openhds.domain.model.SocialGroup;
import org.openhds.domain.model.Visit;
import org.openhds.controller.service.DeathService;
import org.openhds.controller.service.InMigrationService;
import org.openhds.controller.service.IndividualService;
import org.openhds.controller.service.LocationHierarchyService;
import org.openhds.controller.service.MembershipService;
import org.openhds.controller.service.OutMigrationService;
import org.openhds.controller.service.PregnancyService;
import org.openhds.controller.service.RelationshipService;
import org.openhds.controller.service.ResidencyService;
import org.openhds.controller.service.SocialGroupService;
import org.openhds.controller.service.VisitService;
import org.openhds.domain.service.SitePropertiesService;

public class ValidationRoutineBean {
	
	private IndividualService individualService;
	private LocationHierarchyService locationService;
	private SocialGroupService socialgroupService;
	private RelationshipService relationshipService;
	private MembershipService membershipService;
	private PregnancyService pregnancyService;
	private VisitService visitService;
	private DeathService deathService;
	private InMigrationService migrationService;
	private OutMigrationService outmigrationService;
	private InMigrationService inmigrationService;
	private ResidencyService residencyService;
	private GenericDao genericDao;
	private EntityValidationService entityValidator;
	private SitePropertiesService properties;
	private IndividualGenerator indivGenerator;
	private LocationGenerator locGenerator;
	private SocialGroupGenerator sgGenerator;
	private VisitGenerator visitGenerator;
	
	private String selectedEntity;
	
	public void entityChanged(ValueChangeEvent e) {
		selectedEntity = e.getNewValue().toString();
	}
		
	@SuppressWarnings("unchecked")
	public void validateIndividuals() throws ConstraintViolations, SQLException {
		List<Individual> indivs = genericDao.findAll(Individual.class, false);
		for (Individual i : indivs) {
			
			if (i.getStatus() == null || i.getStatus().equals(properties.getDataStatusPendingCode())) {
				if (processDeletedEntity(i))
					continue;
				
				if (!i.getExtId().equals(properties.getUnknownIdentifier())) {
					
					try {
						List<String> violations = entityValidator.validateType(i);
						
						individualService.validateGeneralIndividual(i);
						if (indivGenerator.isGenerated())
							indivGenerator.validateIdLength(i.getExtId(), indivGenerator.getIdScheme());
		
						if (violations.size() > 0) {
							i.setStatus(properties.getDataStatusFatalCode());
							i.setStatusMessage(violations.get(0));
							genericDao.update(i);
						}	
						else {
							i.setStatus(properties.getDataStatusValidCode());
							i.setStatusMessage("");
							genericDao.update(i);
						}
					}
					catch (Exception e) {
						i.setStatus(properties.getDataStatusFatalCode());
						i.setStatusMessage(e.getMessage());
						genericDao.update(i);
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void validateLocations() throws ConstraintViolations, SQLException {
		List<Location> locs = genericDao.findAll(Location.class, false);
		for (Location loc : locs) {		
			if (loc.getStatus() == null || loc.getStatus().equals(properties.getDataStatusPendingCode())) {
				if (processDeletedEntity(loc))
					continue;
				
				try {
					List<String> violations = entityValidator.validateType(loc);
					
					if (violations.size() > 0) {
						loc.setStatus(properties.getDataStatusFatalCode());
						loc.setStatusMessage(violations.get(0));
						genericDao.update(loc);					}	
					else {
						loc.setStatus(properties.getDataStatusValidCode());
						loc.setStatusMessage("");
						genericDao.update(loc);
					}
				}
				catch (Exception e) {
					loc.setStatus(properties.getDataStatusFatalCode());
					loc.setStatusMessage(e.getMessage());
					genericDao.update(loc);
				}
			}
		}
	}
		
	@SuppressWarnings("unchecked")
	public void validateSocialGroups() {
		List<SocialGroup> sgs = genericDao.findAll(SocialGroup.class, false);
		for (SocialGroup sg : sgs) {
			if (sg.getStatus() == null || sg.getStatus().equals(properties.getDataStatusPendingCode())) {
				if (processDeletedEntity(sg))
					continue;
				
				try {
					List<String> violations = entityValidator.validateType(sg);
	
					if (violations.size() > 0) {
						sg.setStatus(properties.getDataStatusFatalCode());
						sg.setStatusMessage(violations.get(0));
						genericDao.update(sg);
					}	
					else {
						sg.setStatus(properties.getDataStatusValidCode());
						sg.setStatusMessage("");
						genericDao.update(sg);
					}
				}
				catch (Exception e) {
					sg.setStatus(properties.getDataStatusFatalCode());
					sg.setStatusMessage(e.getMessage());
					genericDao.update(sg);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void validateRelationships() {
		List<Relationship> rels = genericDao.findAll(Relationship.class, false);
		for (Relationship rel : rels) {
			if (rel.getStatus() == null || rel.getStatus().equals(properties.getDataStatusPendingCode())) {
			
				if (processDeletedEntity(rel))
					continue;
				
				try {
					List<String> violations = entityValidator.validateType(rel);
					relationshipService.validateGeneralRelationship(rel);
					
					if (violations.size() > 0) {
						rel.setStatus(properties.getDataStatusFatalCode());
						rel.setStatusMessage(violations.get(0));
						genericDao.update(rel);
					}	
					else {
						rel.setStatus(properties.getDataStatusValidCode());
						rel.setStatusMessage("");
						genericDao.update(rel);
					}		
				} 
				catch (Exception e) { 
					rel.setStatus(properties.getDataStatusFatalCode());
					rel.setStatusMessage(e.getMessage());
					genericDao.update(rel);
				}	
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void validateMemberships() {
		List<Membership> mems = genericDao.findAll(Membership.class, false);
		for (Membership mem : mems) {
			if (mem.getStatus() == null || mem.getStatus().equals(properties.getDataStatusPendingCode())) {
			
				if (processDeletedEntity(mem))
					continue;
				
				try {
					List<String> violations = entityValidator.validateType(mem);
	
					if (violations.size() > 0) {
						mem.setStatus(properties.getDataStatusFatalCode());
						mem.setStatusMessage(violations.get(0));
						genericDao.update(mem);
					}	
					else {
						mem.setStatus(properties.getDataStatusValidCode());
						mem.setStatusMessage("");
						genericDao.update(mem);
					}
				}
				catch (Exception e) {
					mem.setStatus(properties.getDataStatusFatalCode());
					mem.setStatusMessage(e.getMessage());
					genericDao.update(mem);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void validatePregnancyOutcomes() {
		List<PregnancyOutcome> pos = genericDao.findAll(PregnancyOutcome.class, false);
		for (PregnancyOutcome po : pos) {
			if (po.getStatus() == null || po.getStatus().equals(properties.getDataStatusPendingCode())) {
			
				if (processDeletedEntity(po))
					continue;
				
				try {
					List<String> violations = entityValidator.validateType(po);	
					individualService.validateGeneralIndividual(po.getMother());
					
					if (violations.size() > 0) {
						po.setStatus(properties.getDataStatusFatalCode());
						po.setStatusMessage(violations.get(0));
						genericDao.update(po);
					}	
					else {
						po.setStatus(properties.getDataStatusValidCode());
						po.setStatusMessage("");
						genericDao.update(po);
					}	
				} 
				catch (Exception e) { 
					po.setStatus(properties.getDataStatusFatalCode());
					po.setStatusMessage(e.getMessage());
					genericDao.update(po);
				}		
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void validatePregnancyObservations() {
		List<PregnancyObservation> pos = genericDao.findAll(PregnancyObservation.class, false);
		for (PregnancyObservation po : pos) {
			if (po.getStatus() == null || po.getStatus().equals(properties.getDataStatusPendingCode())) {
				if (processDeletedEntity(po))
					continue;
				
				try {
					List<String> violations = entityValidator.validateType(po);
					individualService.validateGeneralIndividual(po.getMother());
					pregnancyService.validateGeneralPregnancyObservation(po);
					
					if (violations.size() > 0) {
						po.setStatus(properties.getDataStatusFatalCode());
						po.setStatusMessage(violations.get(0));
						genericDao.update(po);
					}	
					else {
						po.setStatus(properties.getDataStatusValidCode());
						po.setStatusMessage("");
						genericDao.update(po);
					}	
				} 
				catch (Exception e) { 
					po.setStatus(properties.getDataStatusFatalCode());
					po.setStatusMessage(e.getMessage());
					genericDao.update(po);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void validateInMigrations() {
		List<InMigration> inmigs = genericDao.findAll(InMigration.class, false);
		for (InMigration inmig : inmigs) {
			if (inmig.getStatus() == null || inmig.getStatus().equals(properties.getDataStatusPendingCode())) {
				if (processDeletedEntity(inmig))
					continue;
				
				try {
					List<String> violations = entityValidator.validateType(inmig);
					individualService.validateGeneralIndividual(inmig.getIndividual());
					
					if (violations.size() > 0) {
						inmig.setStatus(properties.getDataStatusFatalCode());
						inmig.setStatusMessage(violations.get(0));
						genericDao.update(inmig);
					}	
					else {
						inmig.setStatus(properties.getDataStatusValidCode());
						inmig.setStatusMessage("");
						genericDao.update(inmig);
					}	
				} 
				catch (Exception e) { 
					inmig.setStatus(properties.getDataStatusFatalCode());
					inmig.setStatusMessage(e.getMessage());
					genericDao.update(inmig);
				}	
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void validateOutMigrations() {
		List<OutMigration> outmigs = genericDao.findAll(OutMigration.class, false);
		for (OutMigration outmig : outmigs) {
			if (outmig.getStatus() == null || outmig.getStatus().equals(properties.getDataStatusPendingCode())) {
				if (processDeletedEntity(outmig))
					continue;
				
				try {
					List<String> violations = entityValidator.validateType(outmig);
					individualService.validateGeneralIndividual(outmig.getIndividual());
					
					if (violations.size() > 0) {
						outmig.setStatus(properties.getDataStatusFatalCode());
						outmig.setStatusMessage(violations.get(0));
						genericDao.update(outmig);
					}	
					else {
						outmig.setStatus(properties.getDataStatusValidCode());
						outmig.setStatusMessage("");
						genericDao.update(outmig);
					}	
				} 
				catch (Exception e) { 
					outmig.setStatus(properties.getDataStatusFatalCode());
					outmig.setStatusMessage(e.getMessage());
					genericDao.update(outmig);
				}	
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void validateResidencies() {
		List<Residency> residencies = genericDao.findAll(Residency.class, false);
		for (Residency res : residencies) {
			if (res.getStatus() == null || res.getStatus().equals(properties.getDataStatusPendingCode())) {
				if (processDeletedEntity(res))
					continue;
				try {
					List<String> violations = entityValidator.validateType(res);
						
					if (violations.size() > 0) {
						res.setStatus(properties.getDataStatusFatalCode());
						res.setStatusMessage(violations.get(0));
						genericDao.update(res);
					}	
					else {
						res.setStatus(properties.getDataStatusValidCode());
						res.setStatusMessage("");
						genericDao.update(res);
					}	
				} 
				catch (Exception e) { 
					res.setStatus(properties.getDataStatusFatalCode());
					res.setStatusMessage(e.getMessage());
					genericDao.update(res);
				}		
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void validateVisits() {

		List<Visit> visits = genericDao.findAll(Visit.class, false);
		for (Visit visit : visits) {	
			if (visit.getStatus() == null || visit.getStatus().equals(properties.getDataStatusPendingCode())) {
				if (processDeletedEntity(visit))
					continue;
				
				try {
					List<String> violations = entityValidator.validateType(visit);
						
					if (violations.size() > 0) {
						visit.setStatus(properties.getDataStatusFatalCode());
						visit.setStatusMessage(violations.get(0));
						genericDao.update(visit);
					}	
					else {
						visit.setStatus(properties.getDataStatusValidCode());
						visit.setStatusMessage("");
						genericDao.update(visit);
					}
				}
				catch (Exception e) {
					visit.setStatus(properties.getDataStatusFatalCode());
					visit.setStatusMessage(e.getMessage());
					genericDao.update(visit);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void validateDeaths() {
		List<Death> deaths = genericDao.findAll(Death.class, false);
		for (Death death : deaths) {
			if (death.getStatus() == null || death.getStatus().equals(properties.getDataStatusPendingCode())) {
				processDeletedEntity(death);	
				try {
					List<String> violations = entityValidator.validateType(death);
					individualService.validateGeneralIndividual(death.getIndividual());
					
					if (violations.size() > 0) {
						death.setStatus(properties.getDataStatusFatalCode());
						death.setStatusMessage(violations.get(0));
						genericDao.update(death);
					}	
					else {
						death.setStatus(properties.getDataStatusValidCode());
						death.setStatusMessage("");
						genericDao.update(death);
					}	
				} 
				catch (Exception e) { 
					death.setStatus(properties.getDataStatusFatalCode());
					death.setStatusMessage(e.getMessage());
					genericDao.update(death);
				}	
			}
		}
	}
	
	

	private boolean processDeletedEntity(AuditableCollectedEntity entity) {
		if (entity.isDeleted()) {
			entity.setStatus(properties.getDataStatusClosedCode());
			entity.setStatusMessage("");
			genericDao.update(entity);
			return true;
		}
		return false;
	}
	
	public List<Individual> getIndividuals() {
		return genericDao.findListByProperty(Individual.class, "status", properties.getDataStatusFatalCode());
	}
	
	public List<Location> getLocations() {
		return genericDao.findListByProperty(Location.class, "status", properties.getDataStatusFatalCode());
	}
	
	public List<SocialGroup> getSocialGroups() {
		return genericDao.findListByProperty(SocialGroup.class, "status", properties.getDataStatusFatalCode());
	}
	
	public List<Relationship> getRelationships() {
		return genericDao.findListByProperty(Relationship.class, "status", properties.getDataStatusFatalCode());
	}
	
	public List<Membership> getMemberships() {
		return genericDao.findListByProperty(Membership.class, "status", properties.getDataStatusFatalCode());
	}
	
	public List<PregnancyOutcome> getPregnancyOutcomes() {
		return genericDao.findListByProperty(PregnancyOutcome.class, "status", properties.getDataStatusFatalCode());
	}
	
	public List<PregnancyObservation> getPregnancyObservations() {
		return genericDao.findListByProperty(PregnancyObservation.class, "status", properties.getDataStatusFatalCode());
	}
	
	public List<InMigration> getInMigrations() {
		return genericDao.findListByProperty(InMigration.class, "status", properties.getDataStatusFatalCode());
	}
	
	public List<OutMigration> getOutMigrations() {
		return genericDao.findListByProperty(OutMigration.class, "status", properties.getDataStatusFatalCode());
	}
	
	public List<Residency> getResidencies() {
		return genericDao.findListByProperty(Residency.class, "status", properties.getDataStatusFatalCode());
	}
	
	public List<Visit> getVisits() {
		return genericDao.findListByProperty(Visit.class, "status", properties.getDataStatusFatalCode());
	}
	
	public List<Death> getDeaths() {
		return genericDao.findListByProperty(Death.class, "status", properties.getDataStatusFatalCode());
	}
	
	public IndividualService getIndividualService() {
		return individualService;
	}

	public void setIndividualService(IndividualService individualService) {
		this.individualService = individualService;
	}

	public LocationHierarchyService getLocationService() {
		return locationService;
	}

	public void setLocationService(LocationHierarchyService locationService) {
		this.locationService = locationService;
	}

	public SocialGroupService getSocialgroupService() {
		return socialgroupService;
	}

	public void setSocialgroupService(SocialGroupService socialgroupService) {
		this.socialgroupService = socialgroupService;
	}

	public RelationshipService getRelationshipService() {
		return relationshipService;
	}

	public void setRelationshipService(RelationshipService relationshipService) {
		this.relationshipService = relationshipService;
	}

	public MembershipService getMembershipService() {
		return membershipService;
	}

	public void setMembershipService(MembershipService membershipService) {
		this.membershipService = membershipService;
	}

	public PregnancyService getPregnancyService() {
		return pregnancyService;
	}

	public void setPregnancyService(PregnancyService pregnancyService) {
		this.pregnancyService = pregnancyService;
	}

	public VisitService getVisitService() {
		return visitService;
	}

	public void setVisitService(VisitService visitService) {
		this.visitService = visitService;
	}

	public DeathService getDeathService() {
		return deathService;
	}

	public void setDeathService(DeathService deathService) {
		this.deathService = deathService;
	}

	public InMigrationService getMigrationService() {
		return migrationService;
	}

	public void setMigrationService(InMigrationService migrationService) {
		this.migrationService = migrationService;
	}

	public OutMigrationService getOutmigrationService() {
		return outmigrationService;
	}

	public void setOutmigrationService(OutMigrationService outmigrationService) {
		this.outmigrationService = outmigrationService;
	}
	
	public InMigrationService getInmigrationService() {
		return inmigrationService;
	}

	public void setInmigrationService(InMigrationService inmigrationService) {
		this.inmigrationService = inmigrationService;
	}

	public ResidencyService getResidencyService() {
		return residencyService;
	}

	public void setResidencyService(ResidencyService residencyService) {
		this.residencyService = residencyService;
	}

	public GenericDao getGenericDao() {
		return genericDao;
	}

	public void setGenericDao(GenericDao genericDao) {
		this.genericDao = genericDao;
	}
		
	
	public SitePropertiesService getProperties() {
		return properties;
	}

	public void setProperties(SitePropertiesService properties) {
		this.properties = properties;
	}
			
	@SuppressWarnings("unchecked")
	public EntityValidationService getEntityValidator() {
		return entityValidator;
	}

	@SuppressWarnings("unchecked")
	public void setEntityValidator(EntityValidationService entityValidator) {
		this.entityValidator = entityValidator;
	}
	
	public IndividualGenerator getIndivGenerator() {
		return indivGenerator;
	}

	public void setIndivGenerator(IndividualGenerator indivGenerator) {
		this.indivGenerator = indivGenerator;
	}

	public LocationGenerator getLocGenerator() {
		return locGenerator;
	}

	public void setLocGenerator(LocationGenerator locGenerator) {
		this.locGenerator = locGenerator;
	}
	
	@SuppressWarnings("unchecked")
	public SocialGroupGenerator getSgGenerator() {
		return sgGenerator;
	}

	@SuppressWarnings("unchecked")
	public void setSgGenerator(SocialGroupGenerator sgGenerator) {
		this.sgGenerator = sgGenerator;
	}

	public VisitGenerator getVisitGenerator() {
		return visitGenerator;
	}

	public void setVisitGenerator(VisitGenerator visitGenerator) {
		this.visitGenerator = visitGenerator;
	}
	
	public String getSelectedEntity() {
		return selectedEntity;
	}

	public void setSelectedEntity(String selectedEntity) {
		this.selectedEntity = selectedEntity;
	}

}
