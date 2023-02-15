package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambotshelter.model.Adoption;
import pro.sky.telegrambotshelter.model.Person;
import pro.sky.telegrambotshelter.model.Pet;

import java.util.Collection;


public interface AdoptionRepository extends JpaRepository<Adoption, Integer> {

//    Collection<Adoption> findAllByProbationFinished(boolean probationFinished);
    Adoption findByPerson(Person person);
    Adoption findByPet(Pet pet);


}
