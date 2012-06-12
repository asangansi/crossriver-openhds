package org.openhds.domain.model;

import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.Past;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlRootElement;
import org.openhds.domain.annotations.Description;
import org.openhds.domain.constraint.CheckEntityNotVoided;
import org.openhds.domain.constraint.CheckIndividualGenderFemale;
import org.openhds.domain.constraint.CheckIndividualGenderMale;
import org.openhds.domain.constraint.CheckIndividualNotUnknown;
import org.openhds.domain.constraint.CheckInteger;
import org.openhds.domain.constraint.ExtensionIntegerConstraint;
import org.openhds.domain.constraint.Searchable;
import org.openhds.domain.util.CalendarAdapter;

@Description(description="A Pregnancy Outcome represents the results of the " +
		"pregnancy. It contains information about the Visit that is associated, " +
		"the date of the outcome, and who the parents are. One Pregnancy Outcome " +
		"can have multiple Outcomes. ")
@Entity
@Table(name="pregnancyoutcome")
@XmlRootElement(name = "pregnancyoutcome")
public class PregnancyOutcome extends AuditableCollectedEntity implements Serializable {

    private static final long serialVersionUID = -8901037436653805795L;
        
    @ManyToOne
    @Description(description="Visit that is associated with the pregnancy outcome, identified by the external id.")
    private Visit visit;
   
    @CheckInteger
    @Description(description="Total number of children born from recent pregnancy.")
    private Integer totalChildrenBorn = 0;
   
    @CheckInteger
    @Description(description="Total number of live births.")
    private Integer numberOfLiveBirths = 0;
   
    @Temporal(javax.persistence.TemporalType.DATE)
    @Past
    @Description(description="Date of the pregnancy outcome.")
    private Calendar recordedDate;
   
    @Searchable
    @ManyToOne
    @CheckIndividualGenderFemale(allowNull = false)
    @CheckIndividualNotUnknown
    @CheckEntityNotVoided
    @Description(description="Mother of the pregnancy outcome.")
    private Individual mother;
    
    @Searchable
    @ManyToOne
    @CheckIndividualGenderMale(allowNull = false)
    @CheckEntityNotVoided
    @Description(description="Father of the pregnancy outcome.")
    private Individual father;
    
    @Searchable
    @ManyToOne
    @Description(description="House in which this Pregnancy Outcome took place.")
    private Location house;
    
	@Searchable
    @ManyToOne
    @Description(description="Household in which this Pregnancy Outcome took place.")
    private SocialGroup household;
   
    @Temporal(javax.persistence.TemporalType.DATE)
    @Past
    @Description(description="Date of birth for the child.")
    private Calendar dobChild;
    
    @Searchable
    @ManyToOne
    @CheckEntityNotVoided
    @Description(description="Child one of the pregnancy outcome.")
    private Individual child1;
    
    @Searchable
    @ManyToOne
    @Description(description="Child two of the pregnancy outcome, may be null.")
    private Individual child2;
    
    @Description(description="Name of the mother.")
    private String nameOfMother;
    @Description(description="Name of the father.")
    private String nameOfFather;
    @Description(description="Mother's line number.")
    private String motherLineNumber;
    @Description(description="Name of the household in which the pregnancy outcome occurred.")
    private String householdName;
    @ExtensionIntegerConstraint(constraint = "reportedByConstraint", message = "Invalid Value for reportedBy", allowNull = true)
    @Description(description="Who reported this pregnancy outcome.")
    private Integer reportedBy;
    @ExtensionIntegerConstraint(constraint = "placeOfBirthConstraint", message = "Invalid Value for placeOfBirth", allowNull = true)
    @Description(description="Where the birth took place.")
    private Integer placeOfBirth;
    @Description(description="If other is selected, specify.")
    private String placeOfBirthOther;
    @ExtensionIntegerConstraint(constraint = "umbilicalCordConstraint", message = "Invalid Value for umbilicalCord", allowNull = true)
    @Description(description="What was used in cutting the umbilical cord.")
    private Integer umbilicalCord;
    @ExtensionIntegerConstraint(constraint = "umbilicalCordCutConstraint", message = "Invalid Value for umbilicalCordCut", allowNull = true)
    @Description(description="What was applied to the umbilical after it was cut.")
    private Integer umbilicalCordCut;
    @Description(description="If other is selected, specify.")
    private String umbilicalCordCutOther;
	@ExtensionIntegerConstraint(constraint = "yesNoConstraint", message = "Invalid Value for firstLiveBirth", allowNull = true)
    @Description(description="Is this your first live birth.")
    private Integer firstLiveBirth;
    @Description(description="Number of live births from recent pregnancy.")
    private Integer numLiveBirths;

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public Individual getMother() {
        return mother;
    }

