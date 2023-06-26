package pro.sky.telegrambotshelter.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pro.sky.telegrambotshelter.controller.adoptionReport.AdoptionReportCatController;
import pro.sky.telegrambotshelter.controller.adoptionReport.AdoptionReportDogController;
import pro.sky.telegrambotshelter.model.*;
import pro.sky.telegrambotshelter.model.adoption.AdoptionDog;
import pro.sky.telegrambotshelter.model.adoption.AdoptionStatus;
import pro.sky.telegrambotshelter.model.adoptionReport.AdoptionReportDog;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class AdoptionReportControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetRepository petRepository;
    @MockBean
    private PersonDogRepository personDogRepository;
    @MockBean
    private PersonCatRepository personCatRepository;
    @MockBean
    private AdoptionDogRepository adoptionRepository;
    @MockBean
    private AdoptionCatRepository adoptionCatRepository;
    @MockBean
    private AdoptionReportDogRepository adoptionReportRepository;
    @MockBean
    private AdoptionReportCatRepository adoptionReportCatRepository;
    @MockBean
    private UserContextRepository userContextRepository;

    @SpyBean
    private PetService petService;
    @SpyBean
    private PersonDogService personService;
    @SpyBean
    private PersonCatService personCatService;
    @SpyBean
    private AdoptionDogService adoptionService;
    @SpyBean
    private AdoptionCatService adoptionCatService;
    @SpyBean
    private AdoptionReportDogService adoptionReportService;
    @SpyBean
    private AdoptionReportCatService adoptionReportCatService;
    @SpyBean
    private UserContextService userContextService;

    @InjectMocks
    private AdoptionReportDogController adoptionReportController;
    @InjectMocks
    private AdoptionReportCatController adoptionReportCatController;

    private String url = "/adoption_report_dog";

    private int id;
    private AdoptionDog adoption;
    private String filePath;
    private String mediaType;
    private LocalDate reportDate;
    private AdoptionReportDog adoptionReport;

    @BeforeEach
    public void setup() {
        id = 1;
        PersonDog person = new PersonDog(444666555L, "Ivan", "Ivanov", "+79998887766", "email@gmail.com");
        person.setId(1);
        Pet pet = new Pet("Kompot", PetType.DOG, 2020);
        pet.setId(1);
        LocalDate probationStartDate = LocalDate.now().minusDays(10);
        LocalDate probationEndDate = LocalDate.now().plusDays(20);
        AdoptionStatus adoptionStatus = AdoptionStatus.ON_PROBATION;
        adoption = new AdoptionDog(person, pet, probationStartDate, probationEndDate, adoptionStatus);
        adoption.setId(id);
        reportDate = LocalDate.now();
        filePath = "/reports/" + reportDate + "/" + adoption.getId();
        mediaType = MediaType.TEXT_PLAIN_VALUE;
        adoptionReport = new AdoptionReportDog(adoption, filePath, mediaType, reportDate);
    }

    @Test
    public void allReportsTest() throws Exception {
        when(adoptionReportRepository.findAll()).thenReturn(new ArrayList<>(List.of(adoptionReport)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(url)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void getReportsByDateTest() throws Exception {
        when(adoptionReportRepository.findAllByReportDate(any(LocalDate.class))).thenReturn(new ArrayList<>(List.of(adoptionReport)));
        mockMvc.perform(MockMvcRequestBuilders
                        .get(url + "?reportDate=" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void getReportsByDateBetweenTest() throws Exception {
        when(adoptionReportRepository.findAllByReportDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>(List.of(adoptionReport)));
        mockMvc.perform(MockMvcRequestBuilders
                        .get(url + "?fromDate=" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy"))
                        + "&toDate=" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void getReportsByAdoptionIdAndDateTest() throws Exception {
        when(adoptionReportRepository.findAllByAdoptionAndReportDate(any(AdoptionDog.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>(List.of(adoptionReport)));
        when(adoptionRepository.findById(anyInt())).thenReturn(Optional.of(adoption));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(url+"?adoptionId=" + 1
                                + "&date=" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());

        when(adoptionReportRepository.findAllByAdoptionAndReportDate(any(AdoptionDog.class), any(LocalDate.class)))
                .thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders
                        .get(url+"?adoptionId=" + 1
                                + "&date=" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getByIdTest() throws Exception{
        when(adoptionReportRepository.findById(anyInt())).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders
                .get(url + "/" + 1))
                        .andExpect(status().isNotFound());

        adoptionReport.setFilePath("testReports/1.txt");
        when(adoptionReportRepository.findById(anyInt())).thenReturn(Optional.of(adoptionReport));
        mockMvc.perform(MockMvcRequestBuilders
                        .get(url + "/" + 1))
                .andExpect(status().isOk());

    }

	@Test
	public void deleteAdoptionReportTest() throws Exception{
		doNothing().when(adoptionReportRepository).deleteById(anyInt());
        when(adoptionReportRepository.findById(anyInt())).thenReturn(Optional.of(adoptionReport));

		mockMvc.perform(MockMvcRequestBuilders
				.delete(url + "/" + id + "?removeFile=false"))
				.andExpect(status().isOk());

        when(adoptionReportRepository.findById(anyInt())).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(url + "/" + id + "?removeFile=false"))
                .andExpect(status().isNotFound());
	}
}
