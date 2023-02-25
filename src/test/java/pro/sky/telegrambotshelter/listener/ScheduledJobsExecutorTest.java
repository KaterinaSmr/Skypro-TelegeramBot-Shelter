package pro.sky.telegrambotshelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import pro.sky.telegrambotshelter.model.*;
import pro.sky.telegrambotshelter.repository.*;
import pro.sky.telegrambotshelter.service.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Disabled
public class ScheduledJobsExecutorTest {

    @Mock
    private PetRepository petRepository;
    @Mock
    private PersonDogRepository personDogRepository;
    @Mock
    private PersonCatRepository personCatRepository;
    @Mock
    private AdoptionDogRepository adoptionDogRepository;
    @Mock
    private AdoptionCatRepository adoptionCatRepository;
    @Mock
    private AdoptionReportDogRepository adoptionReportDogRepository;
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
    private ScheduledJobsExecutor scheduledJobsExecutor;

    private PersonDog personDog;
    private PersonCat personCat;
    private Pet petDog;
    private Pet petCat;
    private LocalDate probationStartDate;
    private LocalDate probationEndDate;
    private AdoptionStatus adoptionStatus;
    private AdoptionDog adoptionDog;
    private AdoptionCat adoptionCat;
    private LocalDate reportDate;
    private String filePath;
    private String mediaType;
    private AdoptionReportDog adoptionReport;
    private AdoptionReportCat adoptionReportCat;
    private UserContext userContext;

    @BeforeEach
    public void setup() {
        personDog = new PersonDog(444666555L, "Ivan", "Ivanov", "+79998887766", "email@gmail.com");
        personDog.setId(1);
        petDog = new Pet("Kompot", PetType.DOG, 2020);
        petDog.setId(1);
        probationStartDate = LocalDate.now().minusDays(10);
        probationEndDate = LocalDate.now().plusDays(20);
        adoptionStatus = AdoptionStatus.ON_PROBATION;
        adoptionDog = new AdoptionDog(personDog, petDog, probationStartDate, probationEndDate, adoptionStatus);
        adoptionDog.setId(1);
        reportDate = LocalDate.now();
        filePath = "/reports/" + reportDate + "/" + adoptionDog.getId();
        mediaType = MediaType.TEXT_PLAIN_VALUE;
        adoptionReport = new AdoptionReportDog(adoptionDog, filePath, mediaType, reportDate);
        userContext = new UserContext(personDog.getChatId(), "/start", PetType.DOG);

        personCat = new PersonCat(444666555L, "Ivan", "Ivanov", "+79998887766", "email@gmail.com");
        personCat.setId(1);
        petCat = new Pet("Kompot", PetType.CAT, 2020);
        petCat.setId(1);
        adoptionCat = new AdoptionCat(personCat, petCat, probationStartDate, probationEndDate, adoptionStatus);
        adoptionCat.setId(1);
        reportDate = LocalDate.now();
        adoptionReportCat = new AdoptionReportCat(adoptionCat, filePath, mediaType, reportDate);
        userContext = new UserContext(personCat.getChatId(), "/start", PetType.CAT);

        adoptionDogService = new AdoptionDogService(adoptionDogRepository, personDogService, petService);
        adoptionReportDogService = new AdoptionReportDogService(adoptionReportDogRepository, adoptionDogService);
        adoptionCatService = new AdoptionCatService(adoptionCatRepository, personCatService, petService);
        adoptionReportCatService = new AdoptionReportCatService(adoptionReportCatRepository, adoptionCatService);
        scheduledJobsExecutor = new ScheduledJobsExecutor(personDogService, personCatService, adoptionDogService,
                adoptionCatService, adoptionReportDogService, adoptionReportCatService, petService, userContextService);
        scheduledJobsExecutor.setTelegramBot(telegramBot);
    }

