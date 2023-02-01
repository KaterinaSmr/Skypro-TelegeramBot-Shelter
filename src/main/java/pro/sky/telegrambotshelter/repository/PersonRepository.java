package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambotshelter.model.Person;

import java.util.Collection;

public interface PersonRepository extends JpaRepository <Person, Integer> {

    Collection<Person> findAllByChatId(long chatId);
}
