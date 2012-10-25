package org.openhds.dao.service.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openhds.dao.service.LocationHierarchyDao;
import org.openhds.domain.model.LocationHierarchy;

/**
 * A specialized class for the LocationHierarchy entity This was introduced because LocationHierarchy has to filter out
 * the root whenever it makes a query for searching and/or paging results
 */
public class LocationHierarchyDaoImpl extends BaseDaoImpl<LocationHierarchy, String> implements LocationHierarchyDao {

    public LocationHierarchyDaoImpl(Class<LocationHierarchy> entityType) {
        super(entityType);
    }

    @Override
    protected Criteria addImplicitRestrictions(Criteria criteria) {
        return criteria.add(Restrictions.ne("extId", "HIERARCHY_ROOT"));
    }
    
    public LocationHierarchy findLocationHierarchyWithHighestIdAtLevel(String prefix, LocationHierarchy parent) {
        Criteria criteria = getSession().createCriteria(entityType);
        criteria.add(Restrictions.eq("parent", parent));
        criteria.add(Restrictions.like("extId", prefix, MatchMode.START));
        criteria.addOrder(Order.desc("extId"));
        criteria.setMaxResults(1);
        
        return (LocationHierarchy) criteria.uniqueResult();
    }
}
