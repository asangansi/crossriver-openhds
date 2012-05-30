package org.openhds.web.crud.impl;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.controller.idgeneration.IndividualGenerator;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.PregnancyOutcome;
import org.openhds.controller.service.EntityValidationService;
import org.openhds.controller.service.IndividualService;
import org.openhds.controller.service.PregnancyService;
import org.openhds.controller.service.SocialGroupService;

/**
 * The implementation of the crud for the Pregnancy Outcome form
 * This form varies from other crud classes because it using spring web flow
 * 
 * @author Dave
 *
 */
@SuppressWarnings("unchecked")
public class PregnancyOutcomeCrudImpl extends EntityCrudImpl<PregnancyOutcome, String> {

	EntityValidationService entityValidator;
	IndividualService individualService;
    PregnancyService service;
    SocialGroupService sgService;
    IndividualGenerator individualIdGenerator;
        	
    // used for manual conversion between Date and Calendar since the openFaces Calendar doesn't support JSF Converters
    Date recordedDate;
    Date dobChild;
    
	Boolean motherIdChange = false;
	Boolean secondChild = false;

    public PregnancyOutcomeCrudImpl(Class<PregnancyOutcome> entityClass) {
        super(entityClass);
        entityFilter = new PregnancyOutcomeFilter();
    }
    
	@Override
	public String createSetup() {
		motherIdChange = false;
		secondChild = false;
		return super.createSetup();
	}

	@Override
    public String create() {
    	 try {	
         	if (entityValidator.checkConstraints(entityItem) == false) {
 	        	  // verify integrity constraints
 	             service.evaluatePregnancyOutcome(entityItem);
 	             // create the pregnancy outcome
 	             // NOTE: this crud never explicity calls the super.create
 	             // because the service class will persist the pregnancy outcome
 	             service.createPregnancyOutcome(entityItem);
 	             return listSetup();
         	}
         } catch (ConstraintViolations e) {
             jsfService.addError(e.getMessage());
         } catch (IllegalArgumentException e) {
         	jsfService.addError(e.getMessage());
 		} catch (SQLException e) {
 			jsfService.addError(e.getMessage());
 		} 
        return null;
    }
	
	@Override
	public String edit() {
    	String outcome = super.edit();
    	
    	if (outcome != null) {
    		return "pretty:pregnancyoutcomeEdit";
    	}
    	
    	return null;
	}
	
    public void motherIdChange(ValueChangeEvent event) {
    	Individual mother = (Individual) event.getNewValue();
    	
    	try {		
			generateChild1Id(mother);
    	}
		catch (Exception e) {
			motherIdChange = false;
			FacesMessage message = new FacesMessage(e.getMessage());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage("motherExtId", message);
		}
    }

    /**
     * NOTE: This is also called from the flow
     * 
     * @param mother
     * @throws ConstraintViolations
     */
	public void generateChild1Id(Individual mother) throws ConstraintViolations {
		if (mother != null)
			motherIdChange = true;
		
		entityItem.setMother(mother);
		entityItem.setChild1(new Individual());
		entityItem.getChild1().setExtId(individualIdGenerator.filterBound(entityItem.getMother().getExtId()));
		entityItem.getChild1().setExtId(individualService.generateIdWithBound(entityItem.getChild1(), entityItem.getNumberOfLiveBirths()+1));
	}
	
    public void secondChildChange(ValueChangeEvent event) {
    	secondChild = (Boolean)event.getNewValue();
    	
    	if (secondChild) {
    		entityItem.setChild2(new Individual());
    		entityItem.getChild2().setExtId(individualIdGenerator.incrementId(entityItem.getChild1().getExtId()));
    	}
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
	
	public Date getDobChild() {
	    	
    	if (entityItem.getDobChild() == null)
    		return new Date();
    	
    	return entityItem.getDobChild().getTime();
	}
	
	public void setDobChild(Date dobChild) throws ParseException {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(dobChild);
		entityItem.setDobChild(cal);
	}
	
    public PregnancyService getService() {
        return service;
    }

    public void setService(PregnancyService service) {
        this.service = service;
    }

	public SocialGroupService getSgService() {
		return sgService;
	}

	public void setSgService(SocialGroupService sgService) {
		this.sgService = sgService;
	}
	
	public IndividualGenerator getIndividualIdGenerator() {
		return individualIdGenerator;
	}

	public void setIndividualIdGenerator(IndividualGenerator individualIdGenerator) {
		this.individualIdGenerator = individualIdGenerator;
	}
	
 	public IndividualService getIndividualService() {
		return individualService;
	}

	public void setIndividualService(IndividualService individualService) {
		this.individualService = individualService;
	}
	
	public Boolean getMotherIdChange() {
		return motherIdChange;
	}

	public void setMotherIdChange(Boolean motherIdChange) {
		this.motherIdChange = motherIdChange;
	}
		
	public Boolean getSecondChild() {
		return secondChild;
	}

	public void setSecondChild(Boolean secondChild) {
		this.secondChild = secondChild;
	}	
	
	public EntityValidationService getEntityValidator() {
		return entityValidator;
	}

	public void setEntityValidator(EntityValidationService entityValidator) {
		this.entityValidator = entityValidator;
	}
	
    private class PregnancyOutcomeFilter implements EntityFilter<PregnancyOutcome> {

        @Override
        public List<PregnancyOutcome> getFilteredEntityList(PregnancyOutcome entityItem) {
            if(entityItem!=null){
                return service.getPregnancyOutcomesByIndividual(entityItem.getMother());
            }
            return new ArrayList<PregnancyOutcome>();
        }
    }
}
