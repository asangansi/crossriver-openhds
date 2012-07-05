package org.openhds.webservice;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.controller.idgeneration.IdValidator;
import org.openhds.controller.service.CurrentUser;
import org.openhds.controller.service.DeathService;
import org.openhds.controller.service.EntityService;
import org.openhds.controller.service.FieldWorkerService;
import org.openhds.controller.service.InMigrationService;
import org.openhds.controller.service.IndividualService;
import org.openhds.controller.service.LocationHierarchyService;
import org.openhds.controller.service.MembershipService;
import org.openhds.controller.service.OutMigrationService;
import org.openhds.controller.service.PregnancyService;
import org.openhds.controller.service.RelationshipService;
import org.openhds.controller.service.SocialGroupService;
import org.openhds.controller.service.VisitService;
import org.openhds.controller.service.WhitelistService;
import org.openhds.controller.util.OpenHDSResult;
import org.openhds.dao.service.GenericDao;
import org.openhds.dao.service.GenericDao.ValueProperty;
import org.openhds.domain.model.AuditableCollectedEntity;
import org.openhds.domain.model.Death;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.InMigration;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.Location;
import org.openhds.domain.model.LocationHierarchy;
import org.openhds.domain.model.Membership;
import org.openhds.domain.model.Note;
import org.openhds.domain.model.OutMigration;
import org.openhds.domain.model.PregnancyObservation;
import org.openhds.domain.model.PregnancyOutcome;
import org.openhds.domain.model.PrivilegeConstants;
import org.openhds.domain.model.ReferencedBaseEntity;
import org.openhds.domain.model.ReferencedEntity;
import org.openhds.domain.model.Relationship;
import org.openhds.domain.model.Residency;
import org.openhds.domain.model.Round;
import org.openhds.domain.model.SocialGroup;
import org.openhds.domain.model.Visit;
import org.openhds.domain.service.SitePropertiesService;
import org.openhds.domain.util.CalendarUtil;
import org.openhds.webservice.dto.IndividualDTO;
import org.openhds.webservice.dto.LocationDTO;
import org.openhds.webservice.dto.LocationHierarchyDTO;
import org.openhds.webservice.dto.RoundDTO;
import org.openhds.webservice.dto.VisitDTO;
import org.openhds.webservice.dto.wrapper.IndividualDTOWrapper;
import org.openhds.webservice.dto.wrapper.LocationDTOWrapper;
import org.openhds.webservice.dto.wrapper.LocationHierarchyDTOWrapper;
import org.openhds.webservice.dto.wrapper.RoundDTOWrapper;
import org.openhds.webservice.dto.wrapper.VisitDTOWrapper;

@Produces("application/xml")
public class CoreWebServiceImpl {

	private static final String INVALID_FATHER_ID = "Invalid Father Id";
	private static final String INVALID_MOTHER_ID = "Invalid Mother Id";
	private static final String INVALID_SOCIAL_GROUP = "No Social Group Record Found";
	private static final String INVALID_LOCATION_ID = "Invalid Location Id";
	private static final String INVALID_LOCATION_HIERARCHY_ID = "Invalid Location Hierarchy Id";
	private static final String INVALID_VISIT_ID = "Invalid Visit Id";
	private static final String INVALID_FIELD_WORKER_ID = "Invalid Field Worker Id";
	private static final String INDIVIDUAL_ID_NOT_FOUND = "Invalid Individual Id";

	private VisitService visitService;
	private RelationshipService relationshipService;
	private SocialGroupService socialGroupService;
	private LocationHierarchyService locationService;
	private MembershipService membershipService;
	private PregnancyService pregnancyService;
	private DeathService deathService;
	private InMigrationService inMigrationService;
	private IndividualService individualService;
	private OutMigrationService outmigrationService;
	private FieldWorkerService fieldWorkerService;
	private EntityService entityService;
	private GenericDao genericDao;
	private WhitelistService whitelistService;
	private IdValidator idUtilities;
	private SitePropertiesService siteProperties;
	private CalendarUtil calendarUtil;
	private CurrentUser currentUser;

	@Context
	HttpServletRequest request;

	static Logger log = Logger.getLogger(CoreWebServiceImpl.class);

	@POST
	@Path("/visit")
	public Response createVisit(Visit visit) {
		return new VisitInsert().insertForResponse(visit);
	}

	private class VisitInsert extends InsertTemplate<Visit> {
		@Override
		protected void buildReferentialFields(Visit entity, FieldBuilder builder) {
			// visits are inserted in only 2 ways: referencing a previous location or registering a new location
			// a visit that is referencing a previous location will not have a location name, only location ext id
			boolean lookupLocation = entity.getVisitLocation() != null && StringUtils.isEmpty(entity.getVisitLocation().getLocationName());
			builder.referenceField(entity.getCollectedBy());
			if (lookupLocation) {
				builder.referenceField(entity.getVisitLocation());
			}
		}

