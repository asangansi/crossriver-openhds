package org.openhds.webservice.dto.wrapper;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.openhds.webservice.dto.LocationHierarchyDTO;

/**
 * This is necessary because there was no other way to include the count in the xml output.
 * The count is needed because the mobile phone uses this as a progress indicator when
 * downloading entities.
 */
@XmlRootElement(name = "hierarchys")
public class LocationHierarchyDTOWrapper {
	
	int count;
	List<LocationHierarchyDTO> hierarchy = new ArrayList<LocationHierarchyDTO>();
	
	public List<LocationHierarchyDTO> getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(List<LocationHierarchyDTO> hierarchy) {
		this.hierarchy = hierarchy;
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

