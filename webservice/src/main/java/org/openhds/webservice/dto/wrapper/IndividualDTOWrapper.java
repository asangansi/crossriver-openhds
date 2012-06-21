package org.openhds.webservice.dto.wrapper;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.openhds.webservice.dto.IndividualDTO;

/**
 * This is necessary because there was no other way to include the count in the xml output.
 * The count is needed because the mobile phone uses this as a progress indicator when
 * downloading entities.
 */
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
