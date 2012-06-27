package org.openhds.web.crud.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import org.openhds.controller.exception.AuthorizationException;
import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.SocialGroup;
import org.openhds.controller.service.IndividualService;
import org.openhds.controller.service.SocialGroupService;
import org.springframework.binding.message.MessageContext;

public class SocialGroupCrudImpl extends EntityCrudImpl<SocialGroup, String> {

	SocialGroupService socialGroupService;
	IndividualService individualService;
	
	// its possible that head of household & respondent id do not yet
	// exist. This situation arises when registering a new household
	// during an update. The household has to be created before the head
	// of household.
	private String headOfHouseholdId;
	private String respondentId;
		
    // used for manual conversion between Date and Calendar since the openFaces Calendar doesn't support JSF Converters
    Date dateOfInterview;
	
	public SocialGroupCrudImpl(Class<SocialGroup> entityClass) {
		super(entityClass);
		entityFilter = new SocialGroupFilter();
	}
	
	@Override
	public String createSetup() {
		headOfHouseholdId = null;
		respondentId = null;
		return super.createSetup();
	}

	@Override
    public String create() {
    	
		boolean householdValidated = validateHeadOfHousehold();
		boolean respondentValidated = validateRespondent();	
		
		if (!householdValidated || !respondentValidated) {
			return null;
		}

		try {
			socialGroupService.evaluateSocialGroup(entityItem, false);
    		socialGroupService.createSocialGroup(entityItem);
    		showListing = true;
            return listSetup();
    	}		
    	catch(Exception e) {
    		jsfService.addError(e.getMessage());
		} 
    	return null;
    }
	
	private boolean validateHeadOfHousehold() {
		Individual indiv = validateIndividualExtId(headOfHouseholdId, "form:head");
		if (indiv == null) {
			return false;
		} else {
			entityItem.setGroupHead(indiv);
			return true;
		}
	}
	
	private Individual validateIndividualExtId(String field, String componentId) {
    	if (field == null || field.trim().isEmpty()) {
    		jsfService.addErrorForComponent("Please provide a valid Id", componentId);
    		return null;
    	}
    	Individual tmp = individualService.findIndivById(field);
    	if (tmp == null) {
    		tmp = new Individual();
    		tmp.setExtId(field);
    		try {
    			individualService.validateIdLength(tmp);
    		} catch (Exception e) {
    			jsfService.addErrorForComponent(e.getMessage(), componentId);
    			return null;
    		}
    	}
    	
    	return tmp;
	}
	
	private boolean validateRespondent() {
		Individual indiv = validateIndividualExtId(respondentId, "form:idOfRespondent");
		if (indiv == null) {
			return false;
		} else {
			entityItem.setRespondent(indiv);
			return true;
		}
	}
	
    @Override
    public String edit() {
    	
    	SocialGroup persistedItem = (SocialGroup)converter.getAsObject(FacesContext.getCurrentInstance(), null, jsfService.getReqParam("itemId"));

        try {
        	socialGroupService.checkSocialGroup(persistedItem, entityItem);
        	String outcome = super.edit();
        	
        	if (outcome != null) {
        		return "pretty:socialgroupEdit";
        	}
        	
        	return null;
		} catch (AuthorizationException e) {
			jsfService.addError(e.getMessage());
		} catch(Exception e) {
        	jsfService.addError(e.getMessage());
		}

        return null;
    }
    
    @Override
    public String delete() {
    	SocialGroup sg = (SocialGroup)converter.getAsObject(FacesContext.getCurrentInstance(), null, jsfService.getReqParam("itemId"));
    	
        try {
        	socialGroupService.deleteSocialGroup(sg);
		} catch (SQLException e) {
			jsfService.addError("Could not delete the persistent entity");
		} catch (AuthorizationException e) {
			jsfService.addError(e.getMessage());
			return null;
		}

        return listSetup();
    }
	
    @Override
    public boolean commit(MessageContext messageContext) {
    	try {
    		socialGroupService.evaluateSocialGroup(entityItem, false);
    		return super.commit(messageContext);
    	} catch(ConstraintViolations e) {
    		webFlowService.createMessage(messageContext, e.getMessage());
    	}
    	
    	return false;
    }
      
    public boolean validateSocialGroup(MessageContext messageContext) {
    	try {
    		socialGroupService.evaluateSocialGroup(entityItem, false);
		} catch (ConstraintViolations e) {
			webFlowService.createMessage(messageContext, e.getMessage());
			return false;
		}    	
		
		return true;
    }
    
    public Date getDateOfInterview() {
		if (dateOfInterview == null) {
			return new Date();
		}
		
		return entityItem.getDateOfInterview().getTime();
	}

	public void setDateOfInterview(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		entityItem.setDateOfInterview(cal);
	}
	
	public String getHeadOfHouseholdId() {
		return headOfHouseholdId;
	}

	public void setHeadOfHouseholdId(String headOfHouseholdId) {
		this.headOfHouseholdId = headOfHouseholdId;
	}

	public String getRespondentId() {
		return respondentId;
	}

	public void setRespondentId(String respondentId) {
		this.respondentId = respondentId;
	}
                
	public SocialGroupService getSocialGroupService() {
		return socialGroupService;
	}

	public void setSocialGroupService(SocialGroupService socialGroupService) {
		this.socialGroupService = socialGroupService;
	}
	
	public IndividualService getIndividualService() {
		return individualService;
	}

	public void setIndividualService(IndividualService individualService) {
		this.individualService = individualService;
	}
	
	private class SocialGroupFilter implements EntityFilter<SocialGroup> {
		@Override
		public List<SocialGroup> getFilteredEntityList(SocialGroup entityItem) {
			// attempt to get the prefix for the social group
			String extId = entityItem.getExtId();
			if (extId != null && extId.length() > 6) {
				return socialGroupService.getSocialGroupsPrefixedWith(extId.substring(0, 6));	
			}	
			return new ArrayList<SocialGroup>();
		}
	}
}
