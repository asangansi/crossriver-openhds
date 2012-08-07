package org.openhds.domain.constraint;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import org.openhds.domain.constaint.impl.CheckWorkStatusAndOccupationImpl;

@Target( { TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = CheckWorkStatusAndOccupationImpl.class)
@Documented
public @interface CheckWorkStatusAndOccupation {

	String message() default "Individual with work status: 1 can not have an occupational status of: 1 or 8";
	
	Class<?>[] groups() default{};
	
	Class<? extends Payload>[] payload() default {};
}

