package org.openhds.domain.constaint.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.openhds.domain.constraint.CheckWorkStatusAndOccupation;
import org.openhds.domain.model.Individual;

public class CheckWorkStatusAndOccupationImpl implements ConstraintValidator<CheckWorkStatusAndOccupation, Individual> {

	public void initialize(CheckWorkStatusAndOccupation constraintAnnotation) { }

	// this constraint is specific for cross river. not to be used otherwise
	public boolean isValid(Individual indiv, ConstraintValidatorContext context) {
		
		if (indiv.getWorkStatus() == null || indiv.getOccupationalStatus() == null)
			return true;
		
		if (indiv.getWorkStatus() == 2 && 
				indiv.getOccupationalStatus() != 1 || indiv.getOccupationalStatus() != 8)
			return false;
		return true;
	}
}
