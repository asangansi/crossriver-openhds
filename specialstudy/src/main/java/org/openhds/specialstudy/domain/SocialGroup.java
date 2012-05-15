package org.openhds.specialstudy.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.transaction.annotation.Transactional;
import javax.validation.constraints.NotNull;
import java.lang.Integer;
import java.util.ArrayList;
import java.util.List;

@Entity
@RooJavaBean
@RooToString
@RooEntity
public class SocialGroup {

    @PersistenceContext
    transient EntityManager entityManager;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @NotNull
    private String householdId;
    
    @NotNull
    private String interviewTimeStart;
        
    private String water;

    private String toilet;

    private Integer rooms = 0;

    private Integer srooms = 0;

    private String floor;

    private String roof;

    private String wall;

    private String cfuel;

    private String light;

    private Boolean haveCar;

    private Boolean haveMotorcycle;

    private Boolean haveBicycle;

    private Boolean haveElectricity;

    private Boolean haveSolar;

    private Boolean haveRefrigerator;

    private Boolean haveTelevision;

    private Boolean haveDvdVcrVcd;

    private Boolean haveRadio;

    private Boolean haveSewingMachine;

    private Boolean haveStereo;

    private Boolean haveIron;

    private Boolean haveFan;

    private Boolean haveTelephone;

    private Boolean haveElectricGasStove;

    private Boolean havePushTruck;

    private Boolean haveTractor;

    private Boolean haveGrindingMill;

    private Boolean haveKeroseneStove;

    private Boolean havePersonalComputer;

    private Boolean haveGenerator;

    private String land;

    private String tenure;

    private String waste;

    private String owland;

    private String enland;

    private String enfood;

    private String faid;

    private String mstaple;

    private String meals;

    private Integer dfood = 0;

    private Integer mfood = 0;

    private String adfood;

    private Integer numFouls = 0;

    private Integer numSheep = 0;

    private Integer numDogs = 0;

    private Integer numGoats = 0;

    private Integer numPigs = 0;

    private Integer numRabbits = 0;
    
    private String otherAnimals;
    
    @NotNull
    private String formsCheckedBy;
    
    @NotNull
    private String formsReceivedBy;
    
    @NotNull
    private String interviewTimeEnd;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(String householdId) {
        this.householdId = householdId;
    }

    @Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        SocialGroup merged = this.entityManager.merge(this);
        this.entityManager.flush();
        this.id = merged.getId();
    }
    
    public static SocialGroup findSocialGroup(Long id) {
        if (id == null) return null;
        return entityManager().find(SocialGroup.class, id);
    }
    
    @SuppressWarnings("unchecked")
	public static List<SocialGroup> findSocialGroupEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("select o from SocialGroup o").setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static long countSocialGroups() {
        return (Long) entityManager().createQuery("select count(o) from SocialGroup o").getSingleResult();
    }
    
    @SuppressWarnings("unchecked")
	public static List<SocialGroup> findAllSocialGroups() {
        return entityManager().createQuery("select o from SocialGroup o").getResultList();
    }
    
	public static List<SocialGroup> findAllSocialGroupsByHouseholdId(String id) {
		List<SocialGroup> list = findAllSocialGroups();
		List<SocialGroup> filteredList = new ArrayList<SocialGroup>();
		
		for (SocialGroup item : list) {
			if (item.getHouseholdId().equals(id.toUpperCase())) 
				filteredList.add(item);
		}
		return filteredList;
    }
	
    @Transactional
    public void remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            SocialGroup attached = this.entityManager.find(SocialGroup.class, this.id);
            this.entityManager.remove(attached);
        }
    }

    public static final EntityManager entityManager() {
        EntityManager em = new SocialGroup().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
}
