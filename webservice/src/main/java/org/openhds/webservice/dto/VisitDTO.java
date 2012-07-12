package org.openhds.webservice.dto;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.openhds.domain.model.Visit;
import org.openhds.domain.util.CalendarAdapter;

/**
 * DTO Class for Visit. This was created because the Visit object graph is large
 * because of its relationships. This class will wrap a visit and only contains
 * a the smallest necessary fields for a visit
 */
@XmlRootElement(name = "visit")
public class VisitDTO {

	String uuid;
	String extId;
	String visitLocation;
	Calendar visitDate;
	Integer roundNumber;
	String status;

	public VisitDTO() { }

	public VisitDTO(Visit fromVisit) {
		this.uuid = fromVisit.getUuid();
		this.extId = fromVisit.getExtId();
		this.visitLocation = fromVisit.getVisitLocation().getUuid();
		this.visitDate = fromVisit.getVisitDate();
		this.roundNumber = fromVisit.getRoundNumber();
		this.status = fromVisit.getStatus();
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

	public String getVisitLocation() {
		return visitLocation;
	}

	public void setVisitLocation(String visitLocation) {
		this.visitLocation = visitLocation;
	}

	@XmlJavaTypeAdapter(value = CalendarAdapter.class)
	public Calendar getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(Calendar visitDate) {
		this.visitDate = visitDate;
	}

	public Integer getRoundNumber() {
		return roundNumber;
	}

	public void setRoundNumber(Integer roundNumber) {
		this.roundNumber = roundNumber;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
