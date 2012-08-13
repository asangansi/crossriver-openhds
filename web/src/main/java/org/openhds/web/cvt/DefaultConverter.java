package org.openhds.web.cvt;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class DefaultConverter implements Converter {
	
	private final boolean convertToUpperCase;

	public DefaultConverter(boolean convertToUpperCase) {
		this.convertToUpperCase = convertToUpperCase;
	}

	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {

		if (convertToUpperCase) {
			return value.toUpperCase().trim();
		} else {
			return value.trim();
		}
	}

	public String getAsString(FacesContext context, UIComponent component,
			Object value) {

		if (value == null)
			return null;
		
		if (convertToUpperCase) {
			return value.toString().toUpperCase().trim();
		} else {
			return value.toString().trim();
		}
	}
}

