package org.openhds.web.cvt;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.openhds.dao.service.Dao;
import org.openhds.domain.model.Individual;
import org.openhds.web.beans.BaselineBean;

public class IndividualConverter implements Converter {
	
private static final int UNKNOWN_ID = 99;
	
	private BaselineBean baselineController;
	private Dao<Individual, String> dao;
	private boolean useUnknownIndividual;

	public IndividualConverter(BaselineBean baselineController, Dao<Individual, String> dao, boolean useUnknownIndividual) {
		this.baselineController = baselineController;
		this.dao = dao;
		this.useUnknownIndividual = useUnknownIndividual;
	}
	
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
		int individualId = convertStringToInt(value);
		
		if (individualId == UNKNOWN_ID) {
			return getUnknownIndividual();
		}
		
		if (individualId < 1 || individualId > baselineController.getIndividualCount()) {
			individualIdOutOfRangeException();
		}
		
		return matchIndividualIdToIndividual(individualId);
	}

	private int convertStringToInt(String value) {
		int individualNumber = 0;
		
		try {
			individualNumber = Integer.parseInt(value);
		} catch(NumberFormatException e) {
			handleInvalidIndividualId(value);
		}
		
		return individualNumber;
	}

	private void handleInvalidIndividualId(String value) {
		generateFacesConverterException("Entered in an invalid Individual Id");
	}

	private void generateFacesConverterException(String errorMessage) {
		FacesMessage message = new FacesMessage(errorMessage);
		message.setSeverity(FacesMessage.SEVERITY_ERROR);
		throw new ConverterException(message);
	}

	private Object getUnknownIndividual() {
		if (useUnknownIndividual) {
			return dao.findByProperty("extId", "UNK");
		} else {
			Individual indiv = new Individual();
			indiv.setExtId(BaselineBean.TEMPORARY_RELATIONSHIP_EXT_ID);
			return indiv;
		}
	}
	
	private void individualIdOutOfRangeException() {
		generateFacesConverterException("Individual Id is out of range");
	}
	
	private Object matchIndividualIdToIndividual(int individualId) {
		Individual[] individuals = baselineController.getIndividuals();
		
		if (individuals[individualId] == null) {
			individualIdCannotReferToSelf();
		}
		
		individualId -= 1; // off by 1 array index, e.g. user types 1 which translate to index 0
		
		return dao.findByProperty("extId", individuals[individualId].getExtId());
	}
	
	private void individualIdCannotReferToSelf() {
		generateFacesConverterException("Individual Id cannot refer to itself");
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		if (arg2 instanceof Individual) {
			Individual individual = (Individual) arg2;
			
			if (individual.getExtId().equals("UNK") || (individual.getExtId().equals(BaselineBean.TEMPORARY_RELATIONSHIP_EXT_ID) && !useUnknownIndividual)) {
				return "" + UNKNOWN_ID;
			}
			
			Individual[] individuals = baselineController.getIndividuals();
			
			for(int i = 0; i < individuals.length; i++) {
				if (individual.getExtId().equals(individuals[i].getExtId())) {
					return convertIntToString(i + 1);
				}
			}
		}
		
		return null;		
	}

	private String convertIntToString(int i) {
		if (i >= 1 && i <= 9) {
			return "0" + i;
		} else {
			return "" + i;
		}
	}

}
