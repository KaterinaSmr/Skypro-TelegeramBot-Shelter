package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import pro.sky.telegrambotshelter.model.adoption.Adoption;
import pro.sky.telegrambotshelter.model.adoption.AdoptionStatus;
import pro.sky.telegrambotshelter.model.person.Person;
import pro.sky.telegrambotshelter.model.Pet;

import java.util.List;

@NoRepositoryBean
public interface AdoptionRepository<S extends Adoption<T>, T extends Person> extends JpaRepository<S, Integer> {

    List<S> findAllByAdoptionStatusIn(AdoptionStatus ... adoptionStatuses);
    S findByPerson(T person);
    S findByPet(Pet pet);




}
