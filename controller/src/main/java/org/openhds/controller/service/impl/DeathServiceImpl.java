package org.openhds.controller.service.impl;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openhds.dao.service.GenericDao;
import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.controller.service.DeathService;
import org.openhds.controller.service.EntityService;
import org.openhds.controller.service.IndividualService;
import org.openhds.domain.model.Death;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.Membership;
import org.openhds.domain.model.Relationship;
import org.openhds.domain.model.Residency;
import org.openhds.domain.model.SocialGroup;
import org.openhds.domain.service.SitePropertiesService;
import org.springframework.transaction.annotation.Transactional;

public class DeathServiceImpl implements DeathService {
	
	private static final Logger logger = Logger.getLogger(DeathServiceImpl.class);
	
	private static final int MILLISECONDS_IN_DAY = 86400000;
	private EntityService entityService;
	private GenericDao genericDao;
	private IndividualService individualService;
	private SitePropertiesService siteProperties;
	
	public DeathServiceImpl(GenericDao genericDao, IndividualService individualService, EntityService entityService, SitePropertiesService siteProperties) {
		this.genericDao = genericDao;
		this.individualService = individualService;
		this.entityService = entityService;
		this.siteProperties = siteProperties;
	}

	public Death evaluateDeath(Death entityItem) throws ConstraintViolations {
		if (!checkDuplicateIndividual(entityItem.getIndividual())) 
    		throw new ConstraintViolations("The Individual Id specified already exists.");	
		if (!checkHeadOfSocialGroup(entityItem.getIndividual())) 
    		throw new ConstraintViolations("The Individual specified is a Head of a Social Group. If you wish to create a Death event for this Individual, go to the Utility Menu and create a new Death event.");	
		
		return entityItem;
	}
		
	@Transactional(rollbackFor=Exception.class)
	public Death createDeath(Death entityItem) throws ConstraintViolations, SQLException {
		Calendar endDate = entityItem.getDeathDate();
		
		if (entityItem.getIndividual().getCurrentResidency() != null) {
			Residency residency = entityItem.getIndividual().getCurrentResidency();
			residency.setEndDate(endDate);
			residency.setEndType(siteProperties.getDeathCode());
			entityService.save(residency);
		}

		Long ageAtDeath = (endDate.getTimeInMillis() - entityItem.getIndividual().getDob().getTimeInMillis())/MILLISECONDS_IN_DAY;
		entityItem.setAgeAtDeath(ageAtDeath);
		
        //Gets the individual's memberships if any
        // Iterates through memberships and sets endType(DEATH) and endDate
        if (!entityItem.getIndividual().getAllMemberships().isEmpty()) {
            Set<Membership> memberships = (Set<Membership>) entityItem.getIndividual().getAllMemberships();
            for (Membership mem : memberships) {
            	if (mem.getEndType().equals(siteProperties.getNotApplicableCode())) {
	                mem.setEndDate(endDate);
	                mem.setEndType(siteProperties.getDeathCode());
	                entityService.save(mem);
            	}
            }
        }

        //Gets the individual's relationships if any
        // Iterates through the relationships and sets endType(DEATH) and endDate
         if (!entityItem.getIndividual().getAllRelationships1().isEmpty()) {
            Set<Relationship> relationships = (Set<Relationship>) entityItem.getIndividual().getAllRelationships1();
            Iterator<Relationship> it = relationships.iterator();
            while (it.hasNext()) {
                Relationship rel = it.next();
                if (rel.getEndType().equals(siteProperties.getNotApplicableCode())) {
	                rel.setEndDate(endDate);
	                rel.setEndType(siteProperties.getDeathCode());
	                entityService.save(rel);
                }
            }
        }
         
		 if (!entityItem.getIndividual().getAllRelationships2().isEmpty()) {
		     Set<Relationship> relationships = (Set<Relationship>) entityItem.getIndividual().getAllRelationships2();
		     Iterator<Relationship> it = relationships.iterator();
		     while (it.hasNext()) {
		         Relationship rel = it.next();
		         if (rel.getEndType().equals(siteProperties.getNotApplicableCode())) {
		             rel.setEndDate(endDate);
		             rel.setEndType(siteProperties.getDeathCode());
		             entityService.save(rel);
		         }
		     }
		 }
         
         entityService.create(entityItem);

         return entityItem;
	}

