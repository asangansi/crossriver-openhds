package org.openhds.domain.constaint.impl;

import java.util.Calendar;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.openhds.domain.constraint.CheckCalendar;

public class CheckCalendarImpl implements ConstraintValidator<CheckCalendar, Calendar> {

	public void initialize(CheckCalendar arg0) {	}

	public boolean isValid(Calendar cal, ConstraintValidatorContext arg1) {
		
		if (cal == null)
			return true;
		
		int year = cal.get(Calendar.YEAR);
		
		if (year < 1900)
			return false;

		return true;
	}

}
