package pro.sky.telegrambotshelter.service;

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
import pro.sky.telegrambotshelter.lilstener.CallbackQueryProcessor;
import pro.sky.telegrambotshelter.lilstener.TextMessageProcessor;
import pro.sky.telegrambotshelter.model.*;
import pro.sky.telegrambotshelter.repository.*;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TextMessageProcessorTest {

    @Mock
    private PetRepository petRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private AdoptionRepository adoptionRepository;
    @Mock
    private AdoptionReportRepository adoptionReportRepository;
    @Mock
    private UserContextRepository userContextRepository;
    @Mock
    private TelegramBot telegramBot;

    @InjectMocks
    private PersonService personService;
    @InjectMocks
    private PetService petService;
    @InjectMocks
    private AdoptionService adoptionService;
    @InjectMocks
    private AdoptionReportService adoptionReportService;
    @InjectMocks
    private UserContextService userContextService;
    @InjectMocks
    private TextMessageProcessor textMessageProcessor;

    private UserContext userContext;

    @BeforeEach
    public void setup() {
        adoptionService = new AdoptionService(adoptionRepository, personService, petService);
        textMessageProcessor = new TextMessageProcessor(personService, adoptionService, petService, adoptionReportService,
                userContextService);
        textMessageProcessor.setTelegramBot(telegramBot);
    }

    @Test
    public void processTest(){
        userContext = new UserContext(444555666L, "/start");
        when(userContextRepository.save(any(UserContext.class))).thenReturn(userContext);
        when(telegramBot.execute(any(SendMessage.class))).thenReturn(null);

        textMessageProcessor.process(userContext.getChatId(), userContext.getLastCommand(), 123);
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Value("${volunteer.chatId}")
    protected long volunteerChatId;

    @Test
    public void processVolunteerRequestTest(){
        UserContext volunteerUserContext = new UserContext(volunteerChatId, "/warning");

        int id = 1;
        Person person = new Person(444666555L, "Ivan", "Ivanov", "+79998887766", "email@gmail.com");
        person.setId(1);
        Pet pet = new Pet("Kompot", PetType.CAT, 2020);
        pet.setId(1);
        LocalDate probationStartDate = LocalDate.now().minusDays(10);
        LocalDate probationEndDate = LocalDate.now().plusDays(20);
        AdoptionStatus adoptionStatus = AdoptionStatus.ON_PROBATION;
        Adoption adoption = new Adoption(person, pet, probationStartDate, probationEndDate, adoptionStatus);
        adoption.setId(id);

        when(userContextRepository.findByChatId(anyLong())).thenReturn(volunteerUserContext);
        when(adoptionRepository.findById(anyInt())).thenReturn(Optional.of(adoption));

        textMessageProcessor.process(volunteerChatId, "1", 1);
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    public void processUnknownRequest(){
        int id = 1;
        long chatId = 444666555L;
        Person person = new Person(chatId, "Ivan", "Ivanov", "+79998887766", "email@gmail.com");
        person.setId(1);
        Pet pet = new Pet("Kompot", PetType.CAT, 2020);
        pet.setId(1);
        LocalDate probationStartDate = LocalDate.now().minusDays(10);
        LocalDate probationEndDate = LocalDate.now().plusDays(20);
        AdoptionStatus adoptionStatus = AdoptionStatus.ON_PROBATION;
        Adoption adoption = new Adoption(person, pet, probationStartDate, probationEndDate, adoptionStatus);
        adoption.setId(id);
        userContext = new UserContext(chatId, "/volunteer");

        when(personRepository.findByChatId(anyLong())).thenReturn(Optional.of(person));
        when(adoptionRepository.findByPerson(any(Person.class))).thenReturn(adoption);
        when(userContextRepository.findByChatId(anyLong())).thenReturn(userContext);

        textMessageProcessor.process(chatId,"Help needed", 123);

        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
        verify(telegramBot, atLeastOnce()).execute(any(ForwardMessage.class));
    }

    @Test
    public void sendReportTest(){
        userContext = new UserContext(444555666L, "/report");
        when(userContextRepository.save(any(UserContext.class))).thenReturn(userContext);
        when(telegramBot.execute(any(SendMessage.class))).thenReturn(null);

        textMessageProcessor.process(userContext.getChatId(), userContext.getLastCommand(), 123);
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
    }
}
