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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambotshelter.model.*;
import pro.sky.telegrambotshelter.service.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageProcessorTest {

    @Mock
    private TelegramBot telegramBot;
    @Spy
    private PhotoSize photoSize;
    @Spy
    private File file;
    @Spy
    private GetFileResponse getFileResponse;

    @Mock
    private PersonDogService personDogService;
    @Mock
    private PersonCatService personCatService;
    @Mock
    private PetService petService;
    @Mock
    private AdoptionDogService adoptionDogService;
    @Mock
    private AdoptionCatService adoptionCatService;
    @Mock
    private AdoptionReportDogService adoptionReportDogService;
    @Mock
    private AdoptionReportCatService adoptionReportCatService;
    @Mock
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

        imageProcessor = new ImageProcessor(personDogService, personCatService, adoptionDogService,
                adoptionCatService, adoptionReportDogService, adoptionReportCatService, petService, userContextService);
        imageProcessor.setTelegramBot(telegramBot);
        when(userContextService.getLastCommand(anyLong())).thenReturn("/report");
    }

    @Test
    public void processTestNotFound() throws Exception{
        when(adoptionDogService.findByChatId(anyLong())).thenReturn(null);
        when(adoptionCatService.findByChatId(anyLong())).thenReturn(null);

        when(telegramBot.execute(any())).thenReturn(null);
        imageProcessor.process(userContext.getChatId(), new PhotoSize[]{new PhotoSize()});
        verify(telegramBot).execute(any(SendMessage.class));
    }

    @Test
    public void processTest() throws Exception{
        when(adoptionDogService.findByChatId(anyLong())).thenReturn(adoption);
        when(adoptionReportDogService.save(any())).thenReturn(null);
        when(telegramBot.execute(any())).thenReturn(null);

        //mock telegram responses
        when(telegramBot.execute(any(GetFile.class))).thenReturn(getFileResponse);
        when(getFileResponse.file()).thenReturn(file);
        when(telegramBot.getFullFilePath(any())).thenReturn("http://localhost:8080/testimage");
        when(file.filePath()).thenReturn("test.jpg");

        imageProcessor.process(userContext.getChatId(), new PhotoSize[]{new PhotoSize()});
        verify(telegramBot).execute(any(SendMessage.class));
    }

}
