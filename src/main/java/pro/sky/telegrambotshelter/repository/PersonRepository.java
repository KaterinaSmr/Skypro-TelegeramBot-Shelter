package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambotshelter.model.Person;

import java.util.Collection;
import java.util.Optional;

public interface PersonRepository extends JpaRepository <Person, Integer> {

    Optional<Person> findByChatId(long chatId);
}
