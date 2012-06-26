package org.openhds.webservice.dto;

import org.openhds.domain.model.LocationHierarchy;

public class LocationHierarchyDTO {
	
	String uuid;
	String extId;
	String name;
	String parent;
	String level;
	
	public LocationHierarchyDTO() { }
	
	public LocationHierarchyDTO(LocationHierarchy hierarchy) {		
		this.uuid = hierarchy.getUuid();
		this.extId = hierarchy.getExtId();
		this.name = hierarchy.getName();
		this.parent = hierarchy.getParent().getUuid();
		this.level = hierarchy.getLevel().getName();
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
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getParent() {
		return parent;
	}
	
	public void setParent(String parent) {
		this.parent = parent;
	}
	
	public String getLevel() {
		return level;
	}
	
	public void setLevel(String level) {
		this.level = level;
	}
}
