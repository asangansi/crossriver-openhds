package org.openhds.dao.service;

import org.openhds.domain.model.LocationHierarchy;

public interface LocationHierarchyDao extends Dao<LocationHierarchy, String> {

    LocationHierarchy findLocationHierarchyWithHighestIdAtLevel(String prefix, LocationHierarchy parent);
}
