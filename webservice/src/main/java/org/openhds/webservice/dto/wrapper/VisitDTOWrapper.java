package org.openhds.webservice.dto.wrapper;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.openhds.webservice.dto.VisitDTO;

@XmlRootElement(name = "visits")
public class VisitDTOWrapper {
	
	int count;
	List<VisitDTO> visit = new ArrayList<VisitDTO>();

	public List<VisitDTO> getVisit() {
		return visit;
	}

	public void setVisit(List<VisitDTO> visit) {
		this.visit = visit;
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
