package pro.sky.telegrambotshelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendLocation;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambotshelter.model.PetType;
import pro.sky.telegrambotshelter.model.UserContext;
import pro.sky.telegrambotshelter.repository.*;
import pro.sky.telegrambotshelter.service.*;
import pro.sky.telegrambotshelter.service.adoption.AdoptionCatService;
import pro.sky.telegrambotshelter.service.adoption.AdoptionDogService;
import pro.sky.telegrambotshelter.service.adoptionReport.AdoptionReportCatService;
import pro.sky.telegrambotshelter.service.adoptionReport.AdoptionReportDogService;
import pro.sky.telegrambotshelter.service.person.PersonCatService;
import pro.sky.telegrambotshelter.service.person.PersonDogService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CallbackQueryProcessorTest {

    @Mock
    private PetRepository petRepository;
    @Mock
    private PersonDogRepository personDogRepository;
    @Mock
    private AdoptionDogRepository adoptionRepository;
    @Mock
    private AdoptionReportDogRepository adoptionReportRepository;
    @Mock
    private UserContextRepository userContextRepository;
    @Mock
    private TelegramBot telegramBot;

    @InjectMocks
    private PersonDogService personDogService;
    @InjectMocks
    private PersonCatService personCatService;
    @InjectMocks
    private PetService petService;
    @InjectMocks
    private AdoptionDogService adoptionDogService;
    @InjectMocks
    private AdoptionCatService adoptionCatService;
    @InjectMocks
    private AdoptionReportDogService adoptionReportDogService;
    @InjectMocks
    private AdoptionReportCatService adoptionReportCatService;
    @InjectMocks
    private UserContextService userContextService;
    @InjectMocks
    private CallbackQueryProcessor callbackQueryProcessor;

    private UserContext userContext;

    @BeforeEach
    public void setup() {
        callbackQueryProcessor = new CallbackQueryProcessor(personDogService, personCatService, adoptionDogService,
                adoptionCatService, adoptionReportDogService, adoptionReportCatService, petService, userContextService);
        callbackQueryProcessor.setTelegramBot(telegramBot);
        userContext = new UserContext(444555666L, "/safety");
        when(userContextRepository.save(any(UserContext.class))).thenReturn(userContext);
        when(userContextRepository.findByChatId(anyLong())).thenReturn(userContext);
        when(telegramBot.execute(any(SendMessage.class))).thenReturn(null);
    }

    @Test
    public void processTest(){
        callbackQueryProcessor.process(userContext.getChatId(), userContext.getLastCommand());
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    public void processShelterSelectionTest(){
        userContext.setLastCommand(Processor.CAT);
        callbackQueryProcessor.process(callbackQueryProcessor.volunteerChatId, Processor.CAT);
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));

        userContext.setLastCommand(Processor.DOG);
        callbackQueryProcessor.process(userContext.getChatId(), userContext.getLastCommand());
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    public void callAVolunteerTest(){
        userContext.setLastCommand(Processor.CALL_A_VOLUNTEER);
        callbackQueryProcessor.process(userContext.getChatId(), userContext.getLastCommand());
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    public void sendStartMenuTest(){
        userContext.setLastCommand(Processor.GO_BACK);
        callbackQueryProcessor.process(userContext.getChatId(), userContext.getLastCommand());
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    public void getAPetInfoTest(){
        userContext.setLastCommand(Processor.GET_A_PET_INFO);

        callbackQueryProcessor.process(userContext.getChatId(), userContext.getLastCommand());
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));

    }

    @Test
    public void sendHouseMenuTest(){
        userContext.setLastCommand(Processor.HOUSE_ACCOMMODATION);

        callbackQueryProcessor.process(userContext.getChatId(), userContext.getLastCommand());
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));

    }

    @Test
    public void sendLocationTest(){
        when(telegramBot.execute(any(SendLocation.class))).thenReturn(null);
        userContext.setLastCommand(Processor.TEXT_ADDRESS);
        callbackQueryProcessor.process(userContext.getChatId(), userContext.getLastCommand());
        verify(telegramBot, atLeastOnce()).execute(any(SendLocation.class));

        userContext.setPetType(PetType.CAT);
        callbackQueryProcessor.process(userContext.getChatId(), userContext.getLastCommand());
        verify(telegramBot, atLeastOnce()).execute(any(SendLocation.class));

    }
}
