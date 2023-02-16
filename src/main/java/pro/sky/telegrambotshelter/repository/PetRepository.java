package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.model.PetType;

import java.util.Collection;

public interface PetRepository extends JpaRepository<Pet, Integer> {

}
