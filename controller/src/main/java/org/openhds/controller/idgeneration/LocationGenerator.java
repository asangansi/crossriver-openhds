package org.openhds.controller.idgeneration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.controller.service.LocationHierarchyService;
import org.openhds.domain.model.Location;
import org.openhds.domain.model.LocationHierarchy;
import org.openhds.domain.model.LocationHierarchyLevel;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Brian
 * 
 * The Generator for Location Ids. It examines the siteProperties and 
 * based on the template provided, creates the id. Refer to the 
 * application-context with the different components involved with
 * the id. 
 */

public class LocationGenerator extends Generator<Location> {
		
	@Override
	public String generateId(Location location) throws ConstraintViolations  {
		StringBuilder sb = new StringBuilder();	
		
		IdScheme scheme = getIdScheme();
		HashMap<String, Integer> fields = scheme.getFields();
		Iterator<String> itr = fields.keySet().iterator();
		
		if (scheme.getPrefix() != null) {	
			LocationHierarchyLevel level = genericDao.findByProperty(LocationHierarchyLevel.class, "name", scheme.getPrefix());
			List<LocationHierarchy> hierarchyItems = genericDao.findListByProperty(LocationHierarchy.class, "level", level);

			for (LocationHierarchy item : hierarchyItems) {
				List<String> locs = getValidLocationsInHierarchy(item.getExtId());
				
				for (String name : locs) {
					if (name.equals(location.getLocationLevel().getExtId())) {
						sb.append(item.getExtId().toUpperCase() + location.getLocationLevel().getParent().getExtId());
					}
				}
			}
		}
				
		while(itr.hasNext()) {
			String key = itr.next();
			Integer filter = fields.get(key);
			
			if (filter != null) {
			
				if (key.equals(IdGeneratedFields.LOCATION_HIERARCHY_ID.toString())) {
					String locId = location.getLocationLevel().getExtId();
					if (filter > 0 && locId.length() >= filter) 
						sb.append(formatProperString(locId, filter));
					else if (filter == 0 || locId.length() < filter) 
						sb.append(formatProperString(locId, locId.length()));
					else
						throw new ConstraintViolations("An error occurred while attempting to generate " +
								"the id on the field specified as '" + locId + "'");
				}
				else if (key.equals(IdGeneratedFields.LOCATION_NAME.toString())) {
					String locationName = location.getLocationName();
					
					if (locationName.length() >= filter) {
					
						if (filter > 0 && location.getLocationName().length() >= filter) 			
							sb.append(formatProperString(locationName, filter));
						else if (filter == 0 || locationName.length() < filter)
							sb.append(formatProperString(locationName, locationName.length()));
						else
							throw new ConstraintViolations("An error occurred while attempting to generate " +
									"the id on the field specified as '" + locationName + "'");
					}
					else
						throw new ConstraintViolations("Unable to generate the id. Make sure the field Location Name is of the required length " +
								"specified in the id configuration.");
				}
			}
		}
		
		extId = sb.toString();
		if (scheme.getIncrementBound() > 0) 
			sb.append(buildNumberWithBound(location, scheme));
		else
			sb.append(buildNumber(Location.class, sb.toString(), scheme.isCheckDigit()));
					
		validateIdLength(sb.toString(), scheme);
		
		return sb.toString();
	}

	@Override
	public String buildNumberWithBound(Location entityItem, IdScheme scheme) throws ConstraintViolations {
		
		Location loc = new Location();
		
		Integer size = 1;
		String result = "";
		
		// get length of the incrementBound
		Integer incBound = scheme.getIncrementBound();
		int incBoundLength = incBound.toString().length();

		while (loc != null) {
			
			result = "";
			String tempExtId = extId;
						
			while (result.toString().length() < incBoundLength) {
				if (result.toString().length()+ size.toString().length() < incBoundLength)
					result += "0";
				if (result.toString().length() + size.toString().length() == incBoundLength)
					result = result.concat(size.toString());
			}
			
			if (extId == null)
				tempExtId = entityItem.getExtId().concat(result);
			else
				tempExtId = tempExtId.concat(result);
			
			if (scheme.isCheckDigit()) {
				String resultChar = generateCheckCharacter(tempExtId).toString();
				result = result.concat(resultChar);
				tempExtId = tempExtId.concat(resultChar);
			}
			
			loc = genericDao.findByProperty(Location.class, "extId", tempExtId);
			size++;
		}
		return result;
	}
	
	public IdScheme getIdScheme() {
		int index = Collections.binarySearch(resource.getIdScheme(), new IdScheme("Location"));
		return resource.getIdScheme().get(index);
	}
	
	public void validateId(Location loc) throws ConstraintViolations {
		IdScheme scheme = getIdScheme();
		
		HashMap<String, Integer> fields = scheme.getFields();
		Iterator<String> itr = fields.keySet().iterator();
		
		if (validateIdLength(loc.getExtId(), scheme)) {
					
			while(itr.hasNext()) {
				String key = itr.next();
				Integer filter = fields.get(key);
				
				if (filter != null) {
					
					if (key.equals(IdGeneratedFields.LOCATION_HIERARCHY_ID.toString())) {
						String locId = loc.getLocationLevel().getExtId();
						String sub = "";
						
						if (locId.length() >= filter)
							sub = locId.substring(0, filter);
						
						if (!loc.getExtId().contains(sub))
							throw new ConstraintViolations("The Location Id must contain " + sub);
					}
				}
			}
		}
	}
	
	private List<String> getValidLocationsInHierarchy(String locationHierarchyItem) {	
		List<String> locations = new ArrayList<String>();
    	locations.add(locationHierarchyItem);
    	List<LocationHierarchy> hierarchyList = genericDao.findListByProperty(LocationHierarchy.class, "extId", locationHierarchyItem);
    	for (LocationHierarchy item : hierarchyList) {
    		locations = processLocationHierarchy(item, locations);
    	}
    	return locations;
	}
	
	private List<String> processLocationHierarchy(LocationHierarchy item, List<String> locationNames) {	
    	// base case
        if (item.getLevel().getName().equals(getLowestLevel().getName())) {
        	locationNames.add(item.getExtId());
        	return locationNames;
        }
        
        // find all location hierarchy items that are children, continue to recurse
        List<LocationHierarchy> hierarchyList = genericDao.findListByProperty(LocationHierarchy.class, "parent", item);
       
        for (LocationHierarchy locationHierarchy : hierarchyList)
            processLocationHierarchy(locationHierarchy, locationNames);

        return locationNames;
	}
	
	public LocationHierarchyLevel getLowestLevel() {	  	
		List<LocationHierarchyLevel> locHLevels = genericDao.findAll(LocationHierarchyLevel.class, false);
		int highestKey = Integer.MIN_VALUE;
		
		for (LocationHierarchyLevel item : locHLevels) {
			if (item.getKeyIdentifier() > highestKey)
				highestKey = item.getKeyIdentifier();
		}
		return genericDao.findByProperty(LocationHierarchyLevel.class, "keyIdentifier", highestKey);
	}
}