		@Override
		protected void setReferentialFields(Visit entity, FieldBuilder builder) {
			entity.setCollectedBy(builder.fw);
			// its possible a visit is registering a new house in which case the builder reference will be null
			if (builder.loc != null) {
				entity.setVisitLocation(builder.loc);
			}
		}

		@Override
		protected void saveEntity(Visit entity) throws ConstraintViolations, Exception {
			if (entity.getVisitLocation().getUuid() == null) {
				// visits can create new locations. If the location was not fetched, it means it needs to be created
				new LocationInsert().insert(entity.getVisitLocation());
			}
			
			if (entity.getExtId() == null || entity.getExtId().isEmpty()) {
				entity = visitService.generateId(entity);
			}
			
			visitService.evaluateVisit(entity);
			entity.setStatus(siteProperties.getDataStatusValidCode());
			entityService.create(entity);
		}
	}

	@GET
	@Path("/visit/{locationId}/{visitDate}")
	public Response isVisitCreated(@PathParam("locationId") String locationId, @PathParam("visitDate") String visitDate) {
		if (!authenticateOrigin()) {
			return Response.status(Status.UNAUTHORIZED).build();
		}

		currentUser.setProxyUser("webservice", "test", new String[]{PrivilegeConstants.VIEW_ENTITY});
		
		if (locationId == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		Location house = genericDao.findByProperty(Location.class, "extId", locationId);
		Calendar vDate = null;
		try {
			vDate = calendarUtil.stringToCalendar(visitDate);
		} catch (ParseException e) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		ValueProperty vp1 = GenericDao.ValuePropertyBuilder.build("visitLocation", house);
		ValueProperty vp2 = GenericDao.ValuePropertyBuilder.build("visitDate", vDate);
		ValueProperty vp3 = GenericDao.ValuePropertyBuilder.build("deleted", false);

		List<Visit> visits = genericDao.findListByMultiProperty(Visit.class, vp1, vp2, vp3);
		if (visits == null || visits.isEmpty()) {
			return Response.status(Status.NOT_FOUND).build();
		} else if (visits.size() > 1) {
			log.warn("Two visits found when one expected for location: " + locationId + " on date: " + visitDate);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

		VisitDTO dto = new VisitDTO(visits.get(0));
		return Response.ok(dto, MediaType.APPLICATION_XML).build();
	}

	@POST
	@Path("/relationship")
	public Response createRelationship(Relationship relationship) {

		String indivAId = relationship.getIndividualA().getExtId();
		String indivBId = relationship.getIndividualB().getExtId();
		String fieldWorkerId = relationship.getCollectedBy().getExtId();

		HashMap<String, List<String>> idTemplates = new HashMap<String, List<String>>();
		idTemplates.put("Individual", Arrays.asList(indivAId, indivBId));
		idTemplates.put("FieldWorker", Arrays.asList(fieldWorkerId));

		OpenHDSResult result = idUtilities.evaluateCheckDigits(idTemplates);
		if (!result.isSuccess())
			return Response.status(400).build();

		if (authenticateOrigin()) {

			try {
				relationship.setIndividualA(individualService.findIndivById(indivAId, "No Individual A Record Found"));
				relationship.setIndividualB(individualService.findIndivById(indivBId, "No Individual B Record Found"));
				relationship.setCollectedBy(fieldWorkerService.findFieldWorkerById(fieldWorkerId,
						INVALID_FIELD_WORKER_ID));

				relationshipService.evaluateRelationship(relationship);
				relationship.setStatus(siteProperties.getDataStatusValidCode());

				entityService.create(relationship);
			} catch (Exception e) {
				return Response.status(400).build();
			}

			log.info("created relationship via web service call with indivAId=" + indivAId + ", indivBId=" + indivBId
					+ ", relType=" + relationship.getaIsToB() + ", startDate=" + relationship.getStartDate()
					+ ", relEndType=" + relationship.getEndType() + ", endDate=" + relationship.getEndDate()
					+ ", collectedBy=" + fieldWorkerId);
			return Response.ok().build();
		}
		return Response.status(401).build();
	}

	@POST
	@Path("/socialgroup")
	public Response createSocialGroup(SocialGroup socialGroup) {
		return new SocialGroupInsert().insertForResponse(socialGroup);
	}
	
	private class SocialGroupInsert extends InsertTemplate<SocialGroup> {

		@Override
		protected void buildReferentialFields(SocialGroup entity, FieldBuilder builder) {
			builder.referenceField(entity.getCollectedBy());
		}

		@Override
		protected void setReferentialFields(SocialGroup entity, FieldBuilder builder) {
			entity.setCollectedBy(builder.fw);
		}

		@Override
		protected void saveEntity(SocialGroup entity) throws ConstraintViolations, Exception {
			ConstraintViolations violations = new ConstraintViolations();
			if (entity.getGroupHead() == null) {
				violations.addViolations("Social group must have a head");
			}
			
			if (entity.getRespondent() == null) {
				violations.addViolations("Social group must have a respondent");
			}
			
			if (violations.hasViolations()) {
				throw violations;
			}
			
			boolean createdHead = false;
			Individual head = individualService.findIndivById(entity.getGroupHead().getExtId());
			if (head == null) {
				head = individualService.createTemporaryIndividualWithExtId(entity.getGroupHead().getExtId(), entity.getCollectedBy());
				createdHead = true;
			}
			entity.setGroupHead(head);
			
			boolean createdRespondent = false;
			Individual respondent = individualService.findIndivById(entity.getRespondent().getExtId());
			if (respondent == null) {
				respondent = individualService.createTemporaryIndividualWithExtId(entity.getRespondent().getExtId(), entity.getCollectedBy());
				createdRespondent = true;
			}
			entity.setRespondent(respondent);
			
			try {
				socialGroupService.evaluateSocialGroup(entity, true);
				entityService.create(entity);
			} catch(Exception e) {
				// take care to clean up any temporary individuals that might have been created
				if (createdHead) {
					genericDao.delete(entity.getGroupHead());
				}
				
				if (createdRespondent) {
					genericDao.delete(entity.getRespondent());
				}
				
				throw e;
			}
		}
	}

	@GET
	@Path("/socialgroup/{sg}")
	public Response socialGroupExists(@PathParam("sg") String extId) {
		if (!authenticateOrigin()) {
			return Response.status(Status.UNAUTHORIZED).build();
		}

		currentUser.setProxyUser("webservice", "test", new String[]{PrivilegeConstants.VIEW_ENTITY});
		
		if (extId == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		SocialGroup sg = socialGroupService.findSocialGroupById(extId.toUpperCase());
		if (sg == null) {
			return Response.status(Status.NOT_FOUND).build();
		}

		return Response.status(Status.OK).build();
	}

	@POST
	@Path("/location")
	public Response createLocation(Location location) {
		return new LocationInsert().insertForResponse(location);
	}

	private class LocationInsert extends InsertTemplate<Location> {

		@Override
		protected void buildReferentialFields(Location entity, FieldBuilder builder) {
			builder.referenceField(entity.getCollectedBy());
			builder.referenceField(entity.getLocationLevel());
		}

		@Override
		protected void setReferentialFields(Location entity, FieldBuilder builder) {
			entity.setCollectedBy(builder.fw);
			entity.setLocationLevel(builder.locationLevel);
		}

		@Override
		protected void saveEntity(Location entity) throws ConstraintViolations, Exception {
			if (entity.getLocationHead() == null || StringUtils.isEmpty(entity.getLocationHead().getExtId())) {
				throw new ConstraintViolations("A location must provide an individual as head of location");
			}

			// evaluating the location BEFORE setting the head of location because its possible the location could fail
			// validation, in which case a temporary individual would be created and left dangling
			locationService.evaluateLocation(entity, true);

			// CR requires a location to reference an individual who is the location head. It's possible the individual
			// has yet to be created. In this case a temporary individual is created if the location does not point to a
			// valid individual
			Individual headOfLocation = individualService.findIndivById(entity.getLocationHead().getExtId());
			if (headOfLocation == null) {
				headOfLocation = individualService.createTemporaryIndividualWithExtId(entity.getLocationHead()
						.getExtId(), entity.getCollectedBy());
			}

			entity.setLocationHead(headOfLocation);
			entityService.create(entity);
		}
	}

	@GET
	@Path("/location/{location}")
	public Response locationExists(@PathParam("location") String locationExtId) {
		if (!authenticateOrigin()) {
			return Response.status(Status.UNAUTHORIZED).build();
		}

		currentUser.setProxyUser("webservice", "test", new String[]{PrivilegeConstants.VIEW_ENTITY});
		
		if (locationExtId == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		Location location = locationService.findLocationById(locationExtId.toUpperCase());
		if (location == null) {
			return Response.status(Status.NOT_FOUND).build();
		}

		return Response.status(Status.OK).build();
	}

	@POST
	@Path("/membership")
	public Response createMembership(Membership membership) {

		String fieldWorkerId = membership.getCollectedBy().getExtId();
		String individualId = membership.getIndividual().getExtId();
		String socialGroupId = membership.getSocialGroup().getExtId();

		HashMap<String, List<String>> idTemplates = new HashMap<String, List<String>>();
		idTemplates.put("FieldWorker", Arrays.asList(fieldWorkerId));
		idTemplates.put("Individual", Arrays.asList(individualId));
		idTemplates.put("SocialGroup", Arrays.asList(socialGroupId));

		OpenHDSResult result = idUtilities.evaluateCheckDigits(idTemplates);
		if (!result.isSuccess())
			return Response.status(400).build();

		if (authenticateOrigin()) {

			try {
				membership.setIndividual(individualService.findIndivById(individualId, INDIVIDUAL_ID_NOT_FOUND));
				membership.setSocialGroup(socialGroupService.findSocialGroupById(socialGroupId, INVALID_SOCIAL_GROUP));
				membership.setCollectedBy(fieldWorkerService
						.findFieldWorkerById(fieldWorkerId, INVALID_FIELD_WORKER_ID));

				membershipService.evaluateMembership(membership);
				membership.setStatus(siteProperties.getDataStatusValidCode());

				entityService.create(membership);
			} catch (Exception e) {
				return Response.status(400).build();
			}

			log.info("created membership via web service call with indivId=" + individualId + ", socialGroupId="
					+ socialGroupId + ", startDate=" + membership.getStartDate() + ", startType="
					+ membership.getStartType() + ", endDate=" + membership.getEndDate() + ", endType="
					+ membership.getEndType() + ", bIsToA=" + membership.getbIsToA() + ", collectedBy=" + fieldWorkerId);
			return Response.ok().build();
		}
		return Response.status(401).build();
	}

	/**
	 * Builder class to aggregate the the operations involved in retrieving previously saved entities
	 */
	private class FieldBuilder {
		ConstraintViolations violations = new ConstraintViolations();
		Visit visit;
		FieldWorker fw;
		Location loc;
		SocialGroup sg;
		List<Individual> individuals = new ArrayList<Individual>();
		LocationHierarchy locationLevel;

		public FieldBuilder referenceField(Visit visit) {
			if (visit == null) {
				violations.addViolations("No visit id provided");
			} else {
				try {
					this.visit = visitService.findVisitById(visit.getExtId(), INVALID_VISIT_ID);
				} catch (Exception e) {
					violations.addViolations(INVALID_VISIT_ID);
				}
			}

			return this;
		}

		public void referenceField(LocationHierarchy locationLevel) {
			if (locationLevel == null) {
				violations.addViolations("Location must provide a location level");
			} else {
				try {
					this.locationLevel = locationService.findLocationHierarchyById(locationLevel.getExtId()
							.toUpperCase(), "Invalid location level id");
				} catch (Exception e) {
					violations.addViolations(e.getMessage());
				}
			}
		}

		public FieldBuilder requiredField(String requiredField, String violationMsg) {
			if (requiredField == null) {
				violations.addViolations(violationMsg);
			}
			return this;
		}

		public void validate() throws ConstraintViolations {
			if (hasViolations()) {
				throw violations;
			}
		}

		public FieldBuilder referenceField(Individual individual, String msg) {
			if (individual == null) {
				violations.addViolations(msg);
			} else {
				try {
					individuals.add(individualService.findIndivById(individual.getExtId(), msg));
				} catch (Exception e) {
					violations.addViolations(msg);
				}
			}

			return this;
		}

		public FieldBuilder referenceField(Location house) {
			if (house == null) {
				violations.addViolations("No house id provided");
			} else {
				try {
					loc = locationService.findLocationById(house.getExtId(), INVALID_LOCATION_ID);
				} catch (Exception e) {
					violations.addViolations(INVALID_LOCATION_ID);
				}
			}

			return this;
		}

		public FieldBuilder referenceField(SocialGroup household) {
			if (household == null) {
				violations.addViolations("No household id provided");
			} else {
				try {
					sg = socialGroupService.findSocialGroupById(household.getExtId(), INVALID_SOCIAL_GROUP);
				} catch (Exception e) {
					violations.addViolations(INVALID_SOCIAL_GROUP);
				}
			}

			return this;
		}

		public FieldBuilder referenceField(FieldWorker collectedBy) {
			if (collectedBy == null) {
				violations.addViolations("No field worker id provided");
			} else {
				try {
					fw = fieldWorkerService.findFieldWorkerById(collectedBy.getExtId(), INVALID_FIELD_WORKER_ID);
				} catch (Exception e) {
					violations.addViolations(INVALID_FIELD_WORKER_ID);
				}
			}

			return this;
		}

		public boolean hasViolations() {
			return violations.hasViolations();
		}
	}

	/**
	 * Template that provides a generic algorithm for inserting an entity into the system
	 * 
	 * @param <T>
	 *            The type of entity to be inserted
	 */
	private abstract class InsertTemplate<T extends AuditableCollectedEntity> {

		public Response insertForResponse(T entity) {
			if (!authenticateOrigin()) {
				return Response.status(401).build();
			}
			
			try {
				insert(entity);
			} catch (ConstraintViolations e) {
				WebServiceCallException ex = new WebServiceCallException(e);
				return Response.status(Status.OK).entity(ex).build();
			} catch (Exception e) {
				ConstraintViolations v = new ConstraintViolations(e.getMessage());
				WebServiceCallException ex = new WebServiceCallException(v);
				return Response.status(Status.OK).entity(ex).build();
			}

			return Response.ok(entity.getUuid(), MediaType.TEXT_PLAIN).build();
		}

		public void insert(T entity) throws ConstraintViolations, Exception {
			FieldBuilder builder = new FieldBuilder();
			buildReferentialFields(entity, builder);
			builder.validate();

			setReferentialFields(entity, builder);
			saveEntity(entity);
		}

		protected abstract void buildReferentialFields(T entity, FieldBuilder builder);

		protected abstract void setReferentialFields(T entity, FieldBuilder builder);

		protected abstract void saveEntity(T entity) throws ConstraintViolations, Exception;

		private boolean authenticateOrigin() {
			return whitelistService.evaluateAddress(request);
		}
	}

	@POST
	@Path("/death")
	public Response createDeath(Death death) {
		return new DeathInsert().insertForResponse(death);
	}

	private class DeathInsert extends InsertTemplate<Death> {

		@Override
		protected void buildReferentialFields(Death entity, FieldBuilder builder) {
			builder.referenceField(entity.getIndividual(), INDIVIDUAL_ID_NOT_FOUND)
					.referenceField(entity.getVisitDeath()).referenceField(entity.getCollectedBy())
					.referenceField(entity.getHouse()).referenceField(entity.getHousehold());
		}

		@Override
		protected void setReferentialFields(Death entity, FieldBuilder builder) {
			entity.setIndividual(builder.individuals.get(0));
			entity.setCollectedBy(builder.fw);
			entity.setVisitDeath(builder.visit);
			entity.setHouse(builder.loc);
			entity.setHousehold(builder.sg);
		}

		@Override
		protected void saveEntity(Death entity) throws ConstraintViolations, Exception {
			deathService.evaluateDeath(entity);
			deathService.createDeath(entity);
		}
	}

	@POST
	@Path("/inmigration")
	public Response createInMigration(InMigration inmigration) {
		return new InMigrationInsert().insertForResponse(inmigration);
	}

	private class InMigrationInsert extends InsertTemplate<InMigration> {

		@Override
		protected void buildReferentialFields(InMigration entity, FieldBuilder builder) {
			builder.referenceField(entity.getCollectedBy()).referenceField(entity.getVisit())
					.referenceField(entity.getHousehold()).referenceField(entity.getHouse())
					.referenceField(entity.getIndividual().getFather(), "Father permanent id not found")
					.referenceField(entity.getIndividual().getMother(), "Mother permand id is not found")
					.requiredField(entity.getEverRegistered().toString(), "Ever Registered field is required")
					.requiredField(entity.getIndividual().getExtId(), "A permanent id is required");
		}

		@Override
		protected void setReferentialFields(InMigration entity, FieldBuilder builder) {
			entity.setCollectedBy(builder.fw);
			entity.setHouse(builder.loc);
			entity.setHousehold(builder.sg);
			entity.setVisit(builder.visit);
			entity.getIndividual().setFather(builder.individuals.get(0));
			entity.getIndividual().setMother(builder.individuals.get(1));
		}

		@Override
		protected void saveEntity(InMigration entity) throws ConstraintViolations, Exception {
			inMigrationService.evaluateInMigration(entity);
			inMigrationService.createInMigration(entity);
		}
	}

	@POST
	@Path("/pregnancyobservation")
	public Response createPregnancyObservation(PregnancyObservation pregObserv) {
		if (pregObserv.getExpectedDeliveryDate() == null) {
			Calendar expected = Calendar.getInstance();
			expected.setTime(pregObserv.getEstimatedDateOfConception().getTime());
			expected.add(Calendar.MONTH, 9);
			pregObserv.setExpectedDeliveryDate(expected);
		}
		return new PregnancyObservationInsert().insertForResponse(pregObserv);
	}

	private class PregnancyObservationInsert extends InsertTemplate<PregnancyObservation> {

		@Override
		protected void saveEntity(PregnancyObservation entity) throws ConstraintViolations, Exception {
			pregnancyService.evaluatePregnancyObservation(entity);
			entityService.create(entity);
		}

		@Override
		protected void buildReferentialFields(PregnancyObservation entity, FieldBuilder builder) {
			builder.referenceField(entity.getCollectedBy())
					.referenceField(entity.getMother(), "Mother permanent id not found")
					.referenceField(entity.getHouse()).referenceField(entity.getHousehold())
					.referenceField(entity.getVisit());
		}

		@Override
		protected void setReferentialFields(PregnancyObservation entity, FieldBuilder builder) {
			entity.setCollectedBy(builder.fw);
			entity.setHouse(builder.loc);
			entity.setHousehold(builder.sg);
			entity.setVisit(builder.visit);
			entity.setMother(builder.individuals.get(0));
		}
	}

	@POST
	@Path("/outmigration")
	public Response createOutMigration(OutMigration outmigration) {
		return new OutMigrationInsert().insertForResponse(outmigration);
	}

	private class OutMigrationInsert extends InsertTemplate<OutMigration> {

		@Override
		protected void buildReferentialFields(OutMigration entity, FieldBuilder builder) {
			builder.referenceField(entity.getCollectedBy())
					.referenceField(entity.getIndividual(), INDIVIDUAL_ID_NOT_FOUND).referenceField(entity.getHouse())
					.referenceField(entity.getVisit()).referenceField(entity.getHousehold());
		}

		@Override
		protected void setReferentialFields(OutMigration entity, FieldBuilder builder) {
			entity.setIndividual(builder.individuals.get(0));
			entity.setCollectedBy(builder.fw);
			entity.setVisit(builder.visit);
			entity.setHouse(builder.loc);
			entity.setHousehold(builder.sg);
		}

		@Override
		protected void saveEntity(OutMigration entity) throws ConstraintViolations, Exception {
			outmigrationService.evaluateOutMigration(entity);
			outmigrationService.createOutMigration(entity);
		}
	}

	@POST
	@Path("/pregnancyoutcome")
	public Response createPregnancyOutcome(PregnancyOutcome pregnancyOutcome) {
		if (!isSecondChildPresent(pregnancyOutcome)) {
			pregnancyOutcome.setChild2(null);
		}
		return new PregnancyOutcomeInsert().insertForResponse(pregnancyOutcome);
	}

	private boolean isSecondChildPresent(PregnancyOutcome pregnancyOutcome) {
		if (pregnancyOutcome.getChild2() == null) {
			return false;
		}

		if (pregnancyOutcome.getChild2().getExtId() == null || pregnancyOutcome.getChild2().getExtId().isEmpty()) {
			return false;
		}

		return true;
	}

	private class PregnancyOutcomeInsert extends InsertTemplate<PregnancyOutcome> {

		@Override
		protected void buildReferentialFields(PregnancyOutcome entity, FieldBuilder builder) {
			builder.referenceField(entity.getMother(), "Mother permanent Id is not valid")
					.referenceField(entity.getFather(), "Father permanent Id is not valid")
					.referenceField(entity.getVisit()).referenceField(entity.getCollectedBy())
					.referenceField(entity.getHouse()).referenceField(entity.getHousehold());
		}

		@Override
		protected void setReferentialFields(PregnancyOutcome entity, FieldBuilder builder) {
			entity.setMother(builder.individuals.get(0));
			entity.setFather(builder.individuals.get(1));
			entity.setHouse(builder.loc);
			entity.setHousehold(builder.sg);
			entity.setVisit(builder.visit);
			entity.setCollectedBy(builder.fw);
		}

		@Override
		protected void saveEntity(PregnancyOutcome entity) throws ConstraintViolations, Exception {
			pregnancyService.evaluatePregnancyOutcome(entity);
			pregnancyService.createPregnancyOutcome(entity);
		}
	}

	@POST
	@Path("/individual")
	public Response createIndividual(Individual individual) {

		String fieldWorkerId = individual.getCollectedBy().getExtId();
		String motherId = individual.getMother().getExtId();
		String fatherId = individual.getFather().getExtId();

		HashMap<String, List<String>> idTemplates = new HashMap<String, List<String>>();
		idTemplates.put("FieldWorker", Arrays.asList(fieldWorkerId));
		idTemplates.put("Individual", Arrays.asList(motherId, fatherId));

		OpenHDSResult result = idUtilities.evaluateCheckDigits(idTemplates);
		if (!result.isSuccess())
			return Response.status(400).build();

		if (authenticateOrigin()) {

			try {
				individual.setMother(individualService.findIndivById(motherId, INVALID_MOTHER_ID));
				individual.setFather(individualService.findIndivById(fatherId, INVALID_FATHER_ID));
				individual.setCollectedBy(fieldWorkerService
						.findFieldWorkerById(fieldWorkerId, INVALID_FIELD_WORKER_ID));

				individualService.evaluateIndividual(individual);
				individual.setStatus(siteProperties.getDataStatusValidCode());

				entityService.create(individual);

			} catch (Exception e) {
				return Response.status(400).build();
			}
			log.info("created individual via web service call with firstName=" + individual.getFirstName()
					+ ", middleName=" + individual.getMiddleName() + ", lastName=" + individual.getLastName()
					+ ", gender=" + individual.getGender() + ", dob=" + individual.getDob() + ", motherId=" + motherId
					+ ", fatherId=" + fatherId + ", collectedBy=" + fieldWorkerId);
			return Response.ok().build();
		}
		return Response.status(401).build();
	}

	@POST
	@Path("/note")
	public Response createNote(Note note) {

		String fieldWorkerId = note.getCollectedBy().getExtId();

		if (authenticateOrigin()) {

			try {
				note.setCollectedBy(fieldWorkerService.findFieldWorkerById(fieldWorkerId, INVALID_FIELD_WORKER_ID));
				note.setStatus(siteProperties.getDataStatusPendingCode());
				entityService.create(note);
			} catch (Exception e) {
				return Response.status(400).build();
			}
			return Response.ok().build();
		}
		return Response.status(401).build();
	}
	
    @GET
    @Path("/individual")
    public IndividualDTOWrapper getAllIndividuals() {  
    	IndividualDTOWrapper wrapper = new IndividualDTOWrapper();
        List<Individual> indivs = genericDao.findAll(Individual.class, true);
        
        int count = 0;
        for (Individual indiv : indivs) {
        	if (IndividualDTO.isValid(indiv)) {
        		IndividualDTO dto = new IndividualDTO(indiv);
        		wrapper.getIndividual().add(dto);
        		count++;
        	}
        }     
        wrapper.setCount(count);
        return wrapper;
    }
    
    @GET
    @Path("/location")
    public LocationDTOWrapper getAllLocations() {  
    	LocationDTOWrapper wrapper = new LocationDTOWrapper();
        List<Location> locs = genericDao.findAll(Location.class, true);
        
        int count = 0;
        for (Location loc : locs) {
        	if (LocationDTO.isValid(loc)) {
        		LocationDTO dto = new LocationDTO(loc);
        		wrapper.getLocation().add(dto);
        		count++;
        	}
        }     
        wrapper.setCount(count);
        return wrapper;
    }
    
    @GET
    @Path("/locationhierarchy")
    public LocationHierarchyDTOWrapper getAllLocationHierarchy() {  
    	LocationHierarchyDTOWrapper wrapper = new LocationHierarchyDTOWrapper();
        List<LocationHierarchy> hierarchy = genericDao.findAll(LocationHierarchy.class, false);
        
        int count = 0;
        for (LocationHierarchy item : hierarchy) {
        	if (!item.getExtId().equals("HIERARCHY_ROOT")) {
        		LocationHierarchyDTO dto = new LocationHierarchyDTO(item);
        		wrapper.getHierarchy().add(dto);
        		count++;
        	}
        }     
        wrapper.setCount(count);
        return wrapper;
    }
    
    @GET
    @Path("/round")
    public RoundDTOWrapper getAllRounds() {  
    	RoundDTOWrapper wrapper = new RoundDTOWrapper();
        List<Round> rounds = genericDao.findAll(Round.class, false);
        
        int count = 0;
        for (Round item : rounds) {
        	RoundDTO dto = new RoundDTO(item);
        	wrapper.getRound().add(dto);
        	count++;
        }     
        wrapper.setCount(count);
        return wrapper;
    } 
    
    @GET
    @Path("/visit")
    public VisitDTOWrapper getAllVisits() {  
    	VisitDTOWrapper wrapper = new VisitDTOWrapper();
        List<Visit> visits = genericDao.findAll(Visit.class, true);
        
        int count = 0;
        for (Visit item : visits) {
        	VisitDTO dto = new VisitDTO(item);
        	wrapper.getVisit().add(dto);
        	count++;
        }     
        wrapper.setCount(count);
        return wrapper;
    } 

	@GET
	@Path("/hierarchy")
	public ReferencedEntity getHierarchyIds() {

		ReferencedEntity refEntity = new ReferencedEntity();
		List<LocationHierarchy> hierarchyList = genericDao.findAll(LocationHierarchy.class, false);

		int count = 0;
		for (LocationHierarchy item : hierarchyList) {
			if (!item.getExtId().equals("HIERARCHY_ROOT")) {
				Map<String, String> params = new HashMap<String, String>();
				params.put("name", item.getName());

				ReferencedBaseEntity entity = new ReferencedBaseEntity();
				entity.setExtId(item.getExtId());
				entity.setType("hierarchy");
				entity.setParams(params);
				refEntity.getEntity().add(entity);
				count++;
			}
		}
		refEntity.setCount(count);
		return refEntity;
	}

	@GET
	@Path("entityIds/{locationHierarchy}")
	public ReferencedEntity getIdsByLocationHierarchyLevel(@PathParam("locationHierarchy") String locationHierarchy) {

		ReferencedEntity refEntity = new ReferencedEntity();
		List<LocationHierarchy> hierarchyList = genericDao.findListByProperty(LocationHierarchy.class, "extId",
				locationHierarchy);

		for (LocationHierarchy item : hierarchyList)
			refEntity = processLocationHierarchy(item, refEntity);

		return refEntity;
	}

	/**
	 * Recursive function to perform a search on the location hierarchy when retrieving entity ids by location hierarchy
	 * level.
	 * 
	 * @param item
	 *            - the current item in the location hierarchy tree
	 */
	public ReferencedEntity processLocationHierarchy(LocationHierarchy item, ReferencedEntity output) {

		// base case
		if (item.getLevel().equals(locationService.getLowestLevel())) {

			// obtain ids
			// -- location --
			List<Location> locationList = genericDao.findListByProperty(Location.class, "locationLevel", item, true);
			for (Location loc : locationList) {
				ReferencedBaseEntity entity = new ReferencedBaseEntity();
				Map<String, String> params = new HashMap<String, String>();
				params.put("name", loc.getLocationName());

				entity.setExtId(loc.getExtId());
				entity.setType("location");
				entity.setParams(params);
				output.getEntity().add(entity);
				output.increaseCount();
			}

			// -- individual --
			List<Individual> individualList = genericDao.findAll(Individual.class, true);
			for (Individual indiv : individualList) {

				Residency residency = indiv.getCurrentResidency();
				if (residency != null) {
					if (residency.getLocation().getLocationLevel().getExtId().equals(item.getExtId())) {
						ReferencedBaseEntity entity = new ReferencedBaseEntity();
						Map<String, String> params = new HashMap<String, String>();
						params.put("firstname", indiv.getFirstName());
						params.put("lastname", indiv.getLastName());

						if (indiv.getGender().equals(siteProperties.getMaleCode()))
							params.put("gender", "M");
						else if (indiv.getGender().equals(siteProperties.getFemaleCode()))
							params.put("gender", "F");

						entity.setExtId(indiv.getExtId());
						entity.setType("individual");
						entity.setParams(params);
						output.getEntity().add(entity);
						output.increaseCount();
					}
				}
			}

			// -- social group --
			List<SocialGroup> socialgroupList = genericDao.findAll(SocialGroup.class, true);
			for (SocialGroup sg : socialgroupList) {

				Residency residency = sg.getGroupHead().getCurrentResidency();
				if (residency != null) {
					if (residency.getLocation().getLocationLevel().getExtId().equals(item.getExtId())) {
						ReferencedBaseEntity entity = new ReferencedBaseEntity();
						Map<String, String> params = new HashMap<String, String>();
						params.put("groupname", sg.getGroupName());

						entity.setExtId(sg.getExtId());
						entity.setType("socialgroup");
						entity.setParams(params);
						output.getEntity().add(entity);
						output.increaseCount();
					}
				}
			}

			// -- visit --
			List<Visit> visitList = genericDao.findAll(Visit.class, true);
			for (Visit visit : visitList) {
				if (visit.getVisitLocation().getLocationLevel().equals(item)) {
					ReferencedBaseEntity entity = new ReferencedBaseEntity();
					Map<String, String> params = new HashMap<String, String>();
					params.put("round", visit.getRoundNumber().toString());

					entity.setExtId(visit.getExtId());
					entity.setType("visit");
					entity.setParams(params);
					output.getEntity().add(entity);
					output.increaseCount();
				}
			}

			return output;
		}

		// find all location hierarchy items that are children, continue to
		// recurse
		List<LocationHierarchy> hierarchyList = genericDao.findListByProperty(LocationHierarchy.class, "parent", item);

		for (LocationHierarchy locationHierarchy : hierarchyList)
			processLocationHierarchy(locationHierarchy, output);

		return output;
	}

	private boolean authenticateOrigin() {
		return whitelistService.evaluateAddress(request);
	}

	public void setVisitService(VisitService visitService) {
		this.visitService = visitService;
	}

	public void setRelationshipService(RelationshipService relationshipService) {
		this.relationshipService = relationshipService;
	}

	public void setPregnancyService(PregnancyService pregObservService) {
		this.pregnancyService = pregObservService;
	}

	public void setSocialGroupService(SocialGroupService socialGroupService) {
		this.socialGroupService = socialGroupService;
	}

	public void setLocationService(LocationHierarchyService locationService) {
		this.locationService = locationService;
	}

	public void setDeathService(DeathService deathService) {
		this.deathService = deathService;
	}

	public void setMembershipService(MembershipService membershipService) {
		this.membershipService = membershipService;
	}

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}

	public void setGenericDao(GenericDao genericDao) {
		this.genericDao = genericDao;
	}

	public void setInMigrationService(InMigrationService inMigrationService) {
		this.inMigrationService = inMigrationService;
	}

	public void setIndividualService(IndividualService individualService) {
		this.individualService = individualService;
	}

	public void setOutmigrationService(OutMigrationService outmigrationService) {
		this.outmigrationService = outmigrationService;
	}

	public void setWhitelistService(WhitelistService whitelistService) {
		this.whitelistService = whitelistService;
	}

	public void setFieldWorkerService(FieldWorkerService fieldWorkerService) {
		this.fieldWorkerService = fieldWorkerService;
	}

	public void setIdUtilities(IdValidator idUtilities) {
		this.idUtilities = idUtilities;
	}

	public void setSiteProperties(SitePropertiesService siteProperties) {
		this.siteProperties = siteProperties;
	}

	public void setCalendarUtil(CalendarUtil calendarUtil) {
		this.calendarUtil = calendarUtil;
	}

	public void setCurrentUser(CurrentUser currentUser) {
		this.currentUser = currentUser;
	}
}
