package org.openhds.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.controller.idgeneration.Generator;
import org.openhds.controller.idgeneration.IdScheme;
import org.openhds.controller.idgeneration.IdSchemeResource;
import org.openhds.controller.idgeneration.IndividualGenerator;
import org.openhds.controller.idgeneration.LocationGenerator;
import org.openhds.controller.idgeneration.SocialGroupGenerator;
import org.openhds.controller.service.BaselineService;
import org.openhds.controller.service.IndividualService;
import org.openhds.controller.service.LocationHierarchyService;
import org.openhds.controller.service.ResidencyService;
import org.openhds.dao.service.GenericDao;
import org.openhds.domain.model.AuditableCollectedEntity;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.Location;
import org.openhds.domain.model.LocationHierarchy;
import org.openhds.domain.model.Membership;
import org.openhds.domain.model.Relationship;
import org.openhds.domain.model.SocialGroup;
import org.openhds.domain.service.SitePropertiesService;

public class BaselineBean implements Serializable {

	private static final String PHASE_1 = "PHASE_1";
	private static final String PHASE_2 = "PHASE_2";
	private static final String PHASE_3 = "PHASE_3";
	private static final String PHASE_4 = "PHASE_4";

	public static final String TEMPORARY_RELATIONSHIP_EXT_ID = "DO_NOT_USE";

	private static final long serialVersionUID = 2721358112424491641L;

	private static final String ID_ERROR = "The Individual Id could not be found. Please enter the Individual's first and last name.";
	private static final int MAX_HOUSEHOLD_SCHEDULE_SIZE = 20;

	LocationHierarchy currentVillage = new LocationHierarchy();
	Location currentLocation = new Location();
	SocialGroup currentSocialGroup = new SocialGroup();
	Individual headOfHouse;
	Individual headOfHousehold;
	Individual respondent;
	FieldWorker collectedBy;

	// used for manual conversion between Date and Calendar since the openFaces
	// Calendar doesn't support JSF Converters
	Date entryDate;
	WrappedDate[] dobList = initializeDateList();

	GenericDao genericDao;
	ResidencyService residencyService;
	BaselineService baselineService;
	IndividualService individualService;
	LocationHierarchyService locationService;
	
	Generator<SocialGroup> socialGroupGenerator;
	Generator<Location> locationGenerator;
	Generator<Individual> individualGenerator;
	IdSchemeResource idResource;
	SitePropertiesService properties;

	String hierarchyId;
	String houseId;
	String headOfHouseId;
	String householdId;
	String headOfHouseholdId;
	String respondentId;
	String headOfHouseFName;
	String headOfHouseLName;
	String headOfHouseholdFName;
	String headOfHouseholdLName;
	String respondentFName;
	String respondentLName;

	String msgHeadOfHouseNotFound = null;
	String msgHouseholdHeadNotFound = null;
	String msgRespondentNotFound = null;
	boolean isNewHouse = false;
	boolean houseInfoComplete = false;
	boolean householdInfoComplete = false;
	boolean householdSchedule = false;
	boolean householdScheduleComplete = false;
	
	// flags for signaling validation errors in ids.
	// they must be checked against the id schemes
	// because they are being entered manually
	boolean houseErrorsFound = false;
	boolean indivErrorsHouseFound = false;
	boolean indivErrorsHouseholdFound = false;
	boolean respondentErrorsFound = false;
	
	// flags for signaling that the house, household and respondent
	// are ready to be persisted. Once set to true, the
	// create house and household buttons will be enabled.
	boolean houseHeadValid = false;
	boolean householdHeadValid = false;	
	boolean respondentValid = false;
	
	// the lastPersistedRowIndex will contain the value of the highest index for the
	// row in the household schedule that was most recently saved. Any value that is
	// equal to or less than this index can be assumed to have already been saved.
	// For example, if lastPersistedRowIndex = 5, then rows 1, 2, 3, 4, 5 can be
	// assumed to have already been saved.
	// NOTE: the index starts at 1, NOT 0
	int lastPersistedRowIndex = 0;

