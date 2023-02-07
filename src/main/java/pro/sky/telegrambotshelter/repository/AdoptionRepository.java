package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambotshelter.model.Adoption;
import pro.sky.telegrambotshelter.model.Person;
import pro.sky.telegrambotshelter.model.Pet;


public interface AdoptionRepository extends JpaRepository<Adoption, Integer> {

    Adoption findByPerson(Person person);
    Adoption findByPet(Pet pet);


}
