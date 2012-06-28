package org.openhds.webservice.dto;

import java.util.Calendar;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.openhds.domain.model.Round;
import org.openhds.domain.util.CalendarAdapter;

public class RoundDTO {
	
	String uuid;
	Integer roundNumber;
	Calendar startDate;
	Calendar endDate;
	String remarks;
	
	public RoundDTO() { }
	
	public RoundDTO(Round round) {
		this.uuid = round.getUuid();
		this.roundNumber = round.getRoundNumber();
		this.startDate = round.getStartDate();
		this.endDate = round.getEndDate();
		this.remarks = round.getRemarks();
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public Integer getRoundNumber() {
		return roundNumber;
	}
	
	public void setRoundNumber(Integer roundNumber) {
		this.roundNumber = roundNumber;
	}
	
	@XmlJavaTypeAdapter(value = CalendarAdapter.class)
	public Calendar getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}
	
	@XmlJavaTypeAdapter(value = CalendarAdapter.class)
	public Calendar getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}
	
	public String getRemarks() {
		return remarks;
	}
	
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
