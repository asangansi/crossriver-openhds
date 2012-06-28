package org.openhds.webservice.dto.wrapper;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.openhds.webservice.dto.RoundDTO;

@XmlRootElement(name = "rounds")
public class RoundDTOWrapper {
	
	int count;
	List<RoundDTO> round = new ArrayList<RoundDTO>();
	
	public List<RoundDTO> getRound() {
		return round;
	}

	public void setRound(List<RoundDTO> round) {
		this.round = round;
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
