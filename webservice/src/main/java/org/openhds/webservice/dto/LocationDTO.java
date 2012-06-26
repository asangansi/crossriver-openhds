package org.openhds.webservice.dto;

import org.h2.util.StringUtils;
import org.openhds.domain.model.Location;

public class LocationDTO {
	
	String uuid;
	String extId;
	String name;
	String latitude;
	String longitude;
	String hierarchy;
	
	public LocationDTO() {}
	
	// make sure to call isValid before calling this constructor
	// it protects against malformed data
	public LocationDTO(Location location) {		
		this.uuid = location.getUuid();
		this.extId = location.getExtId();
		this.name = location.getLocationName();
		this.hierarchy = location.getLocationLevel().getUuid();
		
		if (location.getLatitude() == null)
			this.latitude = "Unknown";
		if (location.getLongitude() == null) 
			this.longitude = "Unknown";
	}
	
	public static boolean isValid(Location location) {			
		if (StringUtils.isNullOrEmpty(location.getUuid()) || 
			StringUtils.isNullOrEmpty(location.getExtId()) ||
			StringUtils.isNullOrEmpty(location.getLocationName()) ||
			location.getLocationLevel() == null ||
			StringUtils.isNullOrEmpty(location.getLocationLevel().getUuid()))
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
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getHierarchy() {
		return hierarchy;
	}
	
	public void setHierarchy(String hierarchy) {
		this.hierarchy = hierarchy;
	}
	
	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
}
