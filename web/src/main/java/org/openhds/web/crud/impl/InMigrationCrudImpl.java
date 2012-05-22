package org.openhds.web.crud.impl;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.controller.idgeneration.IndividualGenerator;
import org.openhds.domain.model.InMigration;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.Membership;
import org.openhds.domain.model.Residency;
import org.openhds.controller.service.InMigrationService;
import org.openhds.controller.service.IndividualService;

public class InMigrationCrudImpl extends EntityCrudImpl<InMigration, String> {

	InMigrationService service;
    IndividualService individualService;
	IndividualGenerator individualIdGenerator; 

	// used for manual conversion between Date and Calendar since the openFaces Calendar doesn't support JSF Converters
    Date recordedDate;
    Date dateOfInterview;
    Date movedInPersonDob;
    
    Boolean idKnown = false;
  	Boolean everRegistered = false;
    Boolean firstRegistered = false;
    Boolean relationshipCodeRequired = false;
    
    Boolean everRegisteredComplete = false;
    Boolean firstRegisteredComplete = false;
    
    Integer phase = 1;
	
	public InMigrationCrudImpl(Class<InMigration> entityClass) {
        super(entityClass);
        entityFilter = new InMigrationEntityFilter();
    }
    
    @Override
    public String createSetup() {
    	resetState();
    	String outcome = super.createSetup();
    	return outcome.replace("inmigration", "migration");
    }
    
    private void resetState() {
		everRegistered = false;
    	firstRegistered = false;
    	idKnown = false;
    	everRegisteredComplete = false;
    	firstRegisteredComplete = false;
    	relationshipCodeRequired = false;
    	phase = 1;
	}
    
    @Override
	public boolean initFlow() {
		super.initFlow();
		phase = 2;
		return true;
	}

	@Override
	public String create() {
    	try {
    		createInMigration();
		} catch (ConstraintViolations e) {
			for(String msg : e.getViolations()) {
    			jsfService.addError(msg);
    		}
    		phase = 100;
    		return null;
		} catch (Exception e) {
			jsfService.addError(e.getMessage());
			// set the phase to a number that is not matched on the create page
			// so the javascript wont auto-scroll to a portion of the page
			// it is set high so that fields continue to be disabled (they are disabled
			// based on the current phase being greater than some number)
			phase = 100;
			return null;
		}
		resetState();
		return listSetup();
	}

	private void createInMigration() throws ConstraintViolations, SQLException, Exception {
		service.evaluateInMigration(entityItem);
		service.createInMigration(entityItem);
	}

	@Override
	public String edit() {
    	try {
			service.evaluateInMigrationOnEdit(entityItem);		
	        super.edit();
	        
	        return "pretty:migrationsEdit";
    	}		
    	catch(Exception e) {
    		jsfService.addError(e.getMessage());
    	}	  	
    	return null;
	}    
	
	@Override
	public String editSetup() {
		// when saving an in migration, it requires crawling several associations that
		// are lazy loaded by hibernate. This "hack" will crawl those associations so that
		// they are loaded before the entity is detached from the hibernate session. There
		// is a need to provide for a better approach to doing this, but it seems like that would take
		// a fair bit of refactoring. Possibly something that could be done in a future iteration
		// This patch is meant to make things work for now
		String outcome = super.editSetup();
		
		// force the individuals past residency records to be loaded as required for
		// integrity constraint checks by residencyService
		if (entityItem != null) {
			Set<Residency> res = entityItem.getIndividual().getAllResidencies();
			res.size();
		}
		
		return outcome;
	}
	
	public void fieldWorkerChange(ValueChangeEvent event) {
		phase = 2;
	}
	
	public void houseChange(ValueChangeEvent event) {
		phase = 3;
	}
	
	public void householdChange(ValueChangeEvent event) {
		if (entityItem.getVisit() == null) {
			phase = 4;
		} else {
			phase = 5;
		}
	}
	
	public void visitChange(ValueChangeEvent event) {
		phase = 5;
	}
	
	public void registeredChange(ValueChangeEvent event) {
		String value = event.getNewValue().toString();
		if (value.equals("1")) {
			everRegistered = true;
		}
		else if (value.equals("2")) {
			everRegistered = false;
		} else {
			jsfService.addErrorForComponent("Invalid value. Must be 1 or 2", "form:everReg");
			return;
		}
		everRegisteredComplete = true;
		
		phase = 6;
	}
	
	public void knownIdChange(ValueChangeEvent event) {
		String value = event.getNewValue().toString();
		if (value.equals("1")) {
			idKnown = true;
			entityItem.setReferencesTemporaryIndividual(false);
			phase = 7;
		} else if (value.equals("2")) {
			if (!service.generateIdForMigrant(entityItem)) {
				FacesMessage message = new FacesMessage("Cannot generate the permanent id for this Individual");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage("indiv", message);
			} else {
				idKnown = false;
				entityItem.setReferencesTemporaryIndividual(true);		
				relationshipCodeRequired = true;
				phase = 8;
			}
		} else {
			jsfService.addErrorForComponent("Invalid value. Must be 1 or 2", "form:known");
			return;
		}
		
		if (!everRegistered) {
			relationshipCodeRequired = true;
		}
	}
	
