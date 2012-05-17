package org.openhds.extensions;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

/**
 * A class that's responsible for building the core Location entity table
 * using the CodeModel.
 * 
 * @author Brian
 *
 */
public class LocationTemplateBuilder implements ExtensionTemplate {
	
	JCodeModel jCodeModel;
	boolean templateBuilt = false;
	
	LocationTemplateBuilder(JCodeModel jCodeModel) {
		this.jCodeModel = jCodeModel;
	}
	
	public void buildTemplate(JDefinedClass jc) {
		
		JDocComment jDocComment = jc.javadoc();
		jDocComment.add("Generated by JCodeModel");
		
		jc._extends(org.openhds.domain.model.AuditableCollectedEntity.class);
		jc._implements(java.io.Serializable.class);
		
		buildClassAnnotations(jc);	
		buildFieldsAndMethods(jc);
		
		templateBuilt = true;
	}
	
	public void buildFieldsAndMethods(JDefinedClass jc) {
			
		// serial uuid
		JFieldVar jfSerial = jc.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, long.class, "serialVersionUID");
		jfSerial.init(JExpr.lit(169551578162260199L));
		
		// extId
		JFieldVar jfExtId = jc.field(JMod.PRIVATE , java.lang.String.class, "extId");
		jfExtId.annotate(javax.validation.constraints.NotNull.class);
		jfExtId.annotate(org.openhds.domain.constraint.CheckFieldNotBlank.class);
		jfExtId.annotate(org.openhds.domain.constraint.Searchable.class);
		JAnnotationUse jaExtIdDesc = jfExtId.annotate(org.openhds.domain.annotations.Description.class);
		jaExtIdDesc.param("description", "External Id of the location. This id is used internally.");
		
		// getter
		JMethod jmgExtId = jc.method(JMod.PUBLIC, java.lang.String.class, "getExtId");
		JBlock jmgExtIdBlock = jmgExtId.body();
		jmgExtIdBlock._return(jfExtId);
		
		// setter
		JMethod jmsExtId = jc.method(JMod.PUBLIC, void.class, "setExtId");
		JVar jvarExtId = jmsExtId.param(java.lang.String.class, "id");
		JBlock jmsExtIdBlock = jmsExtId.body();
		jmsExtIdBlock.assign(jfExtId, jvarExtId);
		
		// location name
		JFieldVar jfLocationName = jc.field(JMod.PRIVATE , java.lang.String.class, "locationName");
		jfLocationName.annotate(org.openhds.domain.constraint.CheckFieldNotBlank.class);
		jfLocationName.annotate(org.openhds.domain.constraint.Searchable.class);
		JAnnotationUse jaLocationNameDesc = jfLocationName.annotate(org.openhds.domain.annotations.Description.class);
		jaLocationNameDesc.param("description", "Name of the location.");
		
		// getter
		JMethod jmgLocationName = jc.method(JMod.PUBLIC, java.lang.String.class, "getLocationName");
		JBlock jmgLocationNameBlock = jmgLocationName.body();
		jmgLocationNameBlock._return(jfLocationName);
		
		// setter
		JMethod jmsLocationName = jc.method(JMod.PUBLIC, void.class, "setLocationName");
		JVar jvarLocationName = jmsLocationName.param(java.lang.String.class, "name");
		JBlock jmsLocationNameBlock = jmsLocationName.body();
		jmsLocationNameBlock.assign(jfLocationName, jvarLocationName);
		
		// location level
		JFieldVar jfLocationLevel = jc.field(JMod.PRIVATE , org.openhds.domain.model.LocationHierarchy.class, "locationLevel");
		JClass jClassRef = jCodeModel.ref(org.openhds.domain.model.LocationHierarchy.class);
		jfLocationLevel.init(JExpr._new(jClassRef));	
		jfLocationLevel.annotate(javax.persistence.ManyToOne.class);
		JAnnotationUse jaLocationLevel = jfLocationLevel.annotate(org.hibernate.annotations.Cascade.class);
		jaLocationLevel.param("value", org.hibernate.annotations.CascadeType.SAVE_UPDATE);
		
		// getter
		JMethod jmgLocationLevel = jc.method(JMod.PUBLIC, org.openhds.domain.model.LocationHierarchy.class, "getLocationLevel");
		JBlock jmgLocationLevelBlock = jmgLocationLevel.body();
		jmgLocationLevelBlock._return(jfLocationLevel);
		