    public void setMother(Individual mother) {
        this.mother = mother;
    }

    public Individual getFather() {
        return father;
    }

    public void setFather(Individual father) {
        this.father = father;
    }
    
    public Location getHouse() {
		return house;
	}

	public void setHouse(Location house) {
		this.house = house;
	}

	public SocialGroup getHousehold() {
		return household;
	}

	public void setHousehold(SocialGroup household) {
		this.household = household;
	}

    @XmlJavaTypeAdapter(value=CalendarAdapter.class) 
    public Calendar getRecordedDate() {
        return recordedDate;
    }

    public void setRecordedDate(Calendar recordedDate) {
        this.recordedDate = recordedDate;
    }
    
    @XmlJavaTypeAdapter(value=CalendarAdapter.class) 
    public Calendar getDobChild() {
		return dobChild;
	}

	public void setDobChild(Calendar dobChild) {
		this.dobChild = dobChild;
	}
	
	public Individual getChild1() {
		return child1;
	}

	public void setChild1(Individual child1) {
		this.child1 = child1;
	}

	public Individual getChild2() {
		return child2;
	}

	public void setChild2(Individual child2) {
		this.child2 = child2;
	}
	
	public String getNameOfMother() {
		return nameOfMother;
	}

	public void setNameOfMother(String nameOfMother) {
		this.nameOfMother = nameOfMother;
	}

	public String getNameOfFather() {
		return nameOfFather;
	}

	public void setNameOfFather(String nameOfFather) {
		this.nameOfFather = nameOfFather;
	}

	public String getMotherLineNumber() {
		return motherLineNumber;
	}

	public void setMotherLineNumber(String motherLineNumber) {
		this.motherLineNumber = motherLineNumber;
	}

	public String getHouseholdName() {
		return householdName;
	}

	public void setHouseholdName(String householdName) {
		this.householdName = householdName;
	}

	public Integer getReportedBy() {
		return reportedBy;
	}

	public void setReportedBy(Integer reportedBy) {
		this.reportedBy = reportedBy;
	}

	public Integer getPlaceOfBirth() {
		return placeOfBirth;
	}

	public void setPlaceOfBirth(Integer placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}

	public String getPlaceOfBirthOther() {
		return placeOfBirthOther;
	}

	public void setPlaceOfBirthOther(String placeOfBirthOther) {
		this.placeOfBirthOther = placeOfBirthOther;
	}

	public Integer getUmbilicalCord() {
		return umbilicalCord;
	}

	public void setUmbilicalCord(Integer umbilicalCord) {
		this.umbilicalCord = umbilicalCord;
	}

	public Integer getUmbilicalCordCut() {
		return umbilicalCordCut;
	}

	public void setUmbilicalCordCut(Integer umbilicalCordCut) {
		this.umbilicalCordCut = umbilicalCordCut;
	}
	
	public String getUmbilicalCordCutOther() {
		return umbilicalCordCutOther;
	}

	public void setUmbilicalCordCutOther(String umbilicalCordCutOther) {
		this.umbilicalCordCutOther = umbilicalCordCutOther;
	}

	public Integer getFirstLiveBirth() {
		return firstLiveBirth;
	}

	public void setFirstLiveBirth(Integer firstLiveBirth) {
		this.firstLiveBirth = firstLiveBirth;
	}

	public Integer getNumLiveBirths() {
		return numLiveBirths;
	}

	public void setNumLiveBirths(Integer numLiveBirths) {
		this.numLiveBirths = numLiveBirths;
	}

	public Integer getTotalChildrenBorn() {
        return totalChildrenBorn;
    }

    public void setTotalChildrenBorn(Integer totalChildrenBorn) {
        this.totalChildrenBorn = totalChildrenBorn;
    }

    public Integer getNumberOfLiveBirths() {
        return numberOfLiveBirths;
    }

    public void setNumberOfLiveBirths(Integer numberOfLiveBirths) {
        this.numberOfLiveBirths = numberOfLiveBirths;
    }
}
