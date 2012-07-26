package org.openhds.webservice.dto.wrapper;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.openhds.webservice.dto.RelationshipDTO;

@XmlRootElement(name = "relationships")
public class RelationshipDTOWrapper {
	
	int count;
	List<RelationshipDTO> relationship = new ArrayList<RelationshipDTO>();
	
	public List<RelationshipDTO> getRelationship() {
		return relationship;
	}

	public void setRelationship(List<RelationshipDTO> relationship) {
		this.relationship = relationship;
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