	Individual[] individuals = new Individual[MAX_HOUSEHOLD_SCHEDULE_SIZE];
	int individualCount = 1;
	Membership[] memberships = new Membership[MAX_HOUSEHOLD_SCHEDULE_SIZE];
	Relationship[] relationships = new Relationship[MAX_HOUSEHOLD_SCHEDULE_SIZE];

	public void clear() {
		clearHouse();
		clearHousehold();
		currentVillage = new LocationHierarchy();
		collectedBy = null;
		dobList = null;
		hierarchyId = null;
		householdId = null;
		headOfHousehold = null;
		houseId = null;
		householdSchedule = false;
		householdScheduleComplete = false;
		houseErrorsFound = false;
		indivErrorsHouseFound = false;
		indivErrorsHouseholdFound = false;
		houseHeadValid = false;
		householdHeadValid = false;
		individualCount = 1;
		lastPersistedRowIndex = 0;
		dobList = initializeDateList();
	}

	public void clearHouse() {
		currentLocation = new Location();
		headOfHouseId = null;
		headOfHouseFName = null;
		headOfHouseLName = null;
		msgHeadOfHouseNotFound = null;
		houseInfoComplete = false;
		houseErrorsFound = false;
		indivErrorsHouseFound = false;
		houseHeadValid = false;
	}

	public void clearHousehold() {
		currentSocialGroup = new SocialGroup();
		respondent = new Individual();
		headOfHouseholdId = null;
		respondentId = null;
		headOfHouseholdFName = null;
		headOfHouseholdLName = null;
		respondentFName = null;
		respondentLName = null;
		entryDate = null;
		msgHouseholdHeadNotFound = null;
		msgRespondentNotFound = null;
		householdInfoComplete = false;
		indivErrorsHouseholdFound = false;
		householdHeadValid = false;
		respondentValid = false;
	}
	
	private WrappedDate[] initializeDateList() {

		dobList = new WrappedDate[MAX_HOUSEHOLD_SCHEDULE_SIZE];

		for (int i = 0; i < dobList.length; i++) {
			dobList[i] = new WrappedDate();
		}
		return dobList;
	}

	private void intitializeHouseholdSchedule() {
		addRowToHouseholdSchedule(0);
		householdSchedule = true;
	}

	private void addRowToHouseholdSchedule(int rowIndex) {
		individuals[rowIndex] = new Individual();
		memberships[rowIndex] = new Membership();
		relationships[rowIndex] = new Relationship();
		dobList[rowIndex] = new WrappedDate();
	}

	/**
	 * Called by the Add Individual button on Household Schedule
	 * Attempts to persist the last filled in row in the household schedule table
	 */
	public String addIndividual() {
		// if the user has removed the last row, this leaves the table with a collection of
		// already persisted rows. In this case, there is no need to save the rows since
		// they have already been persisted.
		if (isCurrentRowAlreadyPersisted()) { 
			addRowToHouseholdSchedule(individualCount);
			individualCount++;
			return null;
		}
		
		if (!isHouseholdScheduleFilled()) {
			if (savePreviousRow()) {
				lastPersistedRowIndex++;

				// its possible for the household schedule to be filled
				// after saving the previous row. that is why this
				// check is here
				if (!isHouseholdScheduleFilled()) {
					addRowToHouseholdSchedule(individualCount);
					individualCount++;
				}			

			}
		}
		
		return null;
	}

	public boolean isCurrentRowAlreadyPersisted() {
		return lastPersistedRowIndex == individualCount;
	}
	
	public boolean isHouseholdScheduleFilled() {
		return lastPersistedRowIndex == MAX_HOUSEHOLD_SCHEDULE_SIZE;
	}
	
