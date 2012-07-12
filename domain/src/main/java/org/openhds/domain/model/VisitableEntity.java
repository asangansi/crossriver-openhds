package org.openhds.domain.model;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.openhds.domain.annotations.Description;

/**
 * A class that extends this class indicates they are capable of being visited
 * This is used primarily on the core updates of the HDS
 */
@MappedSuperclass
public class VisitableEntity extends AuditableCollectedEntity {

	private static final long serialVersionUID = -4321732774913442147L;
	
	@ManyToOne
	@Description(description = "The visit for this event")
	private Visit visit;

	public Visit getVisit() {
		return visit;
	}

	public void setVisit(Visit vist) {
		this.visit = vist;
	}

}
