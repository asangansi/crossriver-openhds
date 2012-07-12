package org.openhds.domain.constraint;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import org.openhds.domain.constaint.impl.CheckCalendarImpl;

@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = CheckCalendarImpl.class)
@Documented
public @interface CheckCalendar {

	String message() default "Invalid value for calendar";
	
	Class<?>[] groups() default{};
	
	Class<? extends Payload>[] payload() default {};
}
