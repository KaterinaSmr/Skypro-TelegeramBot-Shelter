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
import pro.sky.telegrambotshelter.model.*;
import pro.sky.telegrambotshelter.repository.*;
import pro.sky.telegrambotshelter.service.*;

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
    private AdoptionRepository adoptionRepository;
    @MockBean
    private AdoptionReportRepository adoptionReportRepository;
    @MockBean
    private UserContextRepository userContextRepository;

    @SpyBean
    private PetService petService;
    @SpyBean
    private PersonService personService;
    @SpyBean
    private AdoptionService adoptionService;
    @SpyBean
    private AdoptionReportServiceTest adoptionReportService;
    @SpyBean
    private UserContextService userContextService;

    @InjectMocks
    private AdoptionReportController adoptionReportController;

    private int id;
    private Adoption adoption;
    private String filePath;
    private String mediaType;
    private LocalDate reportDate;
    private AdoptionReport adoptionReport;

    @BeforeEach
    public void setup() {
        id = 1;
        Person person = new Person(444666555L, "Ivan", "Ivanov", "+79998887766", "email@gmail.com");
        person.setId(1);
        Pet pet = new Pet("Kompot", PetType.CAT, 2020);
        pet.setId(1);
        LocalDate probationStartDate = LocalDate.now().minusDays(10);
        LocalDate probationEndDate = LocalDate.now().plusDays(20);
        AdoptionStatus adoptionStatus = AdoptionStatus.ON_PROBATION;
        adoption = new Adoption(person, pet, probationStartDate, probationEndDate, adoptionStatus);
        adoption.setId(id);
        reportDate = LocalDate.now();
        filePath = "/reports/" + reportDate + "/" + adoption.getId();
        mediaType = MediaType.TEXT_PLAIN_VALUE;
        adoptionReport = new AdoptionReport(adoption, filePath, mediaType, reportDate);
    }

    @Test
    public void allReportsTest() throws Exception {
        when(adoptionReportRepository.findAll()).thenReturn(new ArrayList<>(List.of(adoptionReport)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/adoption_report")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void getReportsByDateTest() throws Exception {
        when(adoptionReportRepository.findAllByReportDate(any(LocalDate.class))).thenReturn(new ArrayList<>(List.of(adoptionReport)));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/adoption_report?reportDate=" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void getReportsByDateBetweenTest() throws Exception {
        when(adoptionReportRepository.findAllByReportDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>(List.of(adoptionReport)));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/adoption_report?fromDate=" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy"))
                        + "&toDate=" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void getReportsByAdoptionIdAndDateTest() throws Exception {
        when(adoptionReportRepository.findAllByAdoptionAndReportDate(any(Adoption.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>(List.of(adoptionReport)));
        when(adoptionRepository.findById(anyInt())).thenReturn(Optional.of(adoption));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/adoption_report?adoptionId=" + 1
                                + "&date=" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

	@Test
	public void deleteAdoptionReportTest() throws Exception{
		doNothing().when(adoptionReportRepository).deleteById(anyInt());
        when(adoptionReportRepository.findById(anyInt())).thenReturn(Optional.of(adoptionReport));

		mockMvc.perform(MockMvcRequestBuilders
				.delete("/adoption_report/" + id + "?removeFile=false"))
				.andExpect(status().isOk());

	}
}