	private boolean savePreviousRow() {
		Individual individual = individuals[individualCount - 1];
		Individual indiv = genericDao.findByProperty(Individual.class, "extId", individual.getExtId());
				
		// this id must be validated in baseline since the head of house id
		// is formed from fields that are not included in the individual entity
		if (!individual.getExtId().contains(householdId)) {
			FacesMessage message = new FacesMessage("The Perm id must contain " + householdId);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage("headOfHouseholdId", message);
			return false;
		}

		try {
			
			IndividualGenerator indivGen = (IndividualGenerator)individualGenerator;
			individualGenerator.validateIdLength(individual.getExtId(), indivGen.getIdScheme());

			checkDuplicateIndividual(indiv);

			if (indiv == null)
				indiv = new Individual();

			indiv.setExtId(individual.getExtId());
			indiv.setFirstName(individual.getFirstName());
			indiv.setLastName(individual.getLastName());
			indiv.setDob(convertedEntryDate(dobList[individualCount - 1]
					.getDate()));
			indiv.setDobAspect(individual.getDobAspect());
			indiv.setGender(individual.getGender());
			indiv.setWorkStatus(individual.getWorkStatus());
			indiv.setEducationalStatus(individual.getEducationalStatus());
			indiv.setOccupationalStatus(individual.getOccupationalStatus());
			indiv.setMaritalStatus(individual.getMaritalStatus());
			indiv.setMother(individual.getMother());
			indiv.setFather(individual.getFather());

			individual = indiv;
			individual.setCollectedBy(collectedBy);

			if (individual.getExtId().equals(headOfHousehold.getExtId())) {
				
				// create Membership
				Membership membership = new Membership();
				membership.setEndType(properties.getNotApplicableCode());
				membership.setStartType(properties.getEnumerationCode());
				baselineService.createMembershipForIndividual(individual, 
						membership, currentSocialGroup, 
						collectedBy, convertedEntryDate(entryDate));
				baselineService.createResidencyForIndividual(individual,
						currentLocation, collectedBy,
						convertedEntryDate(entryDate));
			} else {
				currentSocialGroup = genericDao.findByProperty(SocialGroup.class,
						"extId", currentSocialGroup.getExtId());

				Membership membership = memberships[individualCount - 1];
				membership.setIndividual(individual);
				membership.setSocialGroup(currentSocialGroup);
				membership.setCollectedBy(collectedBy);
				membership.setStartDate(convertedEntryDate(entryDate));
				membership.setStartType(properties.getEnumerationCode());
				membership.setEndType(properties.getNotApplicableCode());

				Relationship relationship = relationships[individualCount - 1];

				if (relationship.getIndividualB().getExtId().equals(
						TEMPORARY_RELATIONSHIP_EXT_ID)) {
					baselineService.createResidencyAndMembershipForIndividual(
							individual, membership, currentLocation,
							collectedBy, convertedEntryDate(entryDate));
				} else {
					relationship.setIndividualA(individual);
					relationship.setStartDate(convertedEntryDate(entryDate));
					relationship.setCollectedBy(collectedBy);
					relationship.setaIsToB("2");
					relationship.setEndType(properties.getNotApplicableCode());
					baselineService
							.createResidencyMembershipAndRelationshipForIndividual(
									individual, membership, relationship,
									currentLocation, collectedBy,
									convertedEntryDate(entryDate));
				}
			}
		}

		catch (Exception e) {
			FacesMessage message = new FacesMessage(e.getMessage());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);

			return false;
		}

