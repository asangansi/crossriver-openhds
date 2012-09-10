package org.openhds.dao.finder;

import org.hibernate.type.Type;

/**
 * Maps Enums to a custom Hibernate user type
 */
public class SimpleArgumentTypeFactory implements ArgumentTypeFactory {
    public Type getArgumentType(Object arg) {
        return null;
    }

}
