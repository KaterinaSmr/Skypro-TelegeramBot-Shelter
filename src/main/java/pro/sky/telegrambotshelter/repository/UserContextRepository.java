package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambotshelter.model.UserContext;

public interface UserContextRepository extends JpaRepository <UserContext, Integer> {
    UserContext findByChatId(long chatId);
}