	public void extIdChange(ValueChangeEvent event) {
		phase = 8;
		
		// determine if the individual id entered already has a membership
		// to the household that was entered in
		Individual indiv = individualService.findIndivById(event.getNewValue().toString());
		if (indiv == null) {
			// no individual in the system would mean a new individual
			relationshipCodeRequired = true;
			return;
		}
		
		String sgExtId = entityItem.getHousehold().getExtId();
		boolean alreadyInSocialGroup = false;
		for(Membership mem : indiv.getAllMemberships()) {
			if (mem.getSocialGroup().getExtId().equals(sgExtId)) {
				// if the individual does NOT already have a membership
				alreadyInSocialGroup = true;
			}
		}
		
		if (!alreadyInSocialGroup) {
			relationshipCodeRequired = true;
		}
	}
	
	public void originChange(ValueChangeEvent event) {
		if (event.getOldValue() == null && event.getNewValue().equals("")) {
			return;
		}
		
		String value = event.getNewValue().toString();
		if (value.equals("1")) {
			firstRegistered = true;
			phase = 10;
		} else if (value.equals("5")) {
			phase = 9;
		} else {
			firstRegistered = false;
			if (everRegistered) {
				phase = 11;
			} else {
				phase = 12;
			}
		}
		firstRegisteredComplete = true;
	}
	
	public String startInternal() {
		createSetup();
		
		return "internal_inmigration";
	}
	
	public InMigrationService getService() {
		return service;
	}

	public void setService(InMigrationService service) {
		this.service = service;
	}
	
	private class InMigrationEntityFilter implements EntityFilter<InMigration> {

		public List<InMigration> getFilteredEntityList(InMigration entityItem) {
			if (entityItem.getIndividual() != null && entityItem.getIndividual().getUuid() != null) {
				// its possible for an in migration to have an individual that is not persisted
				// yet during an external in migration. If an individual that is not yet persisted
				// is passed to this service, a TransientObjectException will be thrown
				return service.getInMigrationsByIndividual(entityItem.getIndividual());
			}
			
			return new ArrayList<InMigration>();
		}
		
	}
	
	/**
	 * (droberge):
	 * This is a work around for a JSF issue I was experiencing. The InMigration entity has a field
	 * of the wrapper type Boolean. JSF doesn't seem to want to accept the wrapper, and only accepts
	 * the primitive type. See http://www.icefaces.org/JForum/posts/list/5474.page for a discussion
	 * @return
	 */
	public boolean isUnknownIndividual() {
		return entityItem.isUnknownIndividual() ? true : false;
	}
	
    public Date getRecordedDate() {
    	
    	if (entityItem.getRecordedDate() == null)
    		return new Date();
    	
    	return entityItem.getRecordedDate().getTime();
	}

	public void setRecordedDate(Date recordedDate) throws ParseException {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(recordedDate);
		entityItem.setRecordedDate(cal);
	}
	
    public Date getDateOfInterview() {
    	
    	if (entityItem.getDateOfInterview() == null)
    		return new Date();
    	
    	return entityItem.getDateOfInterview().getTime();
	}

	public void setDateOfInterview(Date dateOfInterview) throws ParseException {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateOfInterview);
		entityItem.setDateOfInterview(cal);
	}
	
    public Date getMovedInPersonDob() {
    	
    	if (entityItem.getMovedInPersonDob() == null)
    		return new Date();
    	
    	return entityItem.getMovedInPersonDob().getTime();
	}

	public void setMovedInPersonDob(Date movedInPersonDob) throws ParseException {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(movedInPersonDob);
		entityItem.setMovedInPersonDob(cal);
	}
	
	public IndividualService getIndividualService() {
		return individualService;
	}

	public void setIndividualService(IndividualService individualService) {
		this.individualService = individualService;
	}

	public IndividualGenerator getIndividualIdGenerator() {
		return individualIdGenerator;
	}

	public void setIndividualIdGenerator(IndividualGenerator individualIdGenerator) {
		this.individualIdGenerator = individualIdGenerator;
	}

	public Boolean getIdKnown() {
		return idKnown;
	}

	public void setIdKnown(Boolean idKnown) {
		this.idKnown = idKnown;
	}

	public Boolean getEverRegistered() {
		return everRegistered;
	}

	public void setEverRegistered(Boolean everRegistered) {
		this.everRegistered = everRegistered;
	}

	public Boolean getFirstRegistered() {
		return firstRegistered;
	}

	public void setFirstRegistered(Boolean firstRegistered) {
		this.firstRegistered = firstRegistered;
	}

	public Boolean getRelationshipCodeRequired() {
		return relationshipCodeRequired;
	}

	public void setRelationshipCodeRequired(Boolean relationshipCodeRequired) {
		this.relationshipCodeRequired = relationshipCodeRequired;
	}

	public Boolean getEverRegisteredComplete() {
		return everRegisteredComplete;
	}

	public void setEverRegisteredComplete(Boolean everRegisteredComplete) {
		this.everRegisteredComplete = everRegisteredComplete;
	}

	public Boolean getFirstRegisteredComplete() {
		return firstRegisteredComplete;
	}

	public void setFirstRegisteredComplete(Boolean firstRegisteredComplete) {
		this.firstRegisteredComplete = firstRegisteredComplete;
	}

	public Integer getPhase() {
		return phase;
	}

	public void setPhase(Integer phase) {
		this.phase = phase;
	}
}
