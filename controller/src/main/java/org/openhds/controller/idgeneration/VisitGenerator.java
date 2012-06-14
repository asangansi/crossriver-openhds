package org.openhds.controller.idgeneration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.domain.model.Location;
import org.openhds.domain.model.Visit;

/**
 * @author Brian
 * 
 * The Generator for Visit Ids. It examines the siteProperties and 
 * based on the template provided, creates the id. Refer to the 
 * application-context with the different components involved with
 * the id. 
 */

public class VisitGenerator extends Generator<Visit> {

	@Override
	public String generateId(Visit entityItem) throws ConstraintViolations  {
				
		StringBuilder sb = new StringBuilder();	
		
		IdScheme scheme = getIdScheme();
		HashMap<String, Integer> fields = scheme.getFields();
		Iterator<String> itr = fields.keySet().iterator();
		
		sb.append(scheme.getPrefix().toUpperCase());
		
		while(itr.hasNext()) {
			String key = itr.next();
			Integer filter = fields.get(key);
			
			if (key.equals(IdGeneratedFields.VISIT_LOCID.toString())) {
				String locId = entityItem.getVisitLocation().getExtId();
				String eaCode = locId.substring(0, locId.length()-3);
				sb.append(eaCode);
			}
			else if (key.equals(IdGeneratedFields.VISIT_ROUND.toString())) {	
				if (filter > 0) {	
					String round = entityItem.getRoundNumber().toString();	
					sb.append(round);
				}
			}
		}
				
		String locId = entityItem.getVisitLocation().getExtId();
		String houseNumber = locId.substring(locId.length()-3, locId.length());
		sb.append(houseNumber);
		entityItem.setExtId(sb.toString());
		
		if (scheme.getIncrementBound() > 0) {
			entityItem.setExtId(sb.toString());
			sb = new StringBuilder();
			sb.append(buildNumberWithBound(entityItem, scheme));
		}
		
		if (scheme.isCheckDigit()) 
			sb.append(generateCheckCharacter(sb.toString()));
				
		validateIdLength(sb.toString(), scheme);
				
		return sb.toString();
	}

	@Override
	public String buildNumberWithBound(Visit entityItem, IdScheme scheme) throws ConstraintViolations {
		
		int locationIdLength = scheme.getFields().get(IdGeneratedFields.VISIT_LOCID.toString());
		int prefixLength = scheme.getPrefix().length() + locationIdLength-3;
		if (scheme.getFields().get(IdGeneratedFields.VISIT_ROUND.toString()) > 0) {
			prefixLength += 1;
		}
		
		String prefix = entityItem.getExtId().substring(0, prefixLength);
		String suffix = entityItem.getExtId().substring(prefixLength);
		int maxNumberOfVisits = scheme.getIncrementBound();
		int currentVisitNumber = 1;
		
		Visit previousVisit = genericDao.findByProperty(Visit.class, "extId", prefix + currentVisitNumber + suffix, true);
		while(previousVisit != null) {
			if (currentVisitNumber >= maxNumberOfVisits) {
				throw new ConstraintViolations("The number of visits for this round has been exceed. The max number of visits per round is set to: " + maxNumberOfVisits);
			}
			currentVisitNumber += 1;
			previousVisit = genericDao.findByProperty(Visit.class, "extId", prefix + currentVisitNumber + suffix, true);
		}
		
		return prefix + currentVisitNumber + suffix;
	}
	
	public IdScheme getIdScheme() {
		int index = Collections.binarySearch(resource.getIdScheme(), new IdScheme("Visit"));
		return resource.getIdScheme().get(index);
	}
}
