package org.openhds.webservice.dto;

import org.openhds.domain.model.FieldWorker;

public class FieldWorkerDTO {
	
	String uuid;
	String extId;
	String firstName;
	String lastName;
	
	public FieldWorkerDTO() { }
	
	public FieldWorkerDTO(FieldWorker fieldWorker) {
		this.uuid = fieldWorker.getUuid();
		this.extId = fieldWorker.getExtId();
		
		if (fieldWorker.getFirstName() == null)
			this.firstName = "Unknown";
		else
			this.firstName = fieldWorker.getFirstName();
		
		if (lastName == null)
			this.lastName = "Unknown";
		else 
			this.lastName = fieldWorker.getLastName();
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
}
