package pro.sky.telegrambotshelter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

@Entity
public class Adoption{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne
    private Person person;
    @OneToOne
    private Pet pet;
    private LocalDate probationStartDate;
    private LocalDate probationEndDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "adoption_status")
    private AdoptionStatus adoptionStatus;

    @JsonIgnore
    @OneToMany(mappedBy = "adoption", fetch = FetchType.LAZY)
    private Collection<AdoptionReport> adoptionReports;

    public Adoption() {
    }

    public Adoption(Person person, Pet pet, LocalDate probationStartDate, LocalDate probationEndDate, AdoptionStatus adoptionStatus) {
        this.person = person;
        this.pet = pet;
        this.probationStartDate = probationStartDate;
        this.probationEndDate = probationEndDate;
        this.adoptionStatus = adoptionStatus;
    }

    public int getId() {
        return id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public LocalDate getProbationStartDate() {
        return probationStartDate;
    }

    public void setProbationStartDate(LocalDate adoptionStartDate) {
        this.probationStartDate = adoptionStartDate;
    }

    public LocalDate getProbationEndDate() {
        return probationEndDate;
    }

    public void setProbationEndDate(LocalDate probationEndDate) {
        this.probationEndDate = probationEndDate;
    }

    public AdoptionStatus getAdoptionStatus() {
        return adoptionStatus;
    }

    public void setAdoptionStatus(AdoptionStatus adoptionStatus) {
        this.adoptionStatus = adoptionStatus;
    }

    public void setAdoptionReports(Collection<AdoptionReport> adoptionReports) {
        this.adoptionReports = adoptionReports;
    }

    public Collection<AdoptionReport> getAdoptionReports() {
        return adoptionReports;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Adoption adoption = (Adoption) o;
        return id == adoption.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Adoption{" +
                "id=" + id +
                ", person=" + person +
                ", pet=" + pet +
                '}';
    }
}
