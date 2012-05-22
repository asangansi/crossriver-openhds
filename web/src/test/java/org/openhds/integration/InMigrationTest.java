package org.openhds.integration;

import static org.junit.Assert.assertNotNull;
import java.util.Calendar;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openhds.controller.service.CurrentUser;
import org.openhds.dao.service.GenericDao;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.InMigration;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.Location;
import org.openhds.domain.model.SocialGroup;
import org.openhds.domain.model.Visit;
import org.openhds.domain.service.SitePropertiesService;
import org.openhds.domain.util.CalendarUtil;
import org.openhds.integration.util.JsfServiceMock;
import org.openhds.web.crud.impl.InMigrationCrudImpl;
import org.openhds.web.service.JsfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("/testContext.xml")
public class InMigrationTest {
	
	 @Autowired
	 @Qualifier("inMigrationCrud")
	 InMigrationCrudImpl inmigrationCrud;
	 	 	 
	 @Autowired
	 SessionFactory sessionFactory;
	 
	 @Autowired
	 GenericDao genericDao;
	 
	 @Autowired
	 SitePropertiesService siteProperties;
	 
	 @Autowired
	 JsfService jsfService;
	 
	 @Autowired
	 CalendarUtil calendarUtil;
	 
	 @Autowired
	 @Qualifier("currentUser")
	 CurrentUser currentUser;
	 
	 FieldWorker fieldWorker;
	 Individual individual;
	 SocialGroup socialGroup;
	 Location location;
	 Visit visit;
	 JsfServiceMock jsfServiceMock;
	 
	 @Before
	 public void setUp() {
		 
		 jsfServiceMock = (JsfServiceMock)jsfService;
		 currentUser.setProxyUser("admin", "test", new String[] {"VIEW_ENTITY", "CREATE_ENTITY"});
		 
		 fieldWorker = genericDao.findByProperty(FieldWorker.class, "extId", "FWEK1D");
		 individual = genericDao.findByProperty(Individual.class, "extId", "BRIHA001", false);
		 visit = genericDao.findByProperty(Visit.class, "extId", "VMBI01");
		 socialGroup = genericDao.findByProperty(SocialGroup.class, "extId", "SG01");
		 location = genericDao.findByProperty(Location.class, "extId", "MBI01");
	 }
	 
	 @Test
	 public void testInMigrationCreate() {
		 		 
		 InMigration inmig = new InMigration();
		 inmig.setIndividual(individual);
		 inmig.setCollectedBy(fieldWorker);
		 inmig.setRecordedDate(calendarUtil.getCalendar(Calendar.JANUARY, 4, 1990));
		 inmig.setMigTypeInternal();
		 inmig.setHouse(location);
		 inmig.setHousehold(socialGroup);
		 inmig.setEverRegistered(2);
		 inmig.setReferencesTemporaryIndividual(false);
		 inmig.setBIsToA("3");
		 inmig.setOrigin(1);
		 inmig.setReason(1);
		 inmig.setUnknownIndividual(false);
		 inmig.setVisit(visit);
		 
		 inmigrationCrud.setItem(inmig);
		 inmigrationCrud.create();
	     
	     InMigration savedInMig = genericDao.findByProperty(InMigration.class, "individual", individual, false);
		 assertNotNull(savedInMig);
	 }
}

