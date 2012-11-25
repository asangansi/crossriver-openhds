
package org.openhds.domain.model;

import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.openhds.domain.annotations.Description;
import org.openhds.domain.constraint.CheckCalendar;
import org.openhds.domain.constraint.CheckDeathDateGreaterThanBirthDate;
import org.openhds.domain.constraint.CheckEntityNotVoided;
import org.openhds.domain.constraint.CheckFieldNotBlank;
import org.openhds.domain.constraint.CheckIndividualNotUnknown;
import org.openhds.domain.constraint.ExtensionIntegerConstraint;
import org.openhds.domain.constraint.ExtensionStringConstraint;
import org.openhds.domain.constraint.Searchable;


/**
 * Generated by JCodeModel
 * 
 */
@Description(description = "A Death represents the final event than an Individual can have within the system. It consists of the Individual who has passed on, the Visit associated with the Death, as well as descriptive information about the occurrence, cause, and date of the death. If the Individual had any Residencies, Relationships, or Memberships then they will become closed.")
@CheckDeathDateGreaterThanBirthDate
@Entity
@Table(name = "death")
@XmlRootElement(name = "death")
public class Death
    extends VisitableEntity
    implements Serializable
{

    public final static long serialVersionUID = -6644256636909420061L;
    @Searchable
    @CheckEntityNotVoided
    @CheckIndividualNotUnknown
    @ManyToOne(cascade = {
        CascadeType.MERGE,
        CascadeType.PERSIST
    })
    @Description(description = "Individual who has died, identified by external id.")
    private Individual individual = new Individual();
    @Searchable
    @ManyToOne
    @Description(description = "House in which this Death took place.")
    private Location house;
    @Searchable
    @ManyToOne
    @Description(description = "Household in which this Death took place.")
    private SocialGroup household = new SocialGroup();
    @Searchable
    @Description(description = "Place where the death occurred.")
    @ExtensionIntegerConstraint(constraint = "placeOfDeathConstraint", message = "Invalid Value for deathPlace", allowNull = true)
    private Integer deathPlace;
    @CheckCalendar(message = "Death date is invalid")
    @NotNull(message = "You must provide a Death date")
    @Past(message = "Death date should be in the past")
    @Temporal(TemporalType.DATE)
    @Description(description = "Date of the Death.")
    private Calendar deathDate;
    @Description(description = "Age of death in number of data.")
    private Long ageAtDeath;
    @Description(description = "Who reported this death")
    @ExtensionIntegerConstraint(constraint = "reportedByConstraint", message = "Invalid Value for reportedBy", allowNull = true)
    private Integer reportedBy;
    @Description(description = "Specify the place of death if other")
    private String placeOfDeathOther;
    @Description(description = "Recorded date for the death")
    @CheckCalendar(message = "Invalid value for date")
    @Temporal(TemporalType.DATE)
    @Past
    private Calendar recordedDate;
    @Description(description = "Deceased Name")
    private String deceasedName;
    @ExtensionStringConstraint(constraint = "genderConstraint", message = "Invalid Value for gender", allowNull = true)
    @Description(description = "The gender of the deceased.")
    private String gender;

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual indiv) {
        individual = indiv;
    }

    public Location getHouse() {
        return house;
    }

    public void setHouse(Location place) {
        house = place;
    }

    public SocialGroup getHousehold() {
        return household;
    }

    public void setHousehold(SocialGroup place) {
        household = place;
    }

    public Integer getDeathPlace() {
        return deathPlace;
    }

    public void setDeathPlace(Integer place) {
        deathPlace = place;
    }

    @XmlJavaTypeAdapter(org.openhds.domain.util.CalendarAdapter.class)
    public Calendar getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(Calendar date) {
        deathDate = date;
    }

    public Long getAgeAtDeath() {
        return ageAtDeath;
    }

    public void setAgeAtDeath(Long age) {
        ageAtDeath = age;
    }

    @Override
    public String toString() {
        return "Death";
    }

    public Integer getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(Integer data) {
        reportedBy = data;
    }

    public String getPlaceOfDeathOther() {
        return placeOfDeathOther;
    }

    public void setPlaceOfDeathOther(String data) {
        placeOfDeathOther = data;
    }

    @XmlJavaTypeAdapter(org.openhds.domain.util.CalendarAdapter.class)
    public Calendar getRecordedDate() {
        return recordedDate;
    }

    public void setRecordedDate(Calendar data) {
        recordedDate = data;
    }

    public String getDeceasedName() {
        return deceasedName;
    }

    public void setDeceasedName(String deceasedName) {
        this.deceasedName = deceasedName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

}