		return true;
	}

	private void checkDuplicateIndividual(Individual indiv)
			throws ConstraintViolations {
		// the indiv must be the Head of the Location or Household
		// must check if the id's are not in conflict
		if (indiv != null) {
			for (int i = 0; i < individualCount - 1; i++) {
				if (individuals[i].getExtId().equals(indiv.getExtId()))
					throw new ConstraintViolations(
							"The Id specified is in conflict with an existing Id entered in the Household Schedule.");
			}
		}
	}

	public void createHouseholdSchedule() {
		if (isCurrentRowAlreadyPersisted()) {
			householdScheduleComplete = true;
			return;
		}
		
		if (savePreviousRow()) {
			householdScheduleComplete = true;
		}
	}

	public void createNewHouse() throws ConstraintViolations {

		try {
			
			List<AuditableCollectedEntity> entitiesList = new ArrayList<AuditableCollectedEntity>();
			IndividualGenerator indivGen = (IndividualGenerator)individualGenerator;
			individualGenerator.validateIdLength(headOfHouseId, indivGen.getIdScheme());
			
			houseErrorsFound = false;
			
			Individual head = null;
			if (headOfHouse == null) {

				// create House Head
				head = new Individual();
				head.setFirstName(headOfHouseFName);
				head.setLastName(headOfHouseLName);
				head.setExtId(headOfHouseId);
				head.setMother(individualService.findIndivById(properties.getUnknownIdentifier()));
				head.setFather(individualService.findIndivById(properties.getUnknownIdentifier()));
				head.setGender(properties.getUnknownIdentifier());
				head.setCollectedBy(collectedBy);
				head.setInsertDate(Calendar.getInstance());
				entitiesList.add(head);
				currentLocation.setLocationHead(head);
			} else
				currentLocation.setLocationHead(headOfHouse);

			// create location
			currentLocation.setCollectedBy(collectedBy);
			currentLocation.setExtId(houseId);
			currentLocation.setInsertDate(Calendar.getInstance());	
			entitiesList.add(currentLocation);
			
			baselineService.createEntities(entitiesList);
			
			isNewHouse = false;
			houseInfoComplete = true;

			setHouseholdInformation();

		} catch (Exception e) {
			houseErrorsFound = true;
			FacesMessage message = new FacesMessage(e.getMessage());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public void createNewHousehold() throws ConstraintViolations {

		try {
			List<AuditableCollectedEntity> entitiesList = new ArrayList<AuditableCollectedEntity>();
			IndividualGenerator indivGen = (IndividualGenerator)individualGenerator;
			individualGenerator.validateIdLength(headOfHouseholdId, indivGen.getIdScheme());
		
			if (headOfHousehold == null) {

				// create House Head
				Individual head = new Individual();
				head.setFirstName(headOfHouseholdFName);
				head.setLastName(headOfHouseholdLName);
				head.setExtId(headOfHouseholdId);
				head.setMother(individualService.findIndivById(properties.getUnknownIdentifier()));
				head.setFather(individualService.findIndivById(properties.getUnknownIdentifier()));
				head.setGender(properties.getUnknownIdentifier());
				head.setCollectedBy(collectedBy);
				head.setInsertDate(Calendar.getInstance());
				entitiesList.add(head);
				currentSocialGroup.setGroupHead(head);
				headOfHousehold = head;
			} else
				currentSocialGroup.setGroupHead(headOfHousehold);
			
			if (headOfHouseholdId.equals(respondentId)) 
				respondent = headOfHousehold;

			// create SocialGroup
			if (respondent == null) {
				respondent = new Individual();
				respondent.setFirstName(respondentFName);
				respondent.setLastName(respondentLName);
				respondent.setExtId(respondentId);
				respondent.setMother(individualService.findIndivById(properties.getUnknownIdentifier()));
				respondent.setFather(individualService.findIndivById(properties.getUnknownIdentifier()));
				respondent.setGender(properties.getUnknownIdentifier());
				respondent.setCollectedBy(collectedBy);
				respondent.setInsertDate(Calendar.getInstance());
				entitiesList.add(respondent);
				currentSocialGroup.setRespondent(respondent);
			}
			else 
				currentSocialGroup.setRespondent(respondent);

			currentSocialGroup.setCollectedBy(collectedBy);
			currentSocialGroup.setExtId(householdId);
			currentSocialGroup.setInsertDate(Calendar.getInstance());
			currentSocialGroup.setGroupType("FAM");	
			entitiesList.add(currentSocialGroup);
			baselineService.createEntities(entitiesList);
					
			householdInfoComplete = true;
			intitializeHouseholdSchedule();
		} catch (Exception e) {
			FacesMessage message = new FacesMessage(e.getMessage());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public Calendar convertedEntryDate(Date date) {
		if (date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal;
		}
		return null;
	}
	
	public void hierarchyIdChange(ValueChangeEvent event) {
		
		hierarchyId = (String)event.getNewValue();
		
		if (!locationService.checkValidLocationEntry(hierarchyId)) {
			FacesMessage message = new FacesMessage("The " + locationService.getLowestLevel().getName() + 
		    	" specified is not the lowest level in the Location Hierarchy.");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage("villageId", message);
		}
		else 
			currentVillage = genericDao.findByProperty(LocationHierarchy.class, "extId", hierarchyId);
	}
	
	public void houseIdChange(ValueChangeEvent event) {
    	houseId = (String) event.getNewValue();
    	
    	try {
			LocationGenerator locGen = (LocationGenerator)locationGenerator;
			locationGenerator.validateIdLength(houseId, locGen.getIdScheme());
	    		
    		houseErrorsFound = false;
	    	currentLocation = genericDao.findByProperty(Location.class, "extId", houseId);
	
	    	if (currentLocation == null) {
				currentLocation = new Location();	
				currentLocation.setLocationLevel(currentVillage);
				currentLocation.setExtId(houseId);
				locGen.validateId(currentLocation);
				isNewHouse = true;
			}
	    	
	    	setHeadOfLocationInformation();
    	} catch (Exception e) {
    		houseErrorsFound = true;
			FacesMessage message = new FacesMessage(e.getMessage());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage("houseId", message);
		}
    }

	public void headOfHouseIdChange(ValueChangeEvent event) {

		headOfHouseId = (String) event.getNewValue();
		
		try {
			IndividualGenerator indivGen = (IndividualGenerator)individualGenerator;
			individualGenerator.validateIdLength(headOfHouseId, indivGen.getIdScheme());
			
			// this id must be validated in baseline since the head of house id
			// is formed from fields that are not included in the individual entity
			if (!headOfHouseId.contains(houseId)) {
				FacesMessage message = new FacesMessage("The Head of House id must contain " + houseId);
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage("headOfHouseId", message);
				indivErrorsHouseFound = true;
				return;
			}
			
			indivErrorsHouseFound = false;
			headOfHouse = genericDao.findByProperty(Individual.class, "extId",
					headOfHouseId);

			currentLocation.setLocationLevel(currentVillage);
			currentLocation.setLocationHead(headOfHouse);

	    	houseHeadValid = true;
			if (headOfHouse == null) {
				msgHeadOfHouseNotFound = ID_ERROR;
			} else {
				headOfHouseFName = headOfHouse.getFirstName();
				headOfHouseLName = headOfHouse.getLastName();
				msgHeadOfHouseNotFound = null;
			}	
		} 
		catch (Exception e) {
			indivErrorsHouseFound = true;
			FacesMessage message = new FacesMessage(e.getMessage());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage("headOfHouseId", message);
		}
	}
	
	public void headOfHouseholdIdChange(ValueChangeEvent event) {

		headOfHouseholdId = (String) event.getNewValue();
		
		try {
			IndividualGenerator indivGen = (IndividualGenerator)individualGenerator;
			individualGenerator.validateIdLength(headOfHouseholdId, indivGen.getIdScheme());
			
			// this id must be validated in baseline since the head of house id
			// is formed from fields that are not included in the individual entity
			if (!headOfHouseholdId.contains(householdId)) {
				FacesMessage message = new FacesMessage("The Head of House id must contain " + householdId);
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage("headOfHouseholdId", message);
				indivErrorsHouseholdFound = true;
				return;
			}
			
			indivErrorsHouseholdFound = false;
			headOfHousehold = genericDao.findByProperty(Individual.class, "extId", headOfHouseholdId);

			currentSocialGroup.setGroupHead(headOfHousehold);

			householdHeadValid = true;
			if (headOfHousehold == null) {
				msgHouseholdHeadNotFound = ID_ERROR;
			} else {
				headOfHouseholdFName = headOfHousehold.getFirstName();
				headOfHouseholdLName = headOfHousehold.getLastName();
				msgHouseholdHeadNotFound = null;
			}
		}
		catch (Exception e) {
			indivErrorsHouseholdFound = true;
			FacesMessage message = new FacesMessage(e.getMessage());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage("headOfHouseholdId", message);
		}
	}
	
	public void respondentIdValueChanged(ValueChangeEvent event) {
		
		respondentId = (String)event.getNewValue();
		
		try {
			IndividualGenerator indivGen = (IndividualGenerator)individualGenerator;
			individualGenerator.validateIdLength(respondentId, indivGen.getIdScheme());
			
			respondent = individualService.findIndivById(respondentId);
			currentSocialGroup.setRespondent(respondent);
			
			respondentErrorsFound = false;
			respondentValid = true;
			if (respondent == null) {
				msgRespondentNotFound = ID_ERROR;
			} else {
				respondentFName = respondent.getFirstName();
				respondentLName = respondent.getLastName();
				msgRespondentNotFound = null;
			}
			
		} catch (Exception e) {
			respondentErrorsFound = true;
			FacesMessage message = new FacesMessage(e.getMessage());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage("idOfRespondent", message);
		}
	}

	private void setHeadOfLocationInformation() {
		if (currentLocation.getLocationHead() != null) {
			Individual locationHead = currentLocation.getLocationHead();
			headOfHouseId = locationHead.getExtId();
			headOfHouseFName = locationHead.getFirstName();
			headOfHouseLName = locationHead.getLastName();
			houseInfoComplete = true;
			setHouseholdInformation();
		}
	}

	@SuppressWarnings("unchecked")
	private void setHouseholdInformation() {

		currentSocialGroup.setExtId(houseId);
		IdScheme scheme = idResource.getIdSchemeByName("SocialGroup");

		SocialGroupGenerator sgGen = (SocialGroupGenerator) socialGroupGenerator;

		try {
			householdId = houseId.concat(sgGen.buildNumberWithBound(currentSocialGroup, scheme));
		} catch (ConstraintViolations e) {
			System.out.println(e.getMessage());
		}
	}
	
	public String removeLastRow() {
		individualCount--;
		return null;
	}

	public String getBaselinePhase() {
		if (villageInformationHasNotBeenCompleted()) {
			return PHASE_1;
		}

		if (houseInformationHasNotBeenCompleted()) {
			return PHASE_2;
		}

		if (householdInformationHasNotBeenCompleted()) {
			return PHASE_3;
		} else {
			return PHASE_4;
		}
	}

	private boolean villageInformationHasNotBeenCompleted() {
		if (currentVillage.getName() == null || collectedBy == null)
			return true;
		return false;
	}

	private boolean houseInformationHasNotBeenCompleted() {
		return currentLocation == null || !houseInfoComplete;
	}

	private boolean householdInformationHasNotBeenCompleted() {
		return currentSocialGroup == null || !householdInfoComplete;
	}

	public GenericDao getGenericDao() {
		return genericDao;
	}

	public void setGenericDao(GenericDao genericDao) {
		this.genericDao = genericDao;
	}

	public Location getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}
	
	public SocialGroup getCurrentSocialGroup() {
		return currentSocialGroup;
	}

	public void setCurrentSocialGroup(SocialGroup currentSocialGroup) {
		this.currentSocialGroup = currentSocialGroup;
	}

	public ResidencyService getResidencyService() {
		return residencyService;
	}

	public void setResidencyService(ResidencyService residencyService) {
		this.residencyService = residencyService;
	}

	public FieldWorker getCollectedBy() {
		return collectedBy;
	}

	public void setCollectedBy(FieldWorker collectedBy) {
		this.collectedBy = collectedBy;
	}

	public BaselineService getBaselineService() {
		return baselineService;
	}

	public void setBaselineService(BaselineService baselineService) {
		this.baselineService = baselineService;
	}

	public IndividualService getIndividualService() {
		return individualService;
	}

	public void setIndividualService(IndividualService individualService) {
		this.individualService = individualService;
	}
	
	public LocationHierarchyService getLocationService() {
		return locationService;
	}

	public void setLocationService(LocationHierarchyService locationService) {
		this.locationService = locationService;
	}

	public LocationHierarchy getCurrentVillage() {
		return currentVillage;
	}

	public void setCurrentVillage(LocationHierarchy currentVillage) {
		this.currentVillage = currentVillage;
	}
	
	public String getHierarchyId() {
		return hierarchyId;
	}

	public void setHierarchyId(String hierarchyId) {
		this.hierarchyId = hierarchyId;
	}

	public String getHouseId() {
		return houseId;
	}

	public void setHouseId(String houseId) {
		this.houseId = houseId;
	}

	public boolean getIsNewHouse() {
		return isNewHouse;
	}

	public boolean isHouseInfoComplete() {
		return houseInfoComplete;
	}

	public boolean isHouseholdInfoComplete() {
		return householdInfoComplete;
	}

	public boolean isHouseholdSchedule() {
		return householdSchedule;
	}

	public void setHouseholdSchedule(boolean householdSchedule) {
		this.householdSchedule = householdSchedule;
	}

	public void setHouseholdInfoComplete(boolean householdInfoComplete) {
		this.householdInfoComplete = householdInfoComplete;
	}

	public String getHeadOfHouseFName() {
		return headOfHouseFName;
	}

	public void setHeadOfHouseFName(String headOfHouseFName) {
		this.headOfHouseFName = headOfHouseFName;
	}

	public String getHeadOfHouseLName() {
		return headOfHouseLName;
	}

	public void setHeadOfHouseLName(String headOfHouseLName) {
		this.headOfHouseLName = headOfHouseLName;
	}
	
	public String getHeadOfHouseId() {
		return headOfHouseId;
	}

	public void setHeadOfHouseId(String headOfHouseId) {
		this.headOfHouseId = headOfHouseId;
	}

	public String getHouseholdId() {
		return householdId;
	}

	public void setHouseholdId(String householdId) {
		this.householdId = householdId;
	}

	public String getHeadOfHouseholdId() {
		return headOfHouseholdId;
	}
	
	public String getRespondentId() {
		return respondentId;
	}

	public void setRespondentId(String respondentId) {
		this.respondentId = respondentId;
	}

	public void setHeadOfHouseholdId(String headOfHouseholdId) {
		this.headOfHouseholdId = headOfHouseholdId;
	}

	public Individual getHeadOfHousehold() {
		return headOfHousehold;
	}

	public void setHeadOfHousehold(Individual headOfHousehold) {
		this.headOfHousehold = headOfHousehold;
	}

	public String getHeadOfHouseholdFName() {
		return headOfHouseholdFName;
	}

	public void setHeadOfHouseholdFName(String headOfHouseholdFName) {
		this.headOfHouseholdFName = headOfHouseholdFName;
	}

	public String getHeadOfHouseholdLName() {
		return headOfHouseholdLName;
	}

	public void setHeadOfHouseholdLName(String headOfHouseholdLName) {
		this.headOfHouseholdLName = headOfHouseholdLName;
	}
	
	public String getRespondentFName() {
		return respondentFName;
	}

	public void setRespondentFName(String respondentFName) {
		this.respondentFName = respondentFName;
	}

	public String getRespondentLName() {
		return respondentLName;
	}

	public void setRespondentLName(String respondentLName) {
		this.respondentLName = respondentLName;
	}
	
	public Individual getRespondent() {
		return respondent;
	}

	public void setRespondent(Individual respondent) {
		this.respondent = respondent;
	}
		
	public Individual getHeadOfHouse() {
		return headOfHouse;
	}

	public void setHeadOfHouse(Individual headOfHouse) {
		this.headOfHouse = headOfHouse;
	}

	public String getMsgHeadOfHouseNotFound() {
		return msgHeadOfHouseNotFound;
	}

	public void setMsgHeadOfHouseNotFound(String msgHeadOfHouseNotFound) {
		this.msgHeadOfHouseNotFound = msgHeadOfHouseNotFound;
	}

	public String getMsgHouseholdHeadNotFound() {
		return msgHouseholdHeadNotFound;
	}

	public void setMsgHouseholdHeadNotFound(String msgHouseholdHeadNotFound) {
		this.msgHouseholdHeadNotFound = msgHouseholdHeadNotFound;
	}
	
	public String getMsgRespondentNotFound() {
		return msgRespondentNotFound;
	}

	public void setMsgRespondentNotFound(String msgRespondentNotFound) {
		this.msgRespondentNotFound = msgRespondentNotFound;
	}

	public Generator getSocialGroupGenerator() {
		return socialGroupGenerator;
	}

	public void setSocialGroupGenerator(Generator socialGroupGenerator) {
		this.socialGroupGenerator = socialGroupGenerator;
	}

	public Generator getLocationGenerator() {
		return locationGenerator;
	}

	public void setLocationGenerator(Generator locationGenerator) {
		this.locationGenerator = locationGenerator;
	}
	
	public Generator getIndividualGenerator() {
		return individualGenerator;
	}

	public void setIndividualGenerator(Generator individualGenerator) {
		this.individualGenerator = individualGenerator;
	}

	public SitePropertiesService getProperties() {
		return properties;
	}

	public void setProperties(SitePropertiesService properties) {
		this.properties = properties;
	}

	public Individual[] getIndividuals() {
		return individuals;
	}

	public void setIndividuals(Individual[] individuals) {
		this.individuals = individuals;
	}

	public Membership[] getMemberships() {
		return memberships;
	}

	public void setMemberships(Membership[] memberships) {
		this.memberships = memberships;
	}

	public Relationship[] getRelationships() {
		return relationships;
	}

	public void setRelationships(Relationship[] relationships) {
		this.relationships = relationships;
	}

	public int getIndividualCount() {
		return individualCount;
	}

	public void setIndividualCount(int individualCount) {
		this.individualCount = individualCount;
	}

	public WrappedDate[] getDobList() {
		return dobList;
	}

	public void setDobList(WrappedDate[] dobList) {
		this.dobList = dobList;
	}

	public Date getEntryDate() {
		if (entryDate == null || currentSocialGroup == null) {
			return new Date();
		}

		return currentSocialGroup.getDateOfInterview().getTime();
	}

	public void setEntryDate(Date entryDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(entryDate);
		currentSocialGroup.setDateOfInterview(cal);
		this.entryDate = entryDate;
	}

	public boolean isHouseholdScheduleComplete() {
		return householdScheduleComplete;
	}

	public void setHouseholdScheduleComplete(boolean householdScheduleComplete) {
		this.householdScheduleComplete = householdScheduleComplete;
	}
	
	public boolean isIndivErrorsHouseFound() {
		return indivErrorsHouseFound;
	}

	public void setIndivErrorsHouseFound(boolean indivErrorsHouseFound) {
		this.indivErrorsHouseFound = indivErrorsHouseFound;
	}
	
	public boolean isIndivErrorsHouseholdFound() {
		return indivErrorsHouseholdFound;
	}

	public void setIndivErrorsHouseholdFound(boolean indivErrorsHouseholdFound) {
		this.indivErrorsHouseholdFound = indivErrorsHouseholdFound;
	}
	
	public boolean isHouseErrorsFound() {
		return houseErrorsFound;
	}

	public void setHouseErrorsFound(boolean houseErrorsFound) {
		this.houseErrorsFound = houseErrorsFound;
	}
	
	public boolean isRespondentErrorsFound() {
		return respondentErrorsFound;
	}

	public void setRespondentErrorsFound(boolean respondentErrorsFound) {
		this.respondentErrorsFound = respondentErrorsFound;
	}
		
	public boolean isHouseHeadValid() {
		return houseHeadValid;
	}

	public void setHouseHeadValid(boolean houseHeadValid) {
		this.houseHeadValid = houseHeadValid;
	}
	
	public boolean isHouseholdHeadValid() {
		return householdHeadValid;
	}

	public void setHouseholdHeadValid(boolean householdHeadValid) {
		this.householdHeadValid = householdHeadValid;
	}
	
	public boolean isRespondentValid() {
		return respondentValid;
	}

	public void setRespondentValid(boolean respondentValid) {
		this.respondentValid = respondentValid;
	}
	
	public IdSchemeResource getIdResource() {
		return idResource;
	}

	public void setIdResource(IdSchemeResource idResource) {
		this.idResource = idResource;
	}

	// this class is necessary in order to access the dob property using the
	// accessors/mutators. if the date is accessed via indexing, el resolver
	// exceptions would occur
	public class WrappedDate {

		Date date;

		WrappedDate() {
			date = new Date();
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public Date getDate() {
			return date;
		}
	}
}
