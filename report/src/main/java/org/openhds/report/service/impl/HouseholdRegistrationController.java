package org.openhds.report.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openhds.controller.service.IndividualService;
import org.openhds.controller.service.LocationHierarchyService;
import org.openhds.controller.service.ResidencyService;
import org.openhds.controller.service.VisitService;
import org.openhds.dao.service.GenericDao;
import org.openhds.dao.service.GenericDao.OrderProperty;
import org.openhds.dao.service.GenericDao.OrderPropertyBuilder;
import org.openhds.domain.model.Death;
import org.openhds.domain.model.InMigration;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.Location;
import org.openhds.domain.model.LocationHierarchy;
import org.openhds.domain.model.OutMigration;
import org.openhds.domain.model.PregnancyObservation;
import org.openhds.domain.model.PregnancyOutcome;
import org.openhds.domain.model.Relationship;
import org.openhds.domain.model.Residency;
import org.openhds.domain.model.Round;
import org.openhds.domain.model.Visit;
import org.openhds.domain.model.VisitableEntity;
import org.openhds.domain.service.SitePropertiesService;
import org.openhds.domain.util.CalendarUtil;
import org.openhds.report.beans.HouseholdRegisterBean;
import org.openhds.report.service.HouseholdRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HouseholdRegistrationController implements HouseholdRegistrationService {
	GenericDao genericDao;
	ResidencyService residencyService;
	LocationHierarchyService locationService;
	IndividualService individualService;
	VisitService visitService;
	SitePropertiesService properties;
	CalendarUtil calendarUtil;
	
	private final static String DEATH_EVENT_CODE = "DD";
	private final static String MIGRATION_OUT_CODE = "MA";
	private final static String MIGRATION_IN_CODE = "MI";
	private final static String NEW_BIRTH_CODE = "NB";
	private final static String PREGNANCY_CODE = "PG";
	
	@Autowired
	public HouseholdRegistrationController(GenericDao genericDao, ResidencyService residencyService, 
			LocationHierarchyService locationService, SitePropertiesService properties, CalendarUtil calendarUtil, 
			IndividualService individualService, VisitService visitService) {
		this.genericDao = genericDao;
		this.residencyService = residencyService;
		this.locationService = locationService;
		this.properties = properties;
		this.calendarUtil = calendarUtil;
		this.individualService = individualService;
		this.visitService = visitService;
	}
	
	@RequestMapping(value="/household-register.report", method=RequestMethod.GET)
	public ModelAndView getHouseholdRegistrationBook(@RequestParam(value = "region", defaultValue = "") String region) {
		if (StringUtils.isBlank(region)) {
			return new ModelAndView("redirect:/householdregister/index.faces");
		}
		ModelAndView mv = new ModelAndView("householdRegister");
		
		int currentRound = getCurrentRound();
		addRoundData(currentRound, mv);
		mv.addObject("myData", getHouseholds(currentRound - 1, region));
		
		return mv;
	}
	
	private int getCurrentRound() {
		OrderProperty order = new OrderProperty() {
			@Override
			public String getPropertyName() {
				return "roundNumber";
			}

			@Override
			public boolean isAscending() {
				return false;
			}
		};
		
		List<Round> round = genericDao.findAllWithOrder(Round.class, order);
		int roundNumber = 0;
		if (round.size() > 0) {
			roundNumber = round.get(0).getRoundNumber();
		}			
		
		return roundNumber;
	}
	
	private void addRoundData(int currentRound, ModelAndView mv) {
		mv.addObject("currentRnd", "R" + (currentRound - 1));
		mv.addObject("rnd1", "R" + currentRound);
		mv.addObject("rnd2", "R" + (currentRound + 1));
		mv.addObject("rnd3", "R" + (currentRound + 2));
	}

	private Collection<HouseholdRegisterBean> getHouseholds(int currentRnd, String region) {
		Collection<HouseholdRegisterBean> beans = new ArrayList<HouseholdRegisterBean>();
		
		// retrieve all locations
		// setup order properties
		OrderProperty locLevel = new OrderProperty() {

			public String getPropertyName() {
				return "locationLevel";
			}

			public boolean isAscending() {
				return true;
			}	
		};
		
		OrderProperty locExtId = new OrderProperty() {

			public String getPropertyName() {
				return "extId";
			}

			public boolean isAscending() {
				return true;
			}	
		};
		
		LocationHierarchy hierarhcyItem = genericDao.findByProperty(LocationHierarchy.class, "extId", region);
		List<Location> locations = genericDao.findListByPropertyWithOrder(Location.class, "locationLevel", hierarhcyItem, locLevel, locExtId);
		
		// iterate over the locations for this location hierarchy item
		// for each location, grab the events that were associated with the last visit(s) to this location
		for(Location loc : locations) {
			List<Individual> individuals = residencyService.getIndividualsByLocation(loc, true);
			List<Visit> visitsAtLocation = visitService.getAllVisitsAtLocationForRound(loc, currentRnd);
			
			for(Individual individual : individuals) {
				HouseholdRegisterBean bean = new HouseholdRegisterBean();
				
				VisitableEntity event = findEventForIndividual(individual, visitsAtLocation);
				if (VisitableEntity.NULL_VISITABLE_ENTITY == event && !individualLivingAtLocation(loc, individual)) {
					continue;
				} else {
					bean.setEventCode(getEventCode(event));	
				}

				bean.setLowestLevelValue(hierarhcyItem.getName());
				bean.setLocationId(loc.getExtId());
				bean.setFamilyName(loc.getLocationName());
				bean.setDob(calendarUtil.formatDate(individual.getDob()));
				bean.setFather(individual.getFather().getExtId());
				bean.setGender(individual.getGender());
				bean.setIndividualId(individual.getExtId());
				bean.setMother(individual.getMother().getExtId());
				if (individual.getAllRelationships().size() > 0 && individual.getGender().equals(properties.getFemaleCode())) {
					Relationship rel = individual.getAllRelationships().iterator().next();
					if (rel.getIndividualA().getExtId().equals(individual.getExtId())) {
						bean.setHusbId(rel.getIndividualB().getExtId());
					} else {
						bean.setHusbId(rel.getIndividualA().getExtId());
					}
				} else {
					bean.setHusbId("");
				}
				String name = individual.getFirstName();
				name += (individual.getMiddleName() == null ? "" : " " + individual.getMiddleName());
				name += " " + individual.getLastName();
				bean.setName(name);
				beans.add(bean);				
			}
		}
		return beans;
	}

	/**
	 * Determine whether an individuals current residency is at a particular location
	 * @param loc
	 * @param individual
	 * @return
	 */
	private boolean individualLivingAtLocation(Location loc, Individual individual) {
		Residency res = individual.getCurrentResidency();
		if (res.getEndDate() == null && res.getLocation().getExtId().equals(loc.getExtId())) {
			return true;
		}
		
		return false;
	}

	/**
	 * Retrieve the latest event for individual at a particular location
	 * NOTE: This is not the same as retrieving the most recent event for an individual
	 * This method considers the location when selecting the most recent event
	 * @param individual
	 * @param visits
	 * @return
	 */
	private VisitableEntity findEventForIndividual(Individual individual, List<Visit> visits) {
		if (visits.isEmpty()) {
			return VisitableEntity.NULL_VISITABLE_ENTITY;
		}
		Calendar latestEventDate = null;
		VisitableEntity latestEvent = null;
		
		// in migrations
		InMigration migrationIn = genericDao.findUniqueByInPropertyWithOrder(InMigration.class, "individual", individual, 
				"visit", visits, OrderPropertyBuilder.build("recordedDate", false), true);
		if (migrationIn != null && dateBefore(latestEventDate, migrationIn.getRecordedDate())) {
			latestEventDate = migrationIn.getRecordedDate();
			latestEvent = migrationIn;
		}
		
		// out migrations
		OutMigration migrationOut = genericDao.findUniqueByInPropertyWithOrder(OutMigration.class, "individual", individual, 
				"visit", visits, OrderPropertyBuilder.build("recordedDate", false), true);
		if (migrationOut != null && dateBefore(latestEventDate, migrationOut.getRecordedDate())) {
			latestEventDate = migrationOut.getRecordedDate();
			latestEvent = migrationOut;
		}
		
		// deaths
		Death death = genericDao.findUniqueByInPropertyWithOrder(Death.class, "individual", individual, 
				"visit", visits, OrderPropertyBuilder.build("recordedDate", false), true);
		if (death != null && dateBefore(latestEventDate, death.getRecordedDate())) {
			latestEventDate = death.getRecordedDate();
			latestEvent = death;
		}
		
		// pregnancy observations
		PregnancyObservation pregObs = genericDao.findUniqueByInPropertyWithOrder(PregnancyObservation.class, "mother", individual, 
				"visit", visits, OrderPropertyBuilder.build("recordedDate", false), true);
		if (pregObs != null && dateBefore(latestEventDate, pregObs.getRecordedDate())) {
			latestEventDate = pregObs.getRecordedDate();
			latestEvent = pregObs;
		}
		
		// pregnancy outcome
		PregnancyOutcome pregOutcome = genericDao.findUniqueByInPropertyWithOrder(PregnancyOutcome.class, "mother", individual, 
				"visit", visits, OrderPropertyBuilder.build("recordedDate", false), true);
		if (pregOutcome != null && dateBefore(latestEventDate, pregOutcome.getRecordedDate())) {
			latestEventDate = pregOutcome.getRecordedDate();
			latestEvent = pregOutcome;
		}
		
		if (latestEvent == null) {
			return VisitableEntity.NULL_VISITABLE_ENTITY;
		} else {
			return latestEvent;
		}
	}

	private boolean dateBefore(Calendar latestEventDate, Calendar recordedDate) {
		if (recordedDate != null) {
			return latestEventDate == null || recordedDate.after(latestEventDate);
		}
		
		return false;
	}

	private String getEventCode(VisitableEntity event) {
		if (event instanceof Death) {
			return DEATH_EVENT_CODE;
		}
		
		if (event instanceof InMigration) {
			return MIGRATION_IN_CODE;
		}
		
		if (event instanceof OutMigration) {
			return MIGRATION_OUT_CODE;
		}
		
		if (event instanceof PregnancyObservation) {
			return PREGNANCY_CODE;
		}
		
		if (event instanceof PregnancyOutcome) {
			return NEW_BIRTH_CODE;
		}
		
		return "";
	}
}

