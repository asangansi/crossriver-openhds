package org.openhds.web.crud.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.openhds.domain.model.Role;
import org.openhds.domain.model.User;
import org.openhds.web.service.UserService;

public class UserCrudImpl extends EntityCrudImpl<User, String> {

	UserService service;
	String password;
	String retypedPassword;
	List<String> roles;

	public UserCrudImpl(Class<User> entityClass) {
        super(entityClass);
        roles = new ArrayList<String>();
    }
	
    @Override
    public String create() {
        try {
        	entityItem.setPassword(password);
        	service.evaluateUser(entityItem, retypedPassword);	
        	service.convertAndSetRoles(entityItem, roles);
        	roles = null;
        	super.create();
        } catch (Exception e) {
            jsfService.addError(e.getMessage());
        }
        return null;
    }
    
    @Override
    public String editSetup() {
    	String value = super.editSetup();
    	// manually touch the roles so they are fetched
    	// otherwise page will throw lazy initialization failure
    	entityItem.getRoles().size();
    	
    	password = entityItem.getPassword();
    	
    	return value;
    }
    
    @Override
    public String detailSetup() {
    	String value = super.detailSetup();
    	// manually touch the roles so they are fetched
    	// otherwise page will throw lazy intialization failure
    	entityItem.getRoles().size();
    	
    	return value;
    }
    
    @Override
    public String edit() {
        try {
        	entityItem.getRoles().clear();
        	service.convertAndSetRoles(entityItem, roles);        	
        	if (!StringUtils.isBlank(password) || !StringUtils.isBlank(retypedPassword)) {
        		entityItem.setPassword(password);
        	} else {
        		retypedPassword = entityItem.getPassword();
        	}
        	service.checkUser(entityItem, retypedPassword);
        	entityService.save(entityItem);
        } 
        catch(Exception e) {
        	jsfService.addError(e.getMessage());
        	return null;
		} 

        return detailSetup();
    }
      
    /**
     * Retrieves the available roles to be displayed as checkboxes on the UI.
     */
    public List<SelectItem> getRoleSelectItems() {
    	List<SelectItem> rolesSelectItems = new ArrayList<SelectItem>();
    	List<Role> roles = service.getRoles();
    	
    	for(Role role : roles) {
    		if (!role.isDeleted())
    			rolesSelectItems.add(new SelectItem(role.getName()));
    	}
    	return rolesSelectItems;
    }
       
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRetypedPassword() {
		return retypedPassword;
	}

	public void setRetypedPassword(String retypedPassword) {
		this.retypedPassword = retypedPassword;
	}

	public UserService getService() {
		return service;
	}

	public void setService(UserService service) {
		this.service = service;
	}
	
	/**
	 * Get all roles of which the entityItem belongs to.
	 */
	public List<String> getRoles() {
		Set<Role> roles = entityItem.getRoles();
		List<String> list = new ArrayList<String>();
		for (Role r : roles) {
			list.add(r.getName());
		}
		return list;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

}
