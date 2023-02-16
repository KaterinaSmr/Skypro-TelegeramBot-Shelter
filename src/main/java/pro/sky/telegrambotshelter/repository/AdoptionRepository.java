package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambotshelter.model.Adoption;
import pro.sky.telegrambotshelter.model.AdoptionStatus;
import pro.sky.telegrambotshelter.model.Person;
import pro.sky.telegrambotshelter.model.Pet;

import java.util.Collection;
import java.util.List;


public interface AdoptionRepository extends JpaRepository<Adoption, Integer> {

    List<Adoption> findAllByAdoptionStatus(AdoptionStatus adoptionStatus);

    List<Adoption> findAllByAdoptionStatusIn(AdoptionStatus ... adoptionStatuses);
    Adoption findByPerson(Person person);
    Adoption findByPet(Pet pet);




}
