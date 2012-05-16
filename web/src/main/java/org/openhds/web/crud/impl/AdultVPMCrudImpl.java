package org.openhds.web.crud.impl;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import org.openhds.domain.model.AdultVPM;

public class AdultVPMCrudImpl extends EntityCrudImpl<AdultVPM, String> {
	
    // used for manual conversion between Date and Calendar since the openFaces Calendar doesn't support JSF Converters
    Date interviewDate;
    Date childDob;
    Date hospitalFacility1Date;
    Date hospitalFacility2Date;
    Date hospitalFacility3Date;
    Date certifiedCorrectDate;
    
    public AdultVPMCrudImpl(Class<AdultVPM> entityClass) {
		super(entityClass);
	}

    @Override
    public String edit() {
    	String outcome = super.edit();
    	
    	if (outcome != null) {
    		return "pretty:adultvpmEdit";
    	}
    	
    	return null;
    }

	public Date getInterviewDate() {
	    	
	    if (entityItem.getDateOfInterview() == null)
	    	return new Date();
	    	
	    return entityItem.getDateOfInterview().getTime();
	}

	public void setInterviewDate(Date interviewDate) throws ParseException {
				
		Calendar cal = Calendar.getInstance();
		cal.setTime(interviewDate);
		entityItem.setDateOfInterview(cal);
	}
	
	public Date getChildDob() {
    	
	    if (entityItem.getChildDob() == null)
	    	return new Date();
	    	
	    return entityItem.getChildDob().getTime();
	}

	public void setChildDob(Date childDob) throws ParseException {
				
		Calendar cal = Calendar.getInstance();
		cal.setTime(childDob);
		entityItem.setChildDob(cal);
	}
	
	public Date getHospitalFacility1Date() {
    	
	    if (entityItem.getHospitalFacility1Date() == null)
	    	return null;
	    	
	    return entityItem.getHospitalFacility1Date().getTime();
	}

	public void setHospitalFacility1Date(Date hospitalFacility1Date) throws ParseException {
				
		if (hospitalFacility1Date == null)
			return;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(hospitalFacility1Date);
		entityItem.setHospitalFacility1Date(cal);
	}
	
	public Date getHospitalFacility2Date() {
    	
	    if (entityItem.getHospitalFacility2Date() == null)
	    	return null;
	    	
	    return entityItem.getHospitalFacility2Date().getTime();
	}

	public void setHospitalFacility2Date(Date hospitalFacility2Date) throws ParseException {
		
		if (hospitalFacility2Date == null)
			return;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(hospitalFacility2Date);
		entityItem.setHospitalFacility2Date(cal);
	}
	
	public Date getHospitalFacility3Date() {
    	
	    if (entityItem.getHospitalFacility3Date() == null)
	    	return null;
	    	
	    return entityItem.getHospitalFacility3Date().getTime();
	}

	public void setHospitalFacility3Date(Date hospitalFacility3Date) throws ParseException {
			
		if (hospitalFacility3Date == null)
			return;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(hospitalFacility3Date);
		entityItem.setHospitalFacility3Date(cal);
	}
	
	public Date getCertifiedCorrectDate() {
    	
	    if (entityItem.getCertifiedCorrectDate() == null)
	    	return null;
	    	
	    return entityItem.getCertifiedCorrectDate().getTime();
	}

	public void setCertifiedCorrectDate(Date certifiedCorrectDate) throws ParseException {
			
		if (certifiedCorrectDate == null)
			return;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(certifiedCorrectDate);
		entityItem.setCertifiedCorrectDate(cal);
	}
}
