package org.openhds.web.crud.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.domain.model.OutMigration;
import org.openhds.controller.service.OutMigrationService;

public class OutMigrationCrudImpl extends EntityCrudImpl<OutMigration, String> {
	
	OutMigrationService service;
	
    // used for manual conversion between Date and Calendar since the openFaces Calendar doesn't support JSF Converters
    Date recordedDate;
    Date interviewDate;
    
    Boolean movedToLGA = false;
    List<String> deleteFailureMessages = new ArrayList<String>();
    Boolean constraintFailure = false;
    String residencyUuid = null;

	public OutMigrationCrudImpl(Class<OutMigration> entityClass) {
		super(entityClass);
		entityFilter = new OutMigrationEntityFilter();
	}
	
	@Override
	public String createSetup() {
		movedToLGA = false;
		residencyUuid = null;
		constraintFailure = false;
		return super.createSetup();
	}
	
    @Override
    public String create() {
    	try {
			service.evaluateOutMigration(entityItem);		
	        service.createOutMigration(entityItem);
	        return listSetup();
    	}		
    	catch(Exception e) {
    		jsfService.addError(e.getMessage());
    	}	  	
    	return null;
    }
    
    @Override
    public String edit() {
    	try {
			service.editOutMigration(entityItem);
		} catch (ConstraintViolations e) {
			for(String msg : e.getViolations()) {
				jsfService.addError(msg);
			}
			return null;
		} catch (Exception e) {
			jsfService.addError(e.getMessage());
			return null;
		}

    	return "pretty:outMigEdit";
    }
    
    @Override
    public String delete() {
    	deleteFailureMessages.clear();
    	
    	OutMigration outMig = (OutMigration)converter.getAsObject(FacesContext.getCurrentInstance(), null, jsfService.getReqParam("itemId"));
    	try {
    		service.deleteOutMigration(outMig);
    	} catch(ConstraintViolations e) {
    		for(String msg : e.getViolations()) {
    			deleteFailureMessages.add(msg);
    		}
    		showListing = false;
    		constraintFailure = true;
    		residencyUuid = outMig.getResidency().getUuid();
    		return outcomePrefix + "_delete_failure";
    	} catch(Exception e) {
    		deleteFailureMessages.add(e.getMessage());
    		showListing = false;
    		return outcomePrefix + "_delete_failure";
    	}
    	
		showListing = true;
        return listSetup();
    }
    
    public void placeMovedToChanged(ValueChangeEvent event) {
    	String placeMovedTo = event.getNewValue().toString();
    	
    	if (placeMovedTo.equals("1"))
    		movedToLGA = true;
    	else
    		movedToLGA = false;
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
	
	public Date getInterviewDate() {
    	
		if (entityItem.getDateOfInterview() == null)
			return new Date();
	
		return entityItem.getDateOfInterview().getTime();
	}

	public void setInterviewDate(Date interviewDate) throws ParseException {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(interviewDate);
		entityItem.setDateOfInterview(cal);
	}

	public OutMigrationService getService() {
		return service;
	}

	public void setService(OutMigrationService service) {
		this.service = service;
	}
	
	public Boolean getMovedToLGA() {
		return movedToLGA;
	}

	public void setMovedToLGA(Boolean movedToLGA) {
		this.movedToLGA = movedToLGA;
	}

	public List<String> getDeleteFailureMessages() {
		return deleteFailureMessages;
	}

	public Boolean getConstraintFailure() {
		return constraintFailure;
	}
	
	public String getResidencyUuid() {
		return residencyUuid;
	}

	private class OutMigrationEntityFilter implements EntityFilter<OutMigration> {

		public List<OutMigration> getFilteredEntityList(OutMigration entityItem) {
			return service.getOutMigrations(entityItem.getIndividual());
		}
	}
}
