package org.openhds.webservice.dto.wrapper;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.openhds.webservice.dto.SocialGroupDTO;

@XmlRootElement(name = "socialGroups")
public class SocialGroupDTOWrapper {
	
	int count;
	List<SocialGroupDTO> socialGroup = new ArrayList<SocialGroupDTO>();
	
	public List<SocialGroupDTO> getSocialGroup() {
		return socialGroup;
	}

	public void setSocialGroup(List<SocialGroupDTO> socialGroup) {
		this.socialGroup = socialGroup;
	}

	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
		
	public void increaseCount() {
		count++;
	}

}
