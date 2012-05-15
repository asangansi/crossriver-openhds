package org.openhds.specialstudy.domain;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import org.openhds.specialstudy.domain.SocialGroup;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

privileged aspect SocialGroupDataOnDemand_Roo_DataOnDemand {
    
    declare @type: SocialGroupDataOnDemand: @Component;
    
    private Random SocialGroupDataOnDemand.rnd = new SecureRandom();
    
    private List<SocialGroup> SocialGroupDataOnDemand.data;
    
    public SocialGroup SocialGroupDataOnDemand.getNewTransientSocialGroup(int index) {
        org.openhds.specialstudy.domain.SocialGroup obj = new org.openhds.specialstudy.domain.SocialGroup();
        obj.setAdfood("adfood_" + index);
        obj.setCfuel("cfuel_" + index);
        obj.setDfood(new Integer(index));
        obj.setEnfood("enfood_" + index);
        obj.setEnland("enland_" + index);
        obj.setFaid("faid_" + index);
        obj.setFloor("floor_" + index);
        obj.setFormsCheckedBy("formsCheckedBy_" + index);
        obj.setFormsReceivedBy("formsReceivedBy_" + index);
        obj.setHaveBicycle(new Boolean(true));
        obj.setHaveCar(new Boolean(true));
        obj.setHaveDvdVcrVcd(new Boolean(true));
        obj.setHaveElectricGasStove(new Boolean(true));
        obj.setHaveElectricity(new Boolean(true));
        obj.setHaveFan(new Boolean(true));
        obj.setHaveGenerator(new Boolean(true));
        obj.setHaveGrindingMill(new Boolean(true));
        obj.setHaveIron(new Boolean(true));
        obj.setHaveKeroseneStove(new Boolean(true));
        obj.setHaveMotorcycle(new Boolean(true));
        obj.setHavePersonalComputer(new Boolean(true));
        obj.setHavePushTruck(new Boolean(true));
        obj.setHaveRadio(new Boolean(true));
        obj.setHaveRefrigerator(new Boolean(true));
        obj.setHaveSewingMachine(new Boolean(true));
        obj.setHaveSolar(new Boolean(true));
        obj.setHaveStereo(new Boolean(true));
        obj.setHaveTelephone(new Boolean(true));
        obj.setHaveTelevision(new Boolean(true));
        obj.setHaveTractor(new Boolean(true));
        obj.setHouseholdId("householdId_" + index);
        obj.setInterviewTimeEnd("interviewTimeEnd_" + index);
        obj.setInterviewTimeStart("interviewTimeStart_" + index);
        obj.setLand("land_" + index);
        obj.setLight("light_" + index);
        obj.setMeals("meals_" + index);
        obj.setMfood(new Integer(index));
        obj.setMstaple("mstaple_" + index);
        obj.setNumDogs(new Integer(index));
        obj.setNumFouls(new Integer(index));
        obj.setNumGoats(new Integer(index));
        obj.setNumPigs(new Integer(index));
        obj.setNumRabbits(new Integer(index));
        obj.setNumSheep(new Integer(index));
        obj.setOtherAnimals("otherAnimals_" + index);
        obj.setOwland("owland_" + index);
        obj.setRoof("roof_" + index);
        obj.setRooms(new Integer(index));
        obj.setSrooms(new Integer(index));
        obj.setTenure("tenure_" + index);
        obj.setToilet("toilet_" + index);
        obj.setWall("wall_" + index);
        obj.setWaste("waste_" + index);
        obj.setWater("water_" + index);
        return obj;
    }
    
    public SocialGroup SocialGroupDataOnDemand.getSpecificSocialGroup(int index) {
        init();
        if (index < 0) index = 0;
        if (index > (data.size()-1)) index = data.size() - 1;
        SocialGroup obj = data.get(index);
        return SocialGroup.findSocialGroup(obj.getId());
    }
    
    public SocialGroup SocialGroupDataOnDemand.getRandomSocialGroup() {
        init();
        SocialGroup obj = data.get(rnd.nextInt(data.size()));
        return SocialGroup.findSocialGroup(obj.getId());
    }
    
    public boolean SocialGroupDataOnDemand.modifySocialGroup(SocialGroup obj) {
        return false;
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void SocialGroupDataOnDemand.init() {
        if (data != null) {
            return;
        }
        
        data = org.openhds.specialstudy.domain.SocialGroup.findSocialGroupEntries(0, 10);
        if (data == null) throw new IllegalStateException("Find entries implementation for 'SocialGroup' illegally returned null");
        if (data.size() > 0) {
            return;
        }
        
        data = new java.util.ArrayList<org.openhds.specialstudy.domain.SocialGroup>();
        for (int i = 0; i < 10; i++) {
            org.openhds.specialstudy.domain.SocialGroup obj = getNewTransientSocialGroup(i);
            obj.persist();
            data.add(obj);
        }
    }
    
}
