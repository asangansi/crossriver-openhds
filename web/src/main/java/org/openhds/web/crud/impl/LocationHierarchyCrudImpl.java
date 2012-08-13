package org.openhds.web.crud.impl;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.openhds.controller.exception.ConstraintViolations;
import org.openhds.domain.model.LocationHierarchy;
import org.openhds.controller.service.LocationHierarchyService;

public class LocationHierarchyCrudImpl extends EntityCrudImpl<LocationHierarchy, String> {
	
	LocationHierarchyService service;
	Boolean manualId = false;
	String locationHierarchyId = null;
	
	public LocationHierarchyCrudImpl(Class<LocationHierarchy> entityClass) {
        super(entityClass);
    }

    @Override
    public String create() {
    	try {
    		if (manualId) {
    			entityItem.setExtId(locationHierarchyId);
    			service.evaluateLocationHierarchy(entityItem, true);
    		} else {
    			entityItem.setExtId(null);
    			service.evaluateLocationHierarchy(entityItem, false);
    		}
	        return super.create();		
    	}
    	catch(ConstraintViolations e) {
    		jsfService.addError(e.getMessage());
    	}
    	return null;
    }
    
    @Override
    protected void reset(boolean resetPaging, boolean resetEntityitem) {
    	super.reset(resetPaging, resetEntityitem);
    	manualId = false;
    	locationHierarchyId = null;
    }
    
	public void updateManualId(ValueChangeEvent event) {
		// empty so manualId value is updated
	}
    
    public Boolean getManualId() {
		return manualId;
	}

	public void setManualId(Boolean manualId) {
		this.manualId = manualId;
	}

	public String getLocationHierarchyId() {
		return locationHierarchyId;
	}

	public void setLocationHierarchyId(String locationHierarchyId) {
		this.locationHierarchyId = locationHierarchyId;
	}

	@Override
    public String edit() {
    	
    	LocationHierarchy persistedItem = (LocationHierarchy)converter.getAsObject(FacesContext.getCurrentInstance(), null, jsfService.getReqParam("itemId"));
    	
    	try {
    		service.checkLocationHierarchy(persistedItem, entityItem);	
	        return super.edit();		
    	}
    	catch(ConstraintViolations e) {
    		jsfService.addError(e.getMessage());
    	}
    	return null;
    }
    
    @Override
    public String delete() {
    	
    	LocationHierarchy persistedItem = (LocationHierarchy)converter.getAsObject(FacesContext.getCurrentInstance(), null, jsfService.getReqParam("itemId"));
    	
    	try {
    		service.deleteLocationHierarchy(persistedItem);	
	        return super.delete();		
    	}
    	catch(ConstraintViolations e) {
    		jsfService.addError(e.getMessage());
    	}
    	return null;
    }
   	
	public LocationHierarchyService getService() {
		return service;
	}

	public void setService(LocationHierarchyService service) {
		this.service = service;
	}
}
