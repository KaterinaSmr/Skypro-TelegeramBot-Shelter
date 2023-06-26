package pro.sky.telegrambotshelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.ForwardMessage;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import pro.sky.telegrambotshelter.model.*;
import pro.sky.telegrambotshelter.model.adoption.AdoptionCat;
import pro.sky.telegrambotshelter.model.adoption.AdoptionDog;
import pro.sky.telegrambotshelter.model.adoption.AdoptionStatus;
import pro.sky.telegrambotshelter.model.person.PersonCat;
import pro.sky.telegrambotshelter.model.person.PersonDog;
import pro.sky.telegrambotshelter.repository.*;
import pro.sky.telegrambotshelter.service.*;
import pro.sky.telegrambotshelter.service.adoption.AdoptionCatService;
import pro.sky.telegrambotshelter.service.adoption.AdoptionDogService;
import pro.sky.telegrambotshelter.service.adoptionReport.AdoptionReportCatService;
import pro.sky.telegrambotshelter.service.adoptionReport.AdoptionReportDogService;
import pro.sky.telegrambotshelter.service.person.PersonCatService;
import pro.sky.telegrambotshelter.service.person.PersonDogService;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TextMessageProcessorTest {

    @Mock
    private PetRepository petRepository;
    @Mock
    private PersonDogRepository personDogRepository;
    @Mock
    private AdoptionDogRepository adoptionRepository;
    @Mock
    private PersonCatRepository personCatRepository;
    @Mock
    private AdoptionCatRepository adoptionRepositoryCat;
    @Mock
    private AdoptionReportDogRepository adoptionReportRepository;
    @Mock
    private AdoptionReportCatRepository adoptionReportCatRepository;
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
    private TextMessageProcessor textMessageProcessor;

    private UserContext userContext;
    private UserContext volunteerUserContext;
    @Value("${volunteer.chatId}")
    protected long volunteerChatId;

    private long chatId;
    private PersonDog person;
    private PersonCat personCat;
    private AdoptionDog adoption;
    private AdoptionCat adoptionCat;

    @BeforeEach
    public void setup() {
        int id = 1;
        chatId = 444666555L;
        person = new PersonDog(chatId, "Ivan", "Ivanov", "+79998887766", "email@gmail.com");
        person.setId(1);
        personCat = new PersonCat(chatId, "Ivan", "Ivanov", "+79998887766", "email@gmail.com");
        personCat.setId(1);
        Pet pet = new Pet("Kompot", PetType.DOG, 2020);
        pet.setId(1);
        Pet cat = new Pet("Kompot", PetType.CAT, 2020);
        cat.setId(1);
        LocalDate probationStartDate = LocalDate.now().minusDays(10);
        LocalDate probationEndDate = LocalDate.now().plusDays(20);
        AdoptionStatus adoptionStatus = AdoptionStatus.ON_PROBATION;
        adoption = new AdoptionDog(person, pet, probationStartDate, probationEndDate, adoptionStatus);
        adoption.setId(id);
        adoptionCat = new AdoptionCat(personCat, cat, probationStartDate, probationEndDate, adoptionStatus);
        adoptionCat.setId(id);

        userContext = new UserContext(chatId, Processor.START, PetType.DOG);
        volunteerUserContext = new UserContext(volunteerChatId, Processor.SEND_WARNING, PetType.DOG);

        adoptionDogService = new AdoptionDogService(adoptionRepository, personDogService, petService);
        adoptionCatService = new AdoptionCatService(adoptionRepositoryCat, personCatService, petService);
        textMessageProcessor = new TextMessageProcessor(personDogService, personCatService, adoptionDogService,
                adoptionCatService, adoptionReportDogService, adoptionReportCatService, petService, userContextService);
        textMessageProcessor.setTelegramBot(telegramBot);
    }

    @Test
    public void processTest(){
        when(userContextRepository.save(any(UserContext.class))).thenReturn(userContext);
        when(telegramBot.execute(any(SendMessage.class))).thenReturn(null);

        textMessageProcessor.process(userContext.getChatId(), userContext.getLastCommand(), 123);
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));

        textMessageProcessor.process(volunteerUserContext.getChatId(), volunteerUserContext.getLastCommand(), 123);
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    public void processVolunteerRequestTest(){
        when(userContextRepository.findByChatId(anyLong())).thenReturn(volunteerUserContext);
        when(adoptionRepository.findById(anyInt())).thenReturn(Optional.of(adoption));

        textMessageProcessor.process(volunteerChatId, "1", 1);
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));

        int wrongAdoptionId = 0;
        when(adoptionRepository.findById(wrongAdoptionId)).thenReturn(Optional.empty());
        textMessageProcessor.process(volunteerChatId, String.valueOf(wrongAdoptionId), 123);
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));

        volunteerUserContext.setPetType(PetType.CAT);
        when(adoptionRepositoryCat.findById(anyInt())).thenReturn(Optional.of(adoptionCat));
        textMessageProcessor.process(volunteerChatId, "1", 1);
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    public void processUnknownRequest(){
        userContext.setLastCommand(Processor.CALL_A_VOLUNTEER);
        when(personDogRepository.findByChatId(anyLong())).thenReturn(Optional.of(person));
        when(adoptionRepository.findByPerson(any(PersonDog.class))).thenReturn(adoption);
        when(userContextRepository.findByChatId(anyLong())).thenReturn(userContext);

        textMessageProcessor.process(chatId,"Help needed", 123);
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
        verify(telegramBot, atLeastOnce()).execute(any(ForwardMessage.class));

        when(personDogRepository.findByChatId(anyLong())).thenReturn(Optional.empty());
        when(personCatRepository.findByChatId(anyLong())).thenReturn(Optional.of(personCat));
        when(adoptionRepositoryCat.findByPerson(any(PersonCat.class))).thenReturn(adoptionCat);
        userContext.setPetType(PetType.CAT);
        textMessageProcessor.process(chatId,"Help needed", 123);
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
        verify(telegramBot, atLeastOnce()).execute(any(ForwardMessage.class));

        userContext.setLastCommand(Processor.REPORT);
        when(adoptionRepositoryCat.findByPerson(any(PersonCat.class))).thenReturn(null);
        textMessageProcessor.process(chatId,"Some text", 123);
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));

        userContext.setLastCommand(null);
        textMessageProcessor.process(chatId,"Some text", 123);
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));

    }

    @Test
    public void saveTextReportTest(){
        userContext.setLastCommand(Processor.REPORT);
        when(personDogRepository.findByChatId(anyLong())).thenReturn(Optional.of(person));
        when(adoptionRepository.findByPerson(any(PersonDog.class))).thenReturn(adoption);
        when(userContextRepository.findByChatId(anyLong())).thenReturn(userContext);

        textMessageProcessor.process(chatId,"Text report", 123);
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));

        userContext.setPetType(PetType.CAT);
        when(adoptionRepository.findByPerson(any(PersonDog.class))).thenReturn(null);
        when(personCatRepository.findByChatId(anyLong())).thenReturn(Optional.of(personCat));
        when(adoptionRepositoryCat.findByPerson(any(PersonCat.class))).thenReturn(adoptionCat);
        when(userContextRepository.findByChatId(anyLong())).thenReturn(userContext);
        textMessageProcessor.process(chatId,"Text report", 123);
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    public void sendInfoSubmenuTest(){
        userContext.setLastCommand(Processor.INFO);
        when(userContextRepository.save(any(UserContext.class))).thenReturn(userContext);
        when(userContextRepository.findByChatId(anyLong())).thenReturn(userContext);
        when(telegramBot.execute(any(SendMessage.class))).thenReturn(null);

        textMessageProcessor.process(userContext.getChatId(), userContext.getLastCommand(), 123);
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    public void sendReportTest(){
        userContext.setLastCommand(Processor.REPORT);
        when(userContextRepository.save(any(UserContext.class))).thenReturn(userContext);
        when(telegramBot.execute(any(SendMessage.class))).thenReturn(null);

        textMessageProcessor.process(userContext.getChatId(), userContext.getLastCommand(), 123);
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    public void sendGetAPetSubmenuTest(){
        userContext.setLastCommand(Processor.GET_A_PET);
        when(userContextRepository.save(any(UserContext.class))).thenReturn(userContext);
        when(userContextRepository.findByChatId(anyLong())).thenReturn(userContext);
        when(telegramBot.execute(any(SendMessage.class))).thenReturn(null);

        textMessageProcessor.process(userContext.getChatId(), userContext.getLastCommand(), 123);
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    public void callAVolunteerTest(){
        userContext.setLastCommand(Processor.CALL_A_VOLUNTEER);
        when(userContextRepository.save(any(UserContext.class))).thenReturn(userContext);

        textMessageProcessor.process(userContext.getChatId(), userContext.getLastCommand(), 123);
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
    }
}
