package org.openhds.dao.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openhds.dao.service.Dao;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.Location;
import org.openhds.domain.model.Residency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testingContext.xml")
public class ResidencyDaoImplTest {

    @Autowired
    @Qualifier("individualDao")
    Dao<Individual, String> individualDao;

    @Autowired
    @Qualifier("locationDao")
    Dao<Location, String> locationDao;
    
    @Autowired
    @Qualifier("fieldWorkerDao")
    Dao<FieldWorker, String> fieldWorkerDao;

    @Autowired
    ResidencyDao residencyDao;

    @Test
    public void shouldFindNoStartEvents() {
        Individual i = individualDao.findByProperty("extId", "UNK");

        Residency residency = new Residency();
    }
}
