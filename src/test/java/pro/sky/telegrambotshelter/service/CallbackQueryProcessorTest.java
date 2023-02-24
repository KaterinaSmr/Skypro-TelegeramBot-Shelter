package pro.sky.telegrambotshelter.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambotshelter.lilstener.CallbackQueryProcessor;
import pro.sky.telegrambotshelter.model.UserContext;
import pro.sky.telegrambotshelter.repository.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CallbackQueryProcessorTest {

    @Mock
    private PetRepository petRepository;
    @Mock
    private PersonDogRepository personDogRepository;
    @Mock
    private AdoptionRepository adoptionRepository;
    @Mock
    private AdoptionReportRepository adoptionReportRepository;
    @Mock
    private UserContextRepository userContextRepository;
    @Mock
    private TelegramBot telegramBot;

    @InjectMocks
    private PersonDogService personService;
    @InjectMocks
    private PetService petService;
    @InjectMocks
    private AdoptionService adoptionService;
    @InjectMocks
    private AdoptionReportService adoptionReportService;
    @InjectMocks
    private UserContextService userContextService;
    @InjectMocks
    private CallbackQueryProcessor callbackQueryProcessor;

    private UserContext userContext;

    @BeforeEach
    public void setup() {
        callbackQueryProcessor = new CallbackQueryProcessor(personService, adoptionService, petService, adoptionReportService,
                userContextService);
        callbackQueryProcessor.setTelegramBot(telegramBot);
    }

    @Test
    public void processTest(){
        userContext = new UserContext(444555666L, "/safety");
        when(userContextRepository.save(any(UserContext.class))).thenReturn(userContext);
        when(telegramBot.execute(any(SendMessage.class))).thenReturn(null);

        callbackQueryProcessor.process(userContext.getChatId(), userContext.getLastCommand());
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    public void getADogInfoTest(){
        userContext = new UserContext(444555666L, "/getadoginfo");
        when(userContextRepository.save(any(UserContext.class))).thenReturn(userContext);
        when(telegramBot.execute(any(SendMessage.class))).thenReturn(null);

        callbackQueryProcessor.process(userContext.getChatId(), userContext.getLastCommand());
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));

    }

    @Test
    public void sendHouseMenuTest(){
        userContext = new UserContext(444555666L, "/houseaccommodation");
        when(userContextRepository.save(any(UserContext.class))).thenReturn(userContext);
        when(telegramBot.execute(any(SendMessage.class))).thenReturn(null);

        callbackQueryProcessor.process(userContext.getChatId(), userContext.getLastCommand());
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));

    }
}
