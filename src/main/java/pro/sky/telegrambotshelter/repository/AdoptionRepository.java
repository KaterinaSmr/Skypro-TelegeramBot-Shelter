package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambotshelter.model.Adoption;
import pro.sky.telegrambotshelter.model.Person;

import java.util.List;

public interface AdoptionRepository extends JpaRepository<Adoption, Integer> {

    List<Adoption> findAllByPerson(Person person);
}
