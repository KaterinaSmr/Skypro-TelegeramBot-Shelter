package pro.sky.telegrambotshelter.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "pet_kind")
    private PetKind petKind;
    private int yearOfBirth;

    public Pet() {
    }

    public Pet(String name, PetKind petKind, int yearOfBirth) {
        this.name = name;
        this.petKind = petKind;
        this.yearOfBirth = yearOfBirth;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PetKind getKind() {
        return petKind;
    }

    public void setKind(PetKind petKind) {
        this.petKind = petKind;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return id == pet.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", kind='" + petKind + '\'' +
                ", yearOfBirth=" + yearOfBirth +
                '}';
    }
}
