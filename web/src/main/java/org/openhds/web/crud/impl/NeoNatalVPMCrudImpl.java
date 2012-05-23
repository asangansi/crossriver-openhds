package org.openhds.web.crud.impl;

import java.util.Calendar;
import java.util.Date;

import org.openhds.domain.model.NeoNatalVPM;

public class NeoNatalVPMCrudImpl extends EntityCrudImpl<NeoNatalVPM, String> {

	 // used for manual conversion between Date and Calendar since the openFaces Calendar doesn't support JSF Converters
    Date interviewDate;
    Date childDob;
    Date childDeathDate;
    Date certifiedCorrectDate;
    
	public NeoNatalVPMCrudImpl(Class<NeoNatalVPM> entityClass) {
		super(entityClass);
	}

	@Override
	public String edit() {
    	String outcome = super.edit();
    	
    	if (outcome != null) {
    		return "pretty:neonatalvpmEdit";
    	}
    	
    	return null;		
	}

	public Date getInterviewDate() {
		if (entityItem.getDateOfInterview() == null)
		    return new Date();  	
		return entityItem.getDateOfInterview().getTime();
	}

	public void setInterviewDate(Date interviewDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(interviewDate);
		entityItem.setDateOfInterview(cal);
	}

	public Date getChildDob() {
		if (entityItem.getChildDob() == null)
		    return new Date();  	
		return entityItem.getChildDob().getTime();
	}

	public void setChildDob(Date childDob) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(childDob);
		entityItem.setChildDob(cal);
	}

	public Date getChildDeathDate() {
		if (entityItem.getChildDeathDate() == null)
		    return new Date();  	
		return entityItem.getChildDeathDate().getTime();
	}

	public void setChildDeathDate(Date childDeathDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(childDeathDate);
		entityItem.setChildDeathDate(cal);
	}
	
	public Date getCertifiedCorrectDate() {
		if (entityItem.getCertifiedCorrectDate() == null)
		    return new Date();  	
		return entityItem.getCertifiedCorrectDate().getTime();
	}

	public void setCertifiedCorrectDate(Date certifiedCorrectDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(certifiedCorrectDate);
		entityItem.setChildDeathDate(cal);
	}
}