		// setter
		JMethod jmsLocationLevel = jc.method(JMod.PUBLIC, void.class, "setLocationLevel");
		JVar jvarLocationLevel = jmsLocationLevel.param(org.openhds.domain.model.LocationHierarchy.class, "level");
		JBlock jmsLocationLevelBlock = jmsLocationLevel.body();
		jmsLocationLevelBlock.assign(jfLocationLevel, jvarLocationLevel);
		
		// location type
		JFieldVar jfLocationType = jc.field(JMod.PRIVATE , java.lang.String.class, "locationType");
		JAnnotationUse jaLocationType = jfLocationType.annotate(org.openhds.domain.constraint.ExtensionStringConstraint.class);
		jaLocationType.param("constraint", "locationTypeConstraint");
		jaLocationType.param("message", "Invalid Value for location type");
		jaLocationType.param("allowNull", true);
		JAnnotationUse jaLocationTypeDesc = jfLocationType.annotate(org.openhds.domain.annotations.Description.class);
		jaLocationTypeDesc.param("description", "The type of Location.");
		
		// getter
		JMethod jmgLocationType = jc.method(JMod.PUBLIC, java.lang.String.class, "getLocationType");
		JBlock jmgLocationTypeBlock = jmgLocationType.body();
		jmgLocationTypeBlock._return(jfLocationType);
		
		// setter
		JMethod jmsLocationType = jc.method(JMod.PUBLIC, void.class, "setLocationType");
		JVar jvarLocationType = jmsLocationType.param(java.lang.String.class, "type");
		JBlock jmsLocationTypeBlock = jmsLocationType.body();
		jmsLocationTypeBlock.assign(jfLocationType, jvarLocationType);
		
		// location head
		JFieldVar jfHead = jc.field(JMod.PRIVATE , org.openhds.domain.model.Individual.class, "locationHead");
		jfHead.annotate(javax.persistence.ManyToOne.class);
		jfHead.annotate(org.openhds.domain.constraint.CheckEntityNotVoided.class);
		jfHead.annotate(org.openhds.domain.constraint.CheckIndividualNotUnknown.class);
		JAnnotationUse jaFatherDesc = jfHead.annotate(org.openhds.domain.annotations.Description.class);
		jaFatherDesc.param("description", "The head of the location.");
		
		// getter
		JMethod jmgHead = jc.method(JMod.PUBLIC, org.openhds.domain.model.Individual.class, "getLocationHead");
		JBlock jmgHeadBlock = jmgHead.body();
		jmgHeadBlock._return(jfHead);
		
		// setter
		JMethod jmsHead = jc.method(JMod.PUBLIC, void.class, "setLocationHead");
		JVar jvarHead = jmsHead.param(org.openhds.domain.model.Individual.class, "head");
		JBlock jmsHeadBlock = jmsHead.body();
		jmsHeadBlock.assign(jfHead, jvarHead);
		
		// longitude
		JFieldVar jfLongitude = jc.field(JMod.PRIVATE, java.lang.String.class, "longitude");
		JAnnotationUse jaLongitudeDesc = jfLongitude.annotate(org.openhds.domain.annotations.Description.class);
		jaLongitudeDesc.param("description", "The longitude for the Location");
		
		// getter
		JMethod jmgLongitude = jc.method(JMod.PUBLIC, java.lang.String.class, "getLongitude");
		JBlock jmgLongitudeBlock = jmgLongitude.body();
		jmgLongitudeBlock._return(jfLongitude);
		
		// setter
		JMethod jmsLongitude = jc.method(JMod.PUBLIC, void.class, "setLongitude");
		JVar jvarLongitude = jmsLongitude.param(java.lang.String.class, "longi");
		JBlock jmsLongitudeBlock = jmsLongitude.body();
		jmsLongitudeBlock.assign(jfLongitude, jvarLongitude);
		
		// latitude
		JFieldVar jfLatitude = jc.field(JMod.PRIVATE, java.lang.String.class, "latitude");
		JAnnotationUse jaLatitudeDesc = jfLatitude.annotate(org.openhds.domain.annotations.Description.class);
		jaLatitudeDesc.param("description", "The latitude for the Location");
		
		// getter
		JMethod jmgLatitude = jc.method(JMod.PUBLIC, java.lang.String.class, "getLatitude");
		JBlock jmgLatitudeBlock = jmgLatitude.body();
		jmgLatitudeBlock._return(jfLatitude);
		
