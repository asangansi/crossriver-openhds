package org.openhds.webservice.dto.wrapper;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.openhds.webservice.dto.FieldWorkerDTO;

@XmlRootElement(name = "fieldworkers")
public class FieldWorkerDTOWrapper {
	
	int count;
	List<FieldWorkerDTO> fieldworker = new ArrayList<FieldWorkerDTO>();
	
	public List<FieldWorkerDTO> getFieldworker() {
		return fieldworker;
	}

	public void setFieldworker(List<FieldWorkerDTO> fieldworker) {
		this.fieldworker = fieldworker;
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
