package org.openhds.webservice.dto;

import java.util.Calendar;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.Relationship;
import org.openhds.domain.service.SitePropertiesService;
import org.openhds.domain.constraint.AppContextAware;
import org.openhds.domain.util.CalendarAdapter;

public class RelationshipDTO extends AppContextAware {
	
	SitePropertiesService properties;
	
	String maleIndividual;
	String femaleIndividual;
	Calendar startDate;
	
	public RelationshipDTO() {}
	
	public RelationshipDTO(Relationship relationship) {
		properties = (SitePropertiesService) context.getBean("siteProperties");
		
		Individual a = relationship.getIndividualA();
		Individual b = relationship.getIndividualB();
		
		this.startDate = relationship.getStartDate();
		
		if (a.getGender().equals(properties.getMaleCode())) 
			this.maleIndividual = a.getExtId();
		else
			this.femaleIndividual = a.getExtId();
		
		if (b.getGender().equals(properties.getMaleCode())) 
			this.maleIndividual = b.getExtId();
		else
			this.femaleIndividual = b.getExtId();
	}
	
	public static boolean isValid(Relationship relationship) {		
		Individual a = relationship.getIndividualA();
		Individual b = relationship.getIndividualB();
		if (a.getGender().equals(b.getGender()))
			return false;
		return true;
	}
	
	public String getMaleIndividual() {
		return maleIndividual;
	}

	public void setMaleIndividual(String maleIndividual) {
		this.maleIndividual = maleIndividual;
	}

	public String getFemaleIndividual() {
		return femaleIndividual;
	}

	public void setFemaleIndividual(String femaleIndividual) {
		this.femaleIndividual = femaleIndividual;
	}

	@XmlJavaTypeAdapter(value = CalendarAdapter.class)
	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}
}
