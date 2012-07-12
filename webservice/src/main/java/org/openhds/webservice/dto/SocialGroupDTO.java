package org.openhds.webservice.dto;

import org.openhds.domain.model.SocialGroup;

public class SocialGroupDTO {
		
	String uuid;
	String extId;
	String groupName;
	String groupHead;
	String status;
	
	public SocialGroupDTO() { }
	
	public SocialGroupDTO(SocialGroup socialGroup) {
		this.uuid = socialGroup.getUuid();
		this.extId = socialGroup.getExtId();
		this.groupName = socialGroup.getGroupName();
		this.groupHead = socialGroup.getGroupHead().getUuid();
		this.status = socialGroup.getStatus();
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

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupHead() {
		return groupHead;
	}

	public void setGroupHead(String groupHead) {
		this.groupHead = groupHead;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