    @Test
    public void shouldSendDailyReportReminder(){
        when(adoptionDogRepository.findAllByAdoptionStatusIn(any())).thenReturn(new ArrayList<>(List.of(adoptionDog)));
        when(adoptionReportDogRepository.findAllByAdoptionAndReportDate(any(AdoptionDog.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>());
        when(adoptionCatRepository.findAllByAdoptionStatusIn(any())).thenReturn(new ArrayList<>(List.of(adoptionCat)));
        when(adoptionReportCatRepository.findAllByAdoptionAndReportDate(any(AdoptionCat.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>());

        scheduledJobsExecutor.dailyReportReminder();
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    public void shouldNotSendDailyReportReminder(){
        AdoptionReportDog adoptionReportWithPhoto = new AdoptionReportDog(adoptionDog, filePath, MediaType.IMAGE_JPEG_VALUE, reportDate);
        when(adoptionDogRepository.findAllByAdoptionStatusIn(any())).thenReturn(new ArrayList<>(List.of(adoptionDog)));
        when(adoptionReportDogRepository.findAllByAdoptionAndReportDate(any(AdoptionDog.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<AdoptionReportDog>(List.of(adoptionReport, adoptionReportWithPhoto)));
        AdoptionReportCat adoptionReportWithPhotoCat = new AdoptionReportCat(adoptionCat, filePath, MediaType.IMAGE_JPEG_VALUE, reportDate);
        when(adoptionCatRepository.findAllByAdoptionStatusIn(any())).thenReturn(new ArrayList<>(List.of(adoptionCat)));
        when(adoptionReportCatRepository.findAllByAdoptionAndReportDate(any(AdoptionCat.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<AdoptionReportCat>(List.of(adoptionReportCat, adoptionReportWithPhotoCat)));

        scheduledJobsExecutor.dailyReportReminder();
        verify(telegramBot, never()).execute(any(SendMessage.class));
    }

    @Test
    public void shouldCallVolunteerForInaccurateReports(){
        when(adoptionReportDogRepository.findAllByReportDateBetween(any(LocalDate.class),any(LocalDate.class)))
                .thenReturn(new ArrayList<AdoptionReportDog>());
        when(adoptionDogRepository.findAllByAdoptionStatusIn(any())).thenReturn(new ArrayList<>(List.of(adoptionDog)));
        when(adoptionReportCatRepository.findAllByReportDateBetween(any(LocalDate.class),any(LocalDate.class)))
                .thenReturn(new ArrayList<AdoptionReportCat>());
        when(adoptionCatRepository.findAllByAdoptionStatusIn(any())).thenReturn(new ArrayList<>(List.of(adoptionCat)));

        scheduledJobsExecutor.callVolunteerForInaccurateReports();
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    public void shouldNotCallVolunteerForInaccurateReports(){
        when(adoptionReportDogRepository.findAllByReportDateBetween(any(LocalDate.class),any(LocalDate.class)))
                .thenReturn(new ArrayList<>(List.of(adoptionReport)));
        when(adoptionDogRepository.findAllByAdoptionStatusIn(any())).thenReturn(new ArrayList<>(List.of(adoptionDog)));
        when(adoptionReportCatRepository.findAllByReportDateBetween(any(LocalDate.class),any(LocalDate.class)))
                .thenReturn(new ArrayList<>(List.of(adoptionReportCat)));
        when(adoptionCatRepository.findAllByAdoptionStatusIn(any())).thenReturn(new ArrayList<>(List.of(adoptionCat)));

        scheduledJobsExecutor.callVolunteerForInaccurateReports();
        verify(telegramBot, never()).execute(any(SendMessage.class));
    }

    @Test
    public void shouldSendAdoptionStatusUpdateNotification(){
        when(adoptionDogRepository.findAllByAdoptionStatusIn(any())).thenReturn(new ArrayList<>(List.of(adoptionDog)));
        when(adoptionCatRepository.findAllByAdoptionStatusIn(any())).thenReturn(new ArrayList<>(List.of(adoptionCat)));

        scheduledJobsExecutor.adoptionStatusUpdateNotification();
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    public void shouldNotSendAdoptionStatusUpdateNotification(){
        when(adoptionDogRepository.findAllByAdoptionStatusIn(any())).thenReturn(new ArrayList<>());
        when(adoptionCatRepository.findAllByAdoptionStatusIn(any())).thenReturn(new ArrayList<>());

        scheduledJobsExecutor.adoptionStatusUpdateNotification();
        verify(telegramBot, never()).execute(any(SendMessage.class));
    }

}
