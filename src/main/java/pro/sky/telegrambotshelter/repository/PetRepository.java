package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambotshelter.model.Pet;

public interface PetRepository extends JpaRepository<Pet, Integer> {

}
