package pro.sky.telegrambotshelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import pro.sky.telegrambotshelter.model.*;
import pro.sky.telegrambotshelter.repository.*;
import pro.sky.telegrambotshelter.service.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageProcessorTest {
    @Mock
    private PetRepository petRepository;
    @Mock
    private PersonDogRepository personDogRepository;
    @Mock
    private AdoptionDogRepository adoptionRepository;
    @Mock
    private AdoptionReportDogRepository adoptionReportRepository;
    @Mock
    private PersonCatRepository personCatRepository;
    @Mock
    private AdoptionCatRepository adoptionCatRepository;
    @Mock
    private AdoptionReportCatRepository adoptionReportCatRepository;
    @Mock
    private UserContextRepository userContextRepository;
    @Mock
    private TelegramBot telegramBot;
    @Mock
    private PhotoSize photoSize;
    @Mock
    private com.pengrad.telegrambot.model.File file;
    @Mock
    private GetFileResponse getFileResponse;

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
    private ImageProcessor imageProcessor;

    private UserContext userContext;
    private UserContext volunteerUserContext;
    private long chatId;
    private PersonDog person;
    private PersonCat personCat;
    private Pet pet;
    private LocalDate probationStartDate;
    private LocalDate probationEndDate;
    private AdoptionStatus adoptionStatus;
    private AdoptionDog adoption;
    private AdoptionCat adoptionCat;

    @BeforeEach
    public void setup() {
        int id = 1;
        chatId = 444555666L;
        person = new PersonDog(chatId, "Ivan", "Ivanov", "+79998887766", "test@gmail.com");
        person.setId(id);
        pet = new Pet("Коржик", PetType.DOG, 2020);
        pet.setId(id);
        probationStartDate = LocalDate.now().minusDays(10);
        probationEndDate = LocalDate.now().plusDays(20);
        adoptionStatus = AdoptionStatus.ON_PROBATION;
        adoption = new AdoptionDog(person, pet, probationStartDate, probationEndDate, adoptionStatus);
        adoption.setId(id);

        personCat = new PersonCat(chatId, "Ivan", "Ivanov", "+79998887766", "test@gmail.com");
        personCat.setId(id);
        Pet cat = new Pet("Коржик", PetType.CAT, 2020);
        adoptionCat = new AdoptionCat(personCat, cat, probationStartDate, probationEndDate, adoptionStatus);

        userContext = new UserContext(chatId, "/report", PetType.DOG);

        adoptionDogService = new AdoptionDogService(adoptionRepository, personDogService, petService);
        adoptionCatService = new AdoptionCatService(adoptionCatRepository, personCatService, petService);
        imageProcessor = new ImageProcessor(personDogService, personCatService, adoptionDogService,
                adoptionCatService, adoptionReportDogService, adoptionReportCatService, petService, userContextService);
        imageProcessor.setTelegramBot(telegramBot);
        when(userContextRepository.findByChatId(anyLong())).thenReturn(userContext);
    }

    @Test
    public void processTest() throws Exception{
        when(telegramBot.execute(any())).thenReturn(null);
        when(personDogRepository.findByChatId(anyLong())).thenReturn(Optional.of(person));
        when(adoptionRepository.findByPerson(any(PersonDog.class))).thenReturn(adoption);
        Constructor c = GetFileResponse.class.getDeclaredConstructor();
        c.setAccessible(true);
        GetFileResponse getFileResponse1 = (GetFileResponse) c.newInstance();
        when(telegramBot.execute(any(GetFile.class))).thenReturn(getFileResponse1);

        Field field = getFileResponse1.getClass().getDeclaredField("result");
        field.setAccessible(true);
        File file = new File();
        field.set(getFileResponse1, file);
        when(telegramBot.getFullFilePath(any())).thenReturn("");

        Field filePathField = file.getClass().getDeclaredField("file_path");
        filePathField.setAccessible(true);
        filePathField.set(file, "1.jpg");

        when(adoptionReportRepository.save(any(AdoptionReportDog.class))).thenReturn(null);

        imageProcessor.process(userContext.getChatId(), new PhotoSize[]{new PhotoSize()});
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));

        when(personDogRepository.findByChatId(anyLong())).thenReturn(Optional.empty());
        when(personCatRepository.findByChatId(anyLong())).thenReturn(Optional.of(personCat));
        when(adoptionCatRepository.findByPerson(any(PersonCat.class))).thenReturn(adoptionCat);
        when(adoptionReportCatRepository.save(any(AdoptionReportCat.class))).thenReturn(null);

        imageProcessor.process(userContext.getChatId(), new PhotoSize[]{new PhotoSize()});
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));

        when(personCatRepository.findByChatId(anyLong())).thenReturn(Optional.empty());
        imageProcessor.process(userContext.getChatId(), new PhotoSize[]{new PhotoSize()});
        verify(telegramBot, atLeastOnce()).execute(any(SendMessage.class));

    }
}
