package org.openhds.specialstudy.domain;

import java.lang.String;

privileged aspect SocialGroup_Roo_ToString {
    
    public String SocialGroup.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("HouseholdId: ").append(getHouseholdId()).append(", ");
        sb.append("Version: ").append(getVersion()).append(", ");
        sb.append("InterviewTimeStart: ").append(getInterviewTimeStart()).append(", ");
        sb.append("Water: ").append(getWater()).append(", ");
        sb.append("Toilet: ").append(getToilet()).append(", ");
        sb.append("Rooms: ").append(getRooms()).append(", ");
        sb.append("Srooms: ").append(getSrooms()).append(", ");
        sb.append("Floor: ").append(getFloor()).append(", ");
        sb.append("Roof: ").append(getRoof()).append(", ");
        sb.append("Wall: ").append(getWall()).append(", ");
        sb.append("Cfuel: ").append(getCfuel()).append(", ");
        sb.append("Light: ").append(getLight()).append(", ");
        sb.append("HaveCar: ").append(getHaveCar()).append(", ");
        sb.append("HaveMotorcycle: ").append(getHaveMotorcycle()).append(", ");
        sb.append("HaveBicycle: ").append(getHaveBicycle()).append(", ");
        sb.append("HaveElectricity: ").append(getHaveElectricity()).append(", ");
        sb.append("HaveSolar: ").append(getHaveSolar()).append(", ");
        sb.append("HaveRefrigerator: ").append(getHaveRefrigerator()).append(", ");
        sb.append("HaveTelevision: ").append(getHaveTelevision()).append(", ");
        sb.append("HaveDvdVcrVcd: ").append(getHaveDvdVcrVcd()).append(", ");
        sb.append("HaveRadio: ").append(getHaveRadio()).append(", ");
        sb.append("HaveSewingMachine: ").append(getHaveSewingMachine()).append(", ");
        sb.append("HaveStereo: ").append(getHaveStereo()).append(", ");
        sb.append("HaveIron: ").append(getHaveIron()).append(", ");
        sb.append("HaveFan: ").append(getHaveFan()).append(", ");
        sb.append("HaveTelephone: ").append(getHaveTelephone()).append(", ");
        sb.append("HaveElectricGasStove: ").append(getHaveElectricGasStove()).append(", ");
        sb.append("HavePushTruck: ").append(getHavePushTruck()).append(", ");
        sb.append("HaveTractor: ").append(getHaveTractor()).append(", ");
        sb.append("HaveGrindingMill: ").append(getHaveGrindingMill()).append(", ");
        sb.append("HaveKeroseneStove: ").append(getHaveKeroseneStove()).append(", ");
        sb.append("HavePersonalComputer: ").append(getHavePersonalComputer()).append(", ");
        sb.append("HaveGenerator: ").append(getHaveGenerator()).append(", ");
        sb.append("Land: ").append(getLand()).append(", ");
        sb.append("Tenure: ").append(getTenure()).append(", ");
        sb.append("Waste: ").append(getWaste()).append(", ");
        sb.append("Owland: ").append(getOwland()).append(", ");
        sb.append("Enland: ").append(getEnland()).append(", ");
        sb.append("Enfood: ").append(getEnfood()).append(", ");
        sb.append("Faid: ").append(getFaid()).append(", ");
        sb.append("Mstaple: ").append(getMstaple()).append(", ");
        sb.append("Meals: ").append(getMeals()).append(", ");
        sb.append("Dfood: ").append(getDfood()).append(", ");
        sb.append("Mfood: ").append(getMfood()).append(", ");
        sb.append("Adfood: ").append(getAdfood()).append(", ");
        sb.append("NumFouls: ").append(getNumFouls()).append(", ");
        sb.append("NumSheep: ").append(getNumSheep()).append(", ");
        sb.append("NumDogs: ").append(getNumDogs()).append(", ");
        sb.append("NumGoats: ").append(getNumGoats()).append(", ");
        sb.append("NumPigs: ").append(getNumPigs()).append(", ");
        sb.append("NumRabbits: ").append(getNumRabbits()).append(", ");
        sb.append("OtherAnimals: ").append(getOtherAnimals()).append(", ");
        sb.append("FormsCheckedBy: ").append(getFormsCheckedBy()).append(", ");
        sb.append("FormsReceivedBy: ").append(getFormsReceivedBy()).append(", ");
        sb.append("InterviewTimeEnd: ").append(getInterviewTimeEnd());
        return sb.toString();
    }
    
}
