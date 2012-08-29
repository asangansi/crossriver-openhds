package org.openhds.controller.idgeneration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import org.openhds.controller.exception.ConstraintViolations;
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
		
		StringBuilder builder = null;
		int increment = 0;
		boolean result = false;
		String visitId = entityItem.getExtId();
		
		do {
			builder = new StringBuilder();
			increment++;
			String prefix = visitId.substring(0, 11);
			String suffix = visitId.substring(11, 14);
			
			builder.append(prefix + increment + suffix);
			Visit visit = genericDao.findByProperty(Visit.class, "extId", builder.toString(), true);
			
			if (visit == null)
				result = true;
			
		} while(!result);
				
		return builder.toString();
	}
	
	public IdScheme getIdScheme() {
		int index = Collections.binarySearch(resource.getIdScheme(), new IdScheme("Visit"));
		return resource.getIdScheme().get(index);
	}
}
