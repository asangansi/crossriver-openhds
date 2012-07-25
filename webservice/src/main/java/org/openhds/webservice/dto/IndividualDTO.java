package org.openhds.webservice.dto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.h2.util.StringUtils;
import org.openhds.domain.constraint.AppContextAware;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.SocialGroup;
import org.openhds.domain.service.SitePropertiesService;
import org.openhds.domain.util.CalendarAdapter;

public class IndividualDTO extends AppContextAware {
	
	SitePropertiesService properties;
		
	String uuid;
	String extId;
	String firstName;
	String lastName;
	Calendar dob;
	String gender;
	String mother;
	String father;
	String status;
	String currentResidence;
	String groups;
	
	public IndividualDTO() {}
	
	// make sure to call isValid before calling this constructor
	// it protects against malformed data
	public IndividualDTO(Individual individual, List<SocialGroup> groups) {
		properties = (SitePropertiesService) context.getBean("siteProperties");
		
		this.uuid = individual.getUuid();
		this.extId = individual.getExtId();
		this.firstName = individual.getFirstName();
		this.lastName = individual.getLastName();
		this.dob = individual.getDob();
		this.mother = individual.getMother().getExtId();
		this.father = individual.getFather().getExtId();
		this.status = individual.getStatus();
		this.currentResidence = individual.getCurrentResidency().getLocation().getUuid();
		
		if (groups != null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < groups.size(); i++) {
				if ((i+1) >= groups.size()) 
					sb.append(groups.get(i).getUuid());
				else 
					sb.append(groups.get(i).getUuid() + ",");
			}
			this.groups = sb.toString();
		}
					
		if (individual.getGender().equals(properties.getMaleCode()))
			gender = "Male";
		else
			gender = "Female";
	}
	
	public static boolean isValid(Individual individual) {			
		if (StringUtils.isNullOrEmpty(individual.getUuid()) || 
			StringUtils.isNullOrEmpty(individual.getExtId()) ||
			StringUtils.isNullOrEmpty(individual.getFirstName()) ||
			StringUtils.isNullOrEmpty(individual.getLastName()) || 
			individual.getDob() == null ||
			StringUtils.isNullOrEmpty(individual.getGender()) ||
			individual.getMother() == null ||
			individual.getFather() == null ||
			StringUtils.isNullOrEmpty(individual.getMother().getUuid()) ||
			StringUtils.isNullOrEmpty(individual.getFather().getUuid()) ||
			individual.getCurrentResidency() == null || 
			individual.getCurrentResidency().getLocation() == null)
			return false;
		return true;
	}
			
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getExtId() {
		return extId;
	}
	
	public void setExtId(String extId) {
		this.extId = extId;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@XmlJavaTypeAdapter(value = CalendarAdapter.class)
	public Calendar getDob() {
		return dob;
	}
	
	public void setDob(Calendar dob) {
		this.dob = dob;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getMother() {
		return mother;
	}
	
	public void setMother(String mother) {
		this.mother = mother;
	}
	
	public String getFather() {
		return father;
	}
	
	public void setFather(String father) {
		this.father = father;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getCurrentResidence() {
		return currentResidence;
	}

	public void setCurrentResidence(String currentResidence) {
		this.currentResidence = currentResidence;
	}
	
	public String getGroups() {
		return groups;
	}

	public void setGroups(String groups) {
		this.groups = groups;
	}
}