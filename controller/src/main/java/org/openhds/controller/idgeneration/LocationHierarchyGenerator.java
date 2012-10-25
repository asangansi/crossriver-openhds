package org.openhds.controller.idgeneration;

import java.util.Collections;

import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.dao.service.GenericDao.ValueProperty;
import org.openhds.dao.service.GenericDao.ValuePropertyBuilder;
import org.openhds.dao.service.LocationHierarchyDao;
import org.openhds.domain.model.LocationHierarchy;

/**
 * The Generator for Location Hierarchy Ids. It examines the siteProperties and based on the template provided, creates
 * the id. Refer to the application-context with the different components involved with the id.
 */

public class LocationHierarchyGenerator extends Generator<LocationHierarchy> {
    
    private LocationHierarchyDao locationHierarchyDao;

    @Override
    public String generateId(LocationHierarchy location) throws ConstraintViolations {
        StringBuilder sb = new StringBuilder();

        if (isFirstLevelLocation(location)) {
            sb.append(generateFirstLevelId(location));
        } else {
            sb.append(generateLowerThanFirstLevelId(location));
        }
        if (!checkValidId(sb.toString(), location.getParent()))
            throw new ConstraintViolations("An id was generated that already exists in the Location Hierarchy.");

        return sb.toString();
    }

    private Object generateLowerThanFirstLevelId(LocationHierarchy location) {
        StringBuilder sb = new StringBuilder();
        LocationHierarchy parent = location.getParent();
        sb.append(parent.getExtId());

        LocationHierarchy highId = locationHierarchyDao.findLocationHierarchyWithHighestIdAtLevel(parent.getExtId(), parent);

        if (highId == null) {
            sb.append("01");
        } else {
            String highest = highId.getExtId();
            int lastDigitCnt = Integer.parseInt(highest.substring(highest.length() - 2));
            int nextDigitCnt = lastDigitCnt + 1;
            String digitCnt = String.format("%02d", nextDigitCnt);
            sb.append(digitCnt);
        }

        return sb.toString();
    }

    private String generateFirstLevelId(LocationHierarchy location) throws ConstraintViolations {
        // use the first 3 letters of the name
        if (location.getName().length() < 3) {
            throw new ConstraintViolations("Name must be at least 3 characters");
        }

        return location.getName().substring(0, 3).toUpperCase();
    }

    private boolean isFirstLevelLocation(LocationHierarchy location) {
        return "HIERARCHY_ROOT".equalsIgnoreCase(location.getParent().getExtId());
    }

    private boolean checkValidId(String extId, LocationHierarchy parent) {
        ValueProperty property1 = ValuePropertyBuilder.build("parent", parent);
        ValueProperty property2 = ValuePropertyBuilder.build("extId", extId);
        LocationHierarchy item = genericDao.findByMultiProperty(LocationHierarchy.class, property1, property2);
        if (item != null)
            return false;
        return true;
    }

    // not applicable for location hierarchy
    @Override
    public String buildNumberWithBound(LocationHierarchy entityItem, IdScheme scheme) throws ConstraintViolations {
        return null;
    }

    public IdScheme getIdScheme() {
        int index = Collections.binarySearch(resource.getIdScheme(), new IdScheme("LocationHierarchy"));
        return resource.getIdScheme().get(index);
    }

    public void setLocationHierarchyDao(LocationHierarchyDao locationHierarchyDao) {
        this.locationHierarchyDao = locationHierarchyDao;
    }
}
