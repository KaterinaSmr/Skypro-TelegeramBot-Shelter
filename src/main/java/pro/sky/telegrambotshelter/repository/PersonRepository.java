package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import pro.sky.telegrambotshelter.model.person.Person;

import java.util.Optional;

@NoRepositoryBean
public interface PersonRepository <T extends Person> extends JpaRepository<T, Integer> {

    Optional<T> findByChatId(long chatId);
}
