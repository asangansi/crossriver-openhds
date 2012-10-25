package org.openhds.controller.idgeneration;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.validation.ConstraintViolation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.dao.service.GenericDao;
import org.openhds.domain.model.Location;
import org.openhds.domain.model.LocationHierarchy;
import org.openhds.domain.model.LocationHierarchyLevel;

public class LocationGeneratorTest {

    @Mock
    GenericDao genericDao;
    private Location location;
    private LocationGenerator generator;

    @Before
    public void setUp() {
        initMocks(this);
        buildHierarchy();
        buildGenerator();
    }

    protected void buildHierarchy() {
        LocationHierarchy enumerationArea = new LocationHierarchy();
        enumerationArea.setExtId("AKP0102");

        location = new Location();
        location.setLocationLevel(enumerationArea);
    }

    protected LocationGenerator buildGenerator() {
        generator = new LocationGenerator();
        IdScheme scheme = buildIdScheme();
        IdSchemeResource resource = new IdSchemeResource();
        resource.setIdScheme(Arrays.asList(scheme));
        generator.setGenericDao(genericDao);
        generator.setResource(resource);
        return generator;
    }

    protected IdScheme buildIdScheme() {
        IdScheme scheme = new IdScheme();
        scheme.setName("Location");
        scheme.setPrefix("LGA");
        scheme.setLength(10);
        scheme.setIncrementBound(999);
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("LOCATION_NAME", null);
        map.put("LOCATION_HIERARCHY_ID", 2);
        scheme.setFields(map);

        return scheme;
    }

    @Test
    public void shouldGenerateInitialLocationId() throws ConstraintViolations {
        List<Location> results = Collections.emptyList();
        when(genericDao.findListByPropertyPrefix(Location.class, "extId", "AKP0102", 1, false, true)).thenReturn(
                results);

        String generatedId = generator.generateId(location);

        assertEquals("AKP0102001", generatedId);
    }

    @Test
    public void shouldGenerateNextLocationId() throws ConstraintViolations {
        Location loc = new Location();
        loc.setExtId("AKP0102001");
        List<Location> results = Arrays.asList(loc);
        when(genericDao.findListByPropertyPrefix(Location.class, "extId", "AKP0102", 1, false, true)).thenReturn(
                results);

        String generatedId = generator.generateId(location);

        assertEquals("AKP0102002", generatedId);
    }

    @Test
    public void shouldGenerateTenthLocationId() throws ConstraintViolations {
        Location loc = new Location();
        loc.setExtId("AKP0102009");
        List<Location> results = Arrays.asList(loc);
        when(genericDao.findListByPropertyPrefix(Location.class, "extId", "AKP0102", 1, false, true)).thenReturn(
                results);

        String generatedId = generator.generateId(location);

        assertEquals("AKP0102010", generatedId);
    }

    @Test
    public void shouldGenerateHundredthLocationId() throws ConstraintViolations {
        Location loc = new Location();
        loc.setExtId("AKP0102099");
        List<Location> results = Arrays.asList(loc);
        when(genericDao.findListByPropertyPrefix(Location.class, "extId", "AKP0102", 1, false, true)).thenReturn(
                results);

        String generatedId = generator.generateId(location);

        assertEquals("AKP0102100", generatedId);
    }

    @Test(expected = ConstraintViolations.class)
    public void shouldThrowExceptionWhenAllIdsUsed() throws ConstraintViolations {
        Location loc = new Location();
        loc.setExtId("AKP0102999");
        List<Location> results = Arrays.asList(loc);
        when(genericDao.findListByPropertyPrefix(Location.class, "extId", "AKP0102", 1, false, true)).thenReturn(
                results);

        generator.generateId(location);
    }
}
