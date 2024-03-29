package org.openhds.controller.service;

import java.sql.SQLException;
import java.util.List;

import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.domain.annotations.Authorized;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.OutMigration;
import org.openhds.domain.model.PrivilegeConstants;

public interface OutMigrationService {

    @Authorized({ PrivilegeConstants.CREATE_ENTITY })
    void evaluateOutMigration(OutMigration outMigration) throws ConstraintViolations;

    @Authorized({ PrivilegeConstants.VIEW_ENTITY })
    List<OutMigration> getOutMigrations(Individual individual);

    @Authorized({ PrivilegeConstants.CREATE_ENTITY })
    void createOutMigration(OutMigration outMigration) throws ConstraintViolations, IllegalArgumentException,
            ConstraintViolations, SQLException;

    @Authorized({ PrivilegeConstants.DELETE_ENTITY })
    void deleteOutMigration(OutMigration outMigration) throws ConstraintViolations, SQLException;

    @Authorized({ PrivilegeConstants.EDIT_ENTITY })
    void editOutMigration(OutMigration entityItem) throws ConstraintViolations, SQLException;
}
