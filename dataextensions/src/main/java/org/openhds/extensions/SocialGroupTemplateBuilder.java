package org.openhds.extensions;

import com.sun.codemodel.JAnnotationArrayMember;
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
 * A class that's responsible for building the core SocialGroup entity table
 * using the CodeModel.
 * 
 * @author Brian
 *
 */
public class SocialGroupTemplateBuilder implements ExtensionTemplate {
	
	JCodeModel jCodeModel;
	boolean templateBuilt = false;
	
	SocialGroupTemplateBuilder(JCodeModel jCodeModel) {
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

	
	public void buildClassAnnotations(JDefinedClass jc) {

		// create Description annotation
		JAnnotationUse jad = jc.annotate(org.openhds.domain.annotations.Description.class);
		jad.param("description", "A Social Group represents a distinct family within the " +
		"study area. Social Groups are identified by a uniquely generated identifier " +
		"which the system uses internally. A Social Group has one head of house which " +
		"all Membership relationships are based on.");
				
		// create Entity annotation
		jc.annotate(javax.persistence.Entity.class);
		
		JAnnotationUse jat = jc.annotate(javax.persistence.Table.class);
		jat.param("name", "socialgroup");
		
		JAnnotationUse jxmlRoot = jc.annotate(javax.xml.bind.annotation.XmlRootElement.class);
		jxmlRoot.param("name", "socialgroup");
	}

	public void buildFieldsAndMethods(JDefinedClass jc) {

		// serial uuid
		JFieldVar jfSerial = jc.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, long.class, "serialVersionUID");
		jfSerial.init(JExpr.lit(-5592935530217622317L));
		
		// extId
		JFieldVar jfExtId = jc.field(JMod.PRIVATE , java.lang.String.class, "extId");
		jfExtId.annotate(org.openhds.domain.constraint.Searchable.class);
		JAnnotationUse jaExtIdDesc = jfExtId.annotate(org.openhds.domain.annotations.Description.class);
		jaExtIdDesc.param("description", "External Id of the social group. This id is used internally.");
	
		// getter
		JMethod jmgExtId = jc.method(JMod.PUBLIC, java.lang.String.class, "getExtId");
		JBlock jmgExtIdBlock = jmgExtId.body();
		jmgExtIdBlock._return(jfExtId);
		
		// setter
		JMethod jmsExtId = jc.method(JMod.PUBLIC, void.class, "setExtId");
		JVar jvarExtId = jmsExtId.param(java.lang.String.class, "id");
		JBlock jmsExtIdBlock = jmsExtId.body();
		jmsExtIdBlock.assign(jfExtId, jvarExtId);
		
		// groupName
		JFieldVar jfGroupName = jc.field(JMod.PRIVATE , java.lang.String.class, "groupName");
		jfGroupName.annotate(org.openhds.domain.constraint.Searchable.class);
		JAnnotationUse jfGroupNameCheckBlank = jfGroupName.annotate(org.openhds.domain.constraint.CheckFieldNotBlank.class);
		jfGroupNameCheckBlank.param("message", "Group name cannot be blank");
		JAnnotationUse jaGroupNameDesc = jfGroupName.annotate(org.openhds.domain.annotations.Description.class);
		jaGroupNameDesc.param("description", "Name of the social group.");
		
		// getter
		JMethod jmgGroupName = jc.method(JMod.PUBLIC, java.lang.String.class, "getGroupName");
		JBlock jmgGroupNameBlock = jmgGroupName.body();
		jmgGroupNameBlock._return(jfGroupName);
		
		// setter
		JMethod jmsGroupName = jc.method(JMod.PUBLIC, void.class, "setGroupName");
		JVar jvarGroupName = jmsGroupName.param(java.lang.String.class, "name");
		JBlock jmsGroupNameBlock = jmsGroupName.body();
		jmsGroupNameBlock.assign(jfGroupName, jvarGroupName);
		
		// groupHead
		JFieldVar jfGroupHead = jc.field(JMod.PRIVATE , org.openhds.domain.model.Individual.class, "groupHead");
		JClass jIndividualRef = jCodeModel.ref(org.openhds.domain.model.Individual.class);
		jfGroupHead.init(JExpr._new(jIndividualRef));
		jfGroupHead.annotate(org.openhds.domain.constraint.Searchable.class);
		jfGroupHead.annotate(org.openhds.domain.constraint.CheckEntityNotVoided.class);
		jfGroupHead.annotate(org.openhds.domain.constraint.CheckIndividualNotUnknown.class);
		JAnnotationUse jaCheckAge = jfGroupHead.annotate(org.openhds.domain.constraint.CheckHouseholdHeadAge.class);
		jaCheckAge.param("allowNull", true);
		jaCheckAge.param("message", "The social group head is younger than the minimum age required in order to be a household head.");
		JAnnotationUse jfCascade = jfGroupHead.annotate(javax.persistence.ManyToOne.class);
		JAnnotationArrayMember motherArray = jfCascade.paramArray("cascade");
		motherArray.param(javax.persistence.CascadeType.ALL);
		JAnnotationUse jaGroupHeadDesc = jfGroupHead.annotate(org.openhds.domain.annotations.Description.class);
		jaGroupHeadDesc.param("description", "Individual who is head of the social group, identified by the external id.");
		
		// getter
		JMethod jmgGroupHead = jc.method(JMod.PUBLIC, org.openhds.domain.model.Individual.class, "getGroupHead");
		JBlock jmgGroupHeadBlock = jmgGroupHead.body();
		jmgGroupHeadBlock._return(jfGroupHead);
		
		// setter
		JMethod jmsGroupHead = jc.method(JMod.PUBLIC, void.class, "setGroupHead");
		JVar jvarGroupHead = jmsGroupHead.param(org.openhds.domain.model.Individual.class, "head");
		JBlock jmsGroupHeadBlock = jmsGroupHead.body();
		jmsGroupHeadBlock.assign(jfGroupHead, jvarGroupHead);
		
		// respondent
		JFieldVar jfRespondent = jc.field(JMod.PRIVATE , org.openhds.domain.model.Individual.class, "respondent");
		jfRespondent.annotate(org.openhds.domain.constraint.Searchable.class);
		jfRespondent.annotate(org.openhds.domain.constraint.CheckEntityNotVoided.class);
		jfRespondent.annotate(org.openhds.domain.constraint.CheckIndividualNotUnknown.class);
		JAnnotationUse jfRespondentCascade = jfRespondent.annotate(javax.persistence.ManyToOne.class);
		JAnnotationArrayMember respondentArray = jfRespondentCascade.paramArray("cascade");
		respondentArray.param(javax.persistence.CascadeType.MERGE);
		respondentArray.param(javax.persistence.CascadeType.PERSIST);
		jfRespondentCascade.param("targetEntity", org.openhds.domain.model.Individual.class);
		JAnnotationUse jaRespondentDesc = jfRespondent.annotate(org.openhds.domain.annotations.Description.class);
		jaRespondentDesc.param("description", "Individual who supplied the information for this social group.");
		
		// getter
		JMethod jmgRespondent = jc.method(JMod.PUBLIC, org.openhds.domain.model.Individual.class, "getRespondent");
		JBlock jmgRespondentBlock = jmgRespondent.body();
		jmgRespondentBlock._return(jfRespondent);
		
		// setter
		JMethod jmsRespondent = jc.method(JMod.PUBLIC, void.class, "setRespondent");
		JVar jvarRespondent = jmsRespondent.param(org.openhds.domain.model.Individual.class, "resp");
		JBlock jmsRespondentBlock = jmsRespondent.body();
		jmsRespondentBlock.assign(jfRespondent, jvarRespondent);
		
		// groupType
		JFieldVar jfGroupType = jc.field(JMod.PRIVATE , java.lang.String.class, "groupType");
		JAnnotationUse jaGroupType = jfGroupType.annotate(org.openhds.domain.constraint.ExtensionStringConstraint.class);
		jaGroupType.param("constraint", "socialGroupTypeConstraint");
		jaGroupType.param("message", "Invalid Value for social group type");
		jaGroupType.param("allowNull", true);
		JAnnotationUse jaGroupTypeDesc = jfGroupType.annotate(org.openhds.domain.annotations.Description.class);
		jaGroupTypeDesc.param("description", "Type of the social group.");
		
		// getter
		JMethod jmgGroupType = jc.method(JMod.PUBLIC, java.lang.String.class, "getGroupType");
		JBlock jmgGroupTypeBlock = jmgGroupType.body();
		jmgGroupTypeBlock._return(jfGroupType);
		
		// setter
		JMethod jmsGroupType = jc.method(JMod.PUBLIC, void.class, "setGroupType");
		JVar jvarGroupType = jmsGroupType.param(java.lang.String.class, "group");
		JBlock jmsGroupTypeBlock = jmsGroupType.body();
		jmsGroupTypeBlock.assign(jfGroupType, jvarGroupType);
				
		// memberships
		JClass basicSetMemberships = jCodeModel.ref(java.util.Set.class);
		basicSetMemberships = basicSetMemberships.narrow(org.openhds.domain.model.Membership.class);
		JFieldVar jfMemberships = jc.field(JMod.PRIVATE , basicSetMemberships, "memberships");
		JAnnotationUse jaMembershipsTarget = jfMemberships.annotate(javax.persistence.OneToMany.class);
		JAnnotationArrayMember targetArray = jaMembershipsTarget.paramArray("cascade");
		targetArray.param(javax.persistence.CascadeType.ALL);
		jaMembershipsTarget.param("mappedBy", "socialGroup");
		JAnnotationUse jaMembershipsDesc = jfMemberships.annotate(org.openhds.domain.annotations.Description.class);
		jaMembershipsDesc.param("description", "The set of all memberships of the social group.");
		
		// getter
		JMethod jmgMembership = jc.method(JMod.PUBLIC, basicSetMemberships, "getMemberships");
		JBlock jmgMembershipBlock = jmgMembership.body();
		jmgMembershipBlock._return(jfMemberships);
		
		// setter
		JMethod jmsMembership = jc.method(JMod.PUBLIC, void.class, "setMemberships");
		JVar jvarMembership = jmsMembership.param(basicSetMemberships, "list");
		JBlock jmsMembershipBlock = jmsMembership.body();
		jmsMembershipBlock.assign(jfMemberships, jvarMembership);
		
		// toString
		JMethod toString = jc.method(JMod.PUBLIC, java.lang.String.class, "toString");
		toString.annotate(Override.class);
		JBlock toStringBlock = toString.body();
		toStringBlock._return(JExpr.lit("Social Group"));
	}
}
