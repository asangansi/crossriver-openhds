package org.openhds.controller.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import org.openhds.dao.service.GenericDao;
import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.controller.service.EntityService;
import org.openhds.controller.service.IndividualService;
import org.openhds.controller.idgeneration.IdScheme;
import org.openhds.controller.idgeneration.IdSchemeResource;
import org.openhds.controller.idgeneration.IndividualGenerator;
import org.openhds.domain.model.Death;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.InMigration;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.OutMigration;
import org.openhds.domain.model.VisitableEntity;
import org.openhds.domain.service.SitePropertiesService;
import org.openhds.domain.util.CalendarUtil;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("unchecked")
public class IndividualServiceImpl implements IndividualService {
	
	private GenericDao genericDao;
	private SitePropertiesService properties;
	private IndividualGenerator indivGen;
	private EntityService entityService;
	private IdSchemeResource resource;

	public IndividualServiceImpl(GenericDao genericDao, IndividualGenerator generator, SitePropertiesService properties, EntityService entityService, IdSchemeResource resource) {
		this.genericDao = genericDao;
		this.indivGen = generator;
		this.properties = properties;
		this.entityService = entityService;
		this.resource = resource;
	}
	
	public Individual evaluateIndividual(Individual entityItem) throws ConstraintViolations {	
		if (indivGen.generated)	
			return generateId(entityItem);
		
		if (findIndivById(entityItem.getExtId()) != null)
			throw new ConstraintViolations("The Id specified already exists");
				
		validateIdLength(entityItem);
		
		return entityItem;
	}
	
	public Individual validateIdLength(Individual entityItem) throws ConstraintViolations {
		indivGen.validateIdLength(entityItem.getExtId(), indivGen.getIdScheme());
		return entityItem;
	}
	
	public void validateGeneralIndividual(Individual indiv) throws ConstraintViolations {
		if (indiv.getGender().equals(properties.getUnknownIdentifier()))
			throw new ConstraintViolations("The Individual is partially completed");
	}
	
	public Individual generateId(Individual entityItem) throws ConstraintViolations {
		entityItem.setExtId(indivGen.generateId(entityItem));
		return entityItem;
	}
		
	/**
	 * Used to create a custom id based off a counter. The custom id generated
	 * will be incremented the number of times of the counter specified. This
	 * routine is used to create new individual ids from pregnancy outcome.
	 */
	public String generateIdWithBound(Individual entityItem, int count) throws ConstraintViolations {
		IdScheme scheme = resource.getIdSchemeByName("Individual");
		String result = entityItem.getExtId().concat(indivGen.buildNumberWithBound(entityItem, scheme));
		String output = result;
		int index = 1;
		while (index < count) {
			output = indivGen.incrementId(result);
			result = output;
			index++;
		}
		return output;
	}
		
    /**
     * Retrieves all Individual extId's that contain the term provided.
     * Used in performing autocomplete.
     */
    public List<String> getIndividualExtIds(String term) {
    	List<String> ids = new ArrayList<String>();
    	List<Individual> list = genericDao.findListByPropertyPrefix(Individual.class, "extId", term, 10, true);
    	for(Individual indiv : list) {
    		ids.add(indiv.getExtId());
    	}
    	
    	return ids;
    }
    
    public Individual findIndivById(String indivExtId, String msg) throws Exception {
        Individual indiv = genericDao.findByProperty(Individual.class, "extId", indivExtId);
        if (indiv == null) {
            throw new Exception(msg);
        }
        return indiv;
    }   
    
    @Transactional(readOnly=true)
    public Individual findIndivById(String indivExtId) {
        Individual indiv = genericDao.findByProperty(Individual.class, "extId", indivExtId);
        return indiv;
    }    
	
    @Transactional(readOnly=true)
	public VisitableEntity getLatestEvent(Individual individual) {
		// it's possible the individual passed in hasn't actually been persisted
		// yet. This is a guard against throwing a Transient Object exception
		if (findIndivById(individual.getExtId()) == null) {
			return VisitableEntity.NULL_VISITABLE_ENTITY;
		}
		
		// determine the latest event
		// a Death event should in theory be the last of any events
		Death death = genericDao.findByProperty(Death.class, "individual", individual, true);
		
		if (death != null) {
			return death;
		}
		// otherwise determine latest event
		OutMigration om = genericDao.findUniqueByPropertyWithOrder(OutMigration.class, "individual", individual, "recordedDate", false);
		InMigration in = genericDao.findUniqueByPropertyWithOrder(InMigration.class, "individual", individual, "recordedDate", false);
		
		if (om == null && in == null) {
			return VisitableEntity.NULL_VISITABLE_ENTITY;
		}
		
		if (om != null && in == null) {
			return om;
		}
		
		if (in != null && om == null) {
			return in;
		}
		
		if (in.getRecordedDate().compareTo(om.getRecordedDate()) >= 0) {
			return in;
		}
		
		return om;
	}

	@Transactional(rollbackFor=Exception.class)
	public Individual createTemporaryIndividualWithExtId(String extId, FieldWorker collectedBy) throws Exception {
		Individual head = new Individual();
		head.setFirstName("Temporary Individual");
		head.setLastName("Temporary Individual");
		head.setExtId(extId);
		head.setMother(findIndivById(properties.getUnknownIdentifier()));
		head.setFather(findIndivById(properties.getUnknownIdentifier()));
		head.setGender(properties.getUnknownIdentifier());
		head.setCollectedBy(collectedBy);
		
		entityService.create(head);
		return head;
	}
}