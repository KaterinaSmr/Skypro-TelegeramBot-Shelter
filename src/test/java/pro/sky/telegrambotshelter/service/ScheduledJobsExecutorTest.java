package pro.sky.telegrambotshelter.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import pro.sky.telegrambotshelter.lilstener.ScheduledJobsExecutor;
import pro.sky.telegrambotshelter.model.*;
import pro.sky.telegrambotshelter.repository.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduledJobsExecutorTest {

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
    private ScheduledJobsExecutor scheduledJobsExecutor;

    private Person person;
    private Pet pet;
    private LocalDate probationStartDate;
    private LocalDate probationEndDate;
    private AdoptionStatus adoptionStatus;
    private Adoption adoption;
    private LocalDate reportDate;
    private String filePath;
    private String mediaType;
    private AdoptionReport adoptionReport;
    private UserContext userContext;

    @BeforeEach
    public void setup() {
        person = new Person(444666555L, "Ivan", "Ivanov", "+79998887766", "email@gmail.com");
        person.setId(1);
        pet = new Pet("Kompot", PetType.CAT, 2020);
        pet.setId(1);
        probationStartDate = LocalDate.now().minusDays(10);
        probationEndDate = LocalDate.now().plusDays(20);
        adoptionStatus = AdoptionStatus.ON_PROBATION;
        adoption = new Adoption(person, pet, probationStartDate, probationEndDate, adoptionStatus);
        adoption.setId(1);
        reportDate = LocalDate.now();
        filePath = "/reports/" + reportDate + "/" + adoption.getId();
        mediaType = MediaType.TEXT_PLAIN_VALUE;
        adoptionReport = new AdoptionReport(adoption, filePath, mediaType, reportDate);
        userContext = new UserContext(person.getChatId(), "/start");

        adoptionService = new AdoptionService(adoptionRepository, personService, petService);
        adoptionReportService = new AdoptionReportService(adoptionReportRepository, adoptionService);
        scheduledJobsExecutor = new ScheduledJobsExecutor(personService, adoptionService, petService, adoptionReportService,
                userContextService);
        scheduledJobsExecutor.setTelegramBot(telegramBot);
    }

    @Test
    public void shouldSendDailyReportReminder(){
        when(adoptionRepository.findAllByAdoptionStatusIn(any())).thenReturn(new ArrayList<>(List.of(adoption)));
        when(adoptionReportRepository.findAllByAdoptionAndReportDate(any(Adoption.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>());

        scheduledJobsExecutor.dailyReportReminder();
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    public void shouldNotSendDailyReportReminder(){
        AdoptionReport adoptionReportWithPhoto = new AdoptionReport(adoption, filePath, MediaType.IMAGE_JPEG_VALUE, reportDate);
        when(adoptionRepository.findAllByAdoptionStatusIn(any())).thenReturn(new ArrayList<>(List.of(adoption)));
        when(adoptionReportRepository.findAllByAdoptionAndReportDate(any(Adoption.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>(List.of(adoptionReport, adoptionReportWithPhoto)));

        scheduledJobsExecutor.dailyReportReminder();
        verify(telegramBot, never()).execute(any(SendMessage.class));
    }

    @Test
    public void shouldCallVolunteerForInaccurateReports(){
        when(adoptionReportRepository.findAllByReportDateBetween(any(LocalDate.class),any(LocalDate.class)))
                .thenReturn(new ArrayList<AdoptionReport>());
        when(adoptionRepository.findAllByAdoptionStatusIn(any())).thenReturn(new ArrayList<>(List.of(adoption)));

        scheduledJobsExecutor.callVolunteerForInaccurateReports();
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    public void shouldNotCallVolunteerForInaccurateReports(){
        when(adoptionReportRepository.findAllByReportDateBetween(any(LocalDate.class),any(LocalDate.class)))
                .thenReturn(new ArrayList<AdoptionReport>(List.of(adoptionReport)));
        when(adoptionRepository.findAllByAdoptionStatusIn(any())).thenReturn(new ArrayList<>(List.of(adoption)));

        scheduledJobsExecutor.callVolunteerForInaccurateReports();
        verify(telegramBot, never()).execute(any(SendMessage.class));
    }

    @Test
    public void shouldSendAdoptionStatusUpdateNotification(){
        when(adoptionRepository.findAllByAdoptionStatusIn(any())).thenReturn(new ArrayList<>(List.of(adoption)));

        scheduledJobsExecutor.adoptionStatusUpdateNotification();
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    public void shouldNotSendAdoptionStatusUpdateNotification(){
        when(adoptionRepository.findAllByAdoptionStatusIn(any())).thenReturn(new ArrayList<Adoption>());

        scheduledJobsExecutor.adoptionStatusUpdateNotification();
        verify(telegramBot, never()).execute(any(SendMessage.class));
    }

}
