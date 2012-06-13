package org.openhds.web.crud.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.Location;
import org.openhds.domain.model.OutMigration;
import org.openhds.domain.model.SocialGroup;
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
    
    Boolean individualIdChange = false;
    Boolean houseIdChange = false;
    Boolean householdIdChange = false;

	public OutMigrationCrudImpl(Class<OutMigration> entityClass) {
		super(entityClass);
		entityFilter = new OutMigrationEntityFilter();
	}
	
	@Override
	public String createSetup() {
		movedToLGA = false;
		residencyUuid = null;
		constraintFailure = false;
		individualIdChange = false;
		houseIdChange = false;
		householdIdChange = false;
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
    
    public void individualIdChange(ValueChangeEvent event) {
    	Individual individual = (Individual) event.getNewValue();
    	
    	try {		
			if (individual != null) {
				entityItem.setIndividual(individual);
				individualIdChange = true;
			}
    	}
		catch (Exception e) {
			individualIdChange = false;
			FacesMessage message = new FacesMessage(e.getMessage());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage("indiv", message);
		}
    }
    
    public void houseIdChange(ValueChangeEvent event) {
    	Location location = (Location) event.getNewValue();
    	
    	try {		
			if (location != null) {
				entityItem.setHouse(location);
				houseIdChange = true;
			}
    	}
		catch (Exception e) {
			houseIdChange = false;
			FacesMessage message = new FacesMessage(e.getMessage());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage("loc", message);
		}
    }
    
    public void householdIdChange(ValueChangeEvent event) {
    	SocialGroup socialGroup = (SocialGroup) event.getNewValue();
    	
    	try {		
			if (socialGroup != null) {
				entityItem.setHousehold(socialGroup);
				householdIdChange = true;
			}
    	}
		catch (Exception e) {
			householdIdChange = false;
			FacesMessage message = new FacesMessage(e.getMessage());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage("sg", message);
		}
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
	
	public Boolean getIndividualIdChange() {
		return individualIdChange;
	}

	public void setIndividualIdChange(Boolean individualIdChange) {
		this.individualIdChange = individualIdChange;
	}

	public Boolean getHouseIdChange() {
		return houseIdChange;
	}

	public void setHouseIdChange(Boolean houseIdChange) {
		this.houseIdChange = houseIdChange;
	}

	public Boolean getHouseholdIdChange() {
		return householdIdChange;
	}

	public void setHouseholdIdChange(Boolean householdIdChange) {
		this.householdIdChange = householdIdChange;
	}


	private class OutMigrationEntityFilter implements EntityFilter<OutMigration> {

		public List<OutMigration> getFilteredEntityList(OutMigration entityItem) {
			return service.getOutMigrations(entityItem.getIndividual());
		}
	}
}