		// setter
		JMethod jmsLatitude = jc.method(JMod.PUBLIC, void.class, "setLatitude");
		JVar jvarLatitude = jmsLatitude.param(java.lang.String.class, "lat");
		JBlock jmsLatitudeBlock = jmsLatitude.body();
		jmsLatitudeBlock.assign(jfLatitude, jvarLatitude);
		
		// accuracy
		JFieldVar jfAccuracy = jc.field(JMod.PRIVATE, java.lang.String.class, "accuracy");
		JAnnotationUse jaAccuracyDesc = jfAccuracy.annotate(org.openhds.domain.annotations.Description.class);
		jaAccuracyDesc.param("description", "How accurate are the longitude/latitude readings for the Location");
		
		// getter
		JMethod jmgAccuracy = jc.method(JMod.PUBLIC, java.lang.String.class, "getAccuracy");
		JBlock jmgAccuracyBlock = jmgAccuracy.body();
		jmgAccuracyBlock._return(jfAccuracy);
		
		// setter
		JMethod jmsAccuracy = jc.method(JMod.PUBLIC, void.class, "setAccuracy");
		JVar jvarAccuracy = jmsAccuracy.param(java.lang.String.class, "acc");
		JBlock jmsAccuracyBlock = jmsAccuracy.body();
		jmsAccuracyBlock.assign(jfAccuracy, jvarAccuracy);
		
		// altitude
		JFieldVar jfAltitude = jc.field(JMod.PRIVATE, java.lang.String.class, "altitude");
		JAnnotationUse jaAltitudeDesc = jfAltitude.annotate(org.openhds.domain.annotations.Description.class);
		jaAltitudeDesc.param("description", "The altitude for the Location");
		
		// getter
		JMethod jmgAltitude = jc.method(JMod.PUBLIC, java.lang.String.class, "getAltitude");
		JBlock jmgAltitudeBlock = jmgAltitude.body();
		jmgAltitudeBlock._return(jfAltitude);
		
		// setter
		JMethod jmsAltitude = jc.method(JMod.PUBLIC, void.class, "setAltitude");
		JVar jvarAltitude = jmsAltitude.param(java.lang.String.class, "alt");
		JBlock jmsAltitudeBlock = jmsAltitude.body();
		jmsAltitudeBlock.assign(jfAltitude, jvarAltitude);
				
		// residencies
		JClass basicListResidencies = jCodeModel.ref(java.util.List.class);
		basicListResidencies = basicListResidencies.narrow(org.openhds.domain.model.Residency.class);
		JFieldVar jfResidencies = jc.field(JMod.PRIVATE , basicListResidencies, "residencies");
		JAnnotationUse jaResidenciesTarget = jfResidencies.annotate(javax.persistence.OneToMany.class);
		jaResidenciesTarget.param("targetEntity", org.openhds.domain.model.Residency.class);
		JAnnotationUse jaResidenciesColumn = jfResidencies.annotate(javax.persistence.JoinColumn.class);
		jaResidenciesColumn.param("name", "location_uuid");
		
		// getter
		JMethod jmgLocationResidencies = jc.method(JMod.PUBLIC, basicListResidencies, "getResidencies");
		JBlock jmgLocationResidenciesBlock = jmgLocationResidencies.body();
		jmgLocationResidenciesBlock._return(jfResidencies);
		
		// setter
		JMethod jmsLocationResidencies = jc.method(JMod.PUBLIC, void.class, "setResidencies");
		JVar jvarLocationResidencies = jmsLocationResidencies.param(basicListResidencies, "list");
		JBlock jmsLocationResidenciesBlock = jmsLocationResidencies.body();
		jmsLocationResidenciesBlock.assign(jfResidencies, jvarLocationResidencies);
	}
	
	public void buildClassAnnotations(JDefinedClass jc) {
		
		// create Description annotation
		JAnnotationUse jad = jc.annotate(org.openhds.domain.annotations.Description.class);
		jad.param("description", "All distinct Locations within the area of study are " +
				"represented here. A Location is identified by a uniquely generated " +
				"identifier that the system uses internally. Each Location has a name associated " +
				"with it and resides at a particular level within the Location Hierarchy.");
		
		// create Entity annotation
		jc.annotate(javax.persistence.Entity.class);
		
		JAnnotationUse jat = jc.annotate(javax.persistence.Table.class);
		jat.param("name", "location");
		
		JAnnotationUse jxmlRoot = jc.annotate(javax.xml.bind.annotation.XmlRootElement.class);
		jxmlRoot.param("name", "location");
	}
}
