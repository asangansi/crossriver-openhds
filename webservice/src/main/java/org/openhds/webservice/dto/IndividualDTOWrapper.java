package org.openhds.webservice.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "individuals")
public class IndividualDTOWrapper {
	
	int count;
	List<IndividualDTO> individual = new ArrayList<IndividualDTO>();
	
	public List<IndividualDTO> getIndividual() {
		return individual;
	}

	public void setIndividual(List<IndividualDTO> individual) {
		this.individual = individual;
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
