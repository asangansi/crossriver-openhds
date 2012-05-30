package org.openhds.web.beans;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.controller.service.EntityService;
import org.openhds.controller.service.IndividualService;
import org.openhds.controller.service.MembershipService;
import org.openhds.controller.service.ResidencyService;
import org.openhds.domain.model.AuditableCollectedEntity;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.Location;
import org.openhds.domain.model.Membership;
import org.openhds.domain.model.Residency;
import org.openhds.web.service.WebFlowService;
import org.springframework.binding.message.MessageContext;
import org.springframework.transaction.annotation.Transactional;

public class RegisterIndividualToSocialGroupBean implements Serializable {

	private static final long serialVersionUID = 7234551291397176322L;
	
	Individual individual;
	Membership membership;
	Residency residency;
	Location location = new Location();
	
	WebFlowService webFlowService;
	EntityService entityService;
	IndividualService individualService;
	MembershipService membershipService;
	ResidencyService residencyService;
	
	public boolean registerIndividual(MessageContext messageContext) {
				
		try {
			membershipService.evaluateMembership(membership);
			residency = residencyService.createResidency(individual, location, membership.getStartDate(), membership.getStartType(), individual.getCollectedBy());	
			residencyService.evaluateResidency(residency);	
		} catch(Exception e) {
			webFlowService.createMessage(messageContext, "Entity violation : " + e.getMessage());
			return false;
		}
		
		try {	
			List<AuditableCollectedEntity> entities = new ArrayList<AuditableCollectedEntity>();
			entities.add(membership);
			entities.add(residency);

			createEntities(entities);
		} catch (Exception e) {
			webFlowService.createMessage(messageContext, "Unable to complete transaction : " + e.getMessage());
			return false;
		} 
		
		webFlowService.createMessage(messageContext, "Registration completed successfully");
		return true;
	}
	
	public boolean createIndividual(MessageContext messageContext) {
		try {
			individualService.evaluateIndividual(individual);
			entityService.create(individual);
		} catch(Exception e) {
			webFlowService.createMessage(messageContext, "Unable to create Individual : " + e.getMessage());
			return false;
		}
		return true;
	}
	
	@Transactional
	private void createEntities(List<AuditableCollectedEntity> list) throws IllegalArgumentException, ConstraintViolations, SQLException {
		for (AuditableCollectedEntity entity : list) {
			entityService.create(entity);
		}
	}
		
	public Individual getIndividual() {
		return individual;
	}
	
	public void setIndividual(Individual individual) {
		this.individual = individual;
	}
	
	public Membership getMembership() {
		return membership;
	}
	
	public void setMembership(Membership membership) {
		this.membership = membership;
	}
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	public WebFlowService getWebFlowService() {
		return webFlowService;
	}

	public void setWebFlowService(WebFlowService webFlowService) {
		this.webFlowService = webFlowService;
	}
	
	public EntityService getEntityService() {
		return entityService;
	}

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}
	
	public IndividualService getIndividualService() {
		return individualService;
	}
	
	public void setIndividualService(IndividualService individualService) {
		this.individualService = individualService;
	}
	
	public MembershipService getMembershipService() {
		return membershipService;
	}
	
	public void setMembershipService(MembershipService membershipService) {
		this.membershipService = membershipService;
	}
	
	public ResidencyService getResidencyService() {
		return residencyService;
	}
	
	public void setResidencyService(ResidencyService residencyService) {
		this.residencyService = residencyService;
	}
}
