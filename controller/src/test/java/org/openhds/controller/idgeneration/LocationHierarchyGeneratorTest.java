package org.openhds.controller.idgeneration;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.dao.service.GenericDao;
import org.openhds.dao.service.LocationHierarchyDao;
import org.openhds.domain.model.LocationHierarchy;

public class LocationHierarchyGeneratorTest {

    @Mock
    LocationHierarchyDao locationHierarchyDao;
    
    @Mock
    GenericDao genericDao;

    private LocationHierarchyGenerator generator;

    private LocationHierarchy region;

    @Before
    public void setUp() {
        initMocks(this);
        generator = new LocationHierarchyGenerator();
        generator.setLocationHierarchyDao(locationHierarchyDao);
        generator.setGenericDao(genericDao);

        LocationHierarchy root = new LocationHierarchy();
        root.setExtId("HIERARCHY_ROOT");

        region = new LocationHierarchy();
        region.setExtId("AKP");
        region.setParent(root);

    }

    @Test
    public void shouldUseLettersForFirstLevel() throws ConstraintViolations {
        LocationHierarchy root = new LocationHierarchy();
        root.setExtId("HIERARCHY_ROOT");

        LocationHierarchy region = new LocationHierarchy();
        region.setName("Akpabuyo");
        region.setParent(root);

        String id = generator.generateId(region);

        assertEquals("AKP", id);
    }

    @Test
    public void shouldUseDigitForSecondLevel() throws ConstraintViolations {
        LocationHierarchy ward = new LocationHierarchy();
        ward.setName("Ward");
        ward.setParent(region);

        String id = generator.generateId(ward);

        assertEquals("AKP01", id);
    }

    @Test
    public void shouldUseDigitForThirdLevel() throws ConstraintViolations {
        LocationHierarchy ward = new LocationHierarchy();
        ward.setExtId("AKP01");

        LocationHierarchy ea = new LocationHierarchy();
        ea.setName("Enumeration Area");
        ea.setParent(ward);

        String id = generator.generateId(ea);

        assertEquals("AKP0101", id);
    }

    @Test
    public void shouldUseNextDigitForSecondLevel() throws ConstraintViolations {
        LocationHierarchy ward = new LocationHierarchy();
        ward.setParent(region);

        LocationHierarchy hierarchy = new LocationHierarchy();
        hierarchy.setExtId("AKP03");
        when(locationHierarchyDao.findLocationHierarchyWithHighestIdAtLevel("AKP", region)).thenReturn(hierarchy);

        String id = generator.generateId(ward);

        assertEquals("AKP04", id);
    }

    @Test
    public void shouldUseNextDigitForThirdLevel() throws ConstraintViolations {
        LocationHierarchy ward = new LocationHierarchy();
        ward.setExtId("AKP01");

        LocationHierarchy ea = new LocationHierarchy();
        ea.setParent(ward);

        LocationHierarchy hierarchy = new LocationHierarchy();
        hierarchy.setExtId("AKP0104");
        when(locationHierarchyDao.findLocationHierarchyWithHighestIdAtLevel("AKP01", ward)).thenReturn(hierarchy);

        String id = generator.generateId(ea);

        assertEquals("AKP0105", id);
    }
}
