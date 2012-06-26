package org.openhds.webservice.dto.wrapper;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.openhds.webservice.dto.LocationDTO;

/**
 * This is necessary because there was no other way to include the count in the xml output.
 * The count is needed because the mobile phone uses this as a progress indicator when
 * downloading entities.
 */
@XmlRootElement(name = "locations")
public class LocationDTOWrapper {
	
	int count;
	List<LocationDTO> location = new ArrayList<LocationDTO>();
	
	public List<LocationDTO> getLocation() {
		return location;
	}

	public void setLocation(List<LocationDTO> location) {
		this.location = location;
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

