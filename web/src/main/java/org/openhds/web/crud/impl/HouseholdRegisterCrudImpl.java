package org.openhds.web.crud.impl;

import java.io.IOException;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.openhds.controller.service.LocationHierarchyService;
import org.openhds.dao.service.GenericDao;
import org.openhds.dao.service.GenericDao.OrderPropertyBuilder;
import org.openhds.domain.model.Location;
import org.openhds.domain.model.LocationHierarchy;
import org.openhds.domain.model.LocationHierarchyLevel;
import org.openhds.domain.model.Round;

public class HouseholdRegisterCrudImpl {

	private GenericDao genericDao;
	private LocationHierarchyService locationService;
	
	///openhds/household-register.report
	String round;
	String selectedLocation;
	SelectItem[] locations;

	public HouseholdRegisterCrudImpl(GenericDao genericDao, LocationHierarchyService locationService) {
		this.genericDao = genericDao;
		this.locationService = locationService;
	}
	
	public String startHouseholdRegistration() {
		List<Round> persistedRounds = genericDao.findAllWithOrder(Round.class, OrderPropertyBuilder.build("roundNumber", false));
		if (persistedRounds.size() == 0) {
			round = "1";
		} else {
			round = persistedRounds.get(0).getRoundNumber().toString();
		}
		
		LocationHierarchyLevel level = locationService.getLowestLevel();
		List<LocationHierarchy> villages = genericDao.findListByProperty(LocationHierarchy.class, "level", level);
		locations = new SelectItem[villages.size()];
		for(int i = 0; i < villages.size(); i++) {
			locations[i] = new SelectItem(villages.get(i).getExtId(), villages.get(i).getName());
			if (i == 0) {
				selectedLocation = (String) locations[i].getValue();
			}
		}
		
		return "household_reg";
	}
	
	public String generateReport() {
		String context = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(context  + "/household-register.report?region=" + selectedLocation);
		} catch (IOException e) {
		}
		
		return null;
	}

	public String getSelectedLocation() {
		return selectedLocation;
	}

	public void setSelectedLocation(String selectedLocation) {
		this.selectedLocation = selectedLocation;
	}

	public SelectItem[] getLocations() {
		return locations;
	}

	public void setLocations(SelectItem[] locations) {
		this.locations = locations;
	}

	public String getRound() {
		if (round == null) {
			startHouseholdRegistration();
		}
		
		return round;
	}

	public void setRound(String round) {
		this.round = round;
	}
}
