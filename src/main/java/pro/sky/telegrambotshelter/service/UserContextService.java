package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.UserContext;
import pro.sky.telegrambotshelter.repository.UserContextRepository;

/**
 * A Service class to perform CRUD operations with the "user_context" table in database.
 * @author Ekaterina Gorbacheva
 */
@Service
public class UserContextService {

    private final UserContextRepository userContextRepository;

    public UserContextService(UserContextRepository userContextRepository) {
        this.userContextRepository = userContextRepository;
    }

    public String getLastCommand(long chatId){
        UserContext userContext = userContextRepository.findByChatId(chatId);
        if (userContext == null){
            return null;
        }
        return userContext.getLastCommand();
    }

    public void save(long chatId, String lastCommand){
        UserContext userContext = new UserContext(chatId, lastCommand);
        save(userContext);
    }

    public void save(UserContext userContext){
        userContextRepository.save(userContext);
    }
}
