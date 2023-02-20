package pro.sky.telegrambotshelter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambotshelter.model.UserContext;
import pro.sky.telegrambotshelter.repository.UserContextRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserContextServiceTest {
    @Mock
    private UserContextRepository userContextRepository;

    @InjectMocks
    private UserContextService userContextService;

    private long chatId;
    private String lastCommand;
    private UserContext userContext;

    @BeforeEach
    public void setup() {
        chatId = 444555666L;
        lastCommand = "/start";
        userContext = new UserContext(chatId, lastCommand);
    }

    @Test
    public void getLastCommandTest() {
        when(userContextRepository.findByChatId(anyLong())).thenReturn(userContext);
        assertEquals(userContext.getLastCommand(), userContextService.getLastCommand(1L));
    }

    @Test
    public void getLastCommandTestShouldReturnNullWhenChatIdNotFound() {
        when(userContextRepository.findByChatId(anyLong())).thenReturn(null);
        assertNull(userContextService.getLastCommand(1L));
    }

    @Test
    public void saveTest() {
        when(userContextRepository.save(any(UserContext.class))).thenReturn(userContext);
        userContextService.save(444555666, "/start");
        verify(userContextRepository, only()).save(any(UserContext.class));
    }

}