	@Transactional(rollbackFor=Exception.class)
	public void deleteDeath(Death entityItem) {
		
		if (entityItem.getIndividual().getCurrentResidency() != null && entityItem.getIndividual().getCurrentResidency().getEndType().equals(siteProperties.getDeathCode())) {
			entityItem.getIndividual().getCurrentResidency().setEndDate(null);
			entityItem.getIndividual().getCurrentResidency().setEndType(siteProperties.getNotApplicableCode());
		}

        if (!entityItem.getIndividual().getAllMemberships().isEmpty()) {
            Set<Membership> memberships = (Set<Membership>) entityItem.getIndividual().getAllMemberships();
            for (Membership mem : memberships) {
            	if (mem.getEndType().equals(siteProperties.getDeathCode())) {
	                mem.setEndDate(null);
	                mem.setEndType(siteProperties.getNotApplicableCode());
            	}
            }
        }

         if (!entityItem.getIndividual().getAllRelationships1().isEmpty()) {
            Set<Relationship> relationships = (Set<Relationship>) entityItem.getIndividual().getAllRelationships1();
            Iterator<Relationship> it = relationships.iterator();
            while (it.hasNext()) {
                Relationship rel = it.next();
                if (!(individualService.getLatestEvent(rel.getIndividualB()) instanceof Death) && rel.getEndType().equals(siteProperties.getDeathCode())) {
	                rel.setEndDate(null);
	                rel.setEndType(siteProperties.getNotApplicableCode());
                }
            }
        }
         
         if (!entityItem.getIndividual().getAllRelationships2().isEmpty()) {
             Set<Relationship> relationships = (Set<Relationship>) entityItem.getIndividual().getAllRelationships2();
             Iterator<Relationship> it = relationships.iterator();
             while (it.hasNext()) {
                 Relationship rel = it.next();
                 if (!(individualService.getLatestEvent(rel.getIndividualA()) instanceof Death) && rel.getEndType().equals(siteProperties.getDeathCode())) {
	                 rel.setEndDate(null);
	                 rel.setEndType(siteProperties.getNotApplicableCode());
                 }
             }
         } 
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void createDeathAndSetNewHead(Death death, List<SocialGroup> groups, List<Individual> successors, HashMap<Integer, List<Membership>> memberships) throws ConstraintViolations, SQLException, Exception {
		
		// Create the death event for the Group Head
		entityService.create(createDeath(death));
		
    	// Set the successor as the new Group Head
    	for (int i = 0; i < groups.size(); i++) { 		
    		groups.get(i).setGroupHead(successors.get(i));
    		entityService.save(groups.get(i));	
    	}	
    	
    	// Remove all Memberships from all Social Groups
    	for (SocialGroup item : groups) {
    		
    		Set<Membership> mems = item.getMemberships();  		
    		Iterator<Membership> itr = mems.iterator();
    		
    		while(itr.hasNext()) {
    			Membership mem = itr.next();
    			mem.setDeleted(true);
    			entityService.save(mem);
    		}
    	}
    	
    	// Create new Memberships 
    	for (List<Membership> list : memberships.values()) {	
    		for (Membership mem : list) {
    			entityService.create(mem);
    		}
    	}
    	
    }
	
	/**
	 * Check for duplicate Individuals entered
	 */
	public boolean checkDuplicateIndividual(Individual indiv) {
		
		List<Death> list = genericDao.findListByProperty(Death.class, "individual", indiv);	
		
		for (Death item : list) {		
			if (!item.isDeleted())
				return false;
		}
		return true;		
	}
	
	/**
	 * Checks if the Individual is the head of any Social Group
	 * Do not allow a death event for an individual who is the head of a social group with more than
	 * 1 member in the social group. If this is the case, the user will need to re-establish the relationships
	 * with the new head of house
	 */
	public boolean checkHeadOfSocialGroup(Individual indiv) {
		List<SocialGroup> list = genericDao.findListByProperty(SocialGroup.class, "groupHead", indiv);		
		
		for(SocialGroup sg : list) {
			Set<Membership> memberships = sg.getMemberships();
			if (memberships.size() > 1) {
				// social group has multiple memberships so user will need to re-establish the memberships
				return false;
			} else if (memberships.size() == 0) {
				// this should never be the case - but account for it anyway
				logger.warn("Found a social group with no memberhips when evaluating a death");
			} else {
				// if the household has only 1 membership, it should belong to the head of house
				// we double check here
				Membership[] mems = memberships.toArray(new Membership[]{});
				if (!mems[0].getIndividual().getExtId().equals(indiv.getExtId())) {
					logger.warn("Found a single membership to a household and it's not the head of house when evaluating a death");
					return false;
				}
			}
		}
		
		return true;
	}
	
	public List<Death> getDeathsByIndividual(Individual individual) {
		return genericDao.findListByProperty(Death.class, "individual", individual, true);
	}
}
