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
    private LocalDate adoptionStartDate;
    private LocalDate probationEndDate;
    private boolean probationFinished;
    private boolean adoptionConfirmed;

    @JsonIgnore
    @OneToMany(mappedBy = "adoption", fetch = FetchType.LAZY)
    private Collection<AdoptionReport> adoptionReports;

    public Adoption() {
    }

    public Adoption(Person person, Pet pet, LocalDate adoptionStartDate, LocalDate probationEndDate) {
        this.person = person;
        this.pet = pet;
        this.adoptionStartDate = adoptionStartDate;
        this.probationEndDate = probationEndDate;
        this.probationFinished = false;
        this.adoptionConfirmed = false;
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

    public LocalDate getAdoptionStartDate() {
        return adoptionStartDate;
    }

    public void setAdoptionStartDate(LocalDate adoptionStartDate) {
        this.adoptionStartDate = adoptionStartDate;
    }

    public LocalDate getProbationEndDate() {
        return probationEndDate;
    }

    public void setProbationEndDate(LocalDate probationEndDate) {
        this.probationEndDate = probationEndDate;
    }

    public boolean isProbationFinished() {
        return probationFinished;
    }

    public void setProbationFinished(boolean probationFinished) {
        this.probationFinished = probationFinished;
    }

    public boolean isAdoptionConfirmed() {
        return adoptionConfirmed;
    }

    public void setAdoptionConfirmed(boolean adoptionConfirmed) {
        this.adoptionConfirmed = adoptionConfirmed;
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
