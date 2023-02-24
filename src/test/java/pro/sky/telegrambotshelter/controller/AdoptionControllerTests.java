package pro.sky.telegrambotshelter.controller;

import org.json.JSONObject;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class AdoptionControllerTests {

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
    private AdoptionController adoptionController;

    private int id;
    private Person person;
    private Pet pet;
    private LocalDate probationStartDate;
    private LocalDate probationEndDate;
    private AdoptionStatus adoptionStatus;
    private Adoption adoption;

    @BeforeEach
    public void setup() {
        id = 1;
        person = new Person(444666555L, "Ivan", "Ivanov", "+79998887766", "email@gmail.com");
        person.setId(1);
        pet = new Pet("Kompot", PetType.CAT, 2020);
        pet.setId(1);
        probationStartDate = LocalDate.now().minusDays(10);
        probationEndDate = LocalDate.now().plusDays(20);
        adoptionStatus = AdoptionStatus.ON_PROBATION;

        adoption = new Adoption(person, pet, probationStartDate, probationEndDate, adoptionStatus);
        adoption.setId(id);
    }

    @Test
    public void addAdoptionTest() throws Exception {
        JSONObject petObject = new JSONObject();
        petObject.put("id", 1);
        petObject.put("name", pet.getName());
        petObject.put("petType", pet.getPetType());
        petObject.put("yearOfBirth", pet.getYearOfBirth());

        JSONObject personObject = new JSONObject();
        petObject.put("id", "1");
        personObject.put("chatID", person.getChatId());
        personObject.put("firstName", person.getFirstName());
        personObject.put("lastName", person.getLastName());
        personObject.put("phone", person.getPhone());
        personObject.put("email", person.getEmail());

        JSONObject adoptionObject = new JSONObject();
        adoptionObject.put("id", id);
        adoptionObject.put("person", personObject);
        adoptionObject.put("pet", petObject);
        adoptionObject.put("probationStartDate", probationStartDate);
        adoptionObject.put("probationEndDate", probationEndDate);
        adoptionObject.put("adoptionStatus", adoptionStatus);

        when(adoptionRepository.save(any(Adoption.class))).thenReturn(adoption);
        when(personDogRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(petRepository.findById(anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/adoption")
                        .content(adoptionObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.person").isNotEmpty())
                .andExpect(jsonPath("$.pet").isNotEmpty())
                .andExpect(jsonPath("$.probationStartDate").value(probationStartDate.toString()))
                .andExpect(jsonPath("$.probationEndDate").value(probationEndDate.toString()))
                .andExpect(jsonPath("$.adoptionStatus").value(adoptionStatus.toString()));
    }

    @Test
    public void getAdoptionByIdTest() throws Exception {
        when(adoptionRepository.findById(anyInt())).thenReturn(Optional.of(adoption));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/adoption/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.person").isNotEmpty())
                .andExpect(jsonPath("$.pet").isNotEmpty())
                .andExpect(jsonPath("$.probationStartDate").value(probationStartDate.toString()))
                .andExpect(jsonPath("$.probationEndDate").value(probationEndDate.toString()))
                .andExpect(jsonPath("$.adoptionStatus").value(adoptionStatus.toString()));
    }

    @Test
    public void getAdoptionByPetIdTest() throws Exception {
        when(adoptionRepository.findByPet(any(Pet.class))).thenReturn(adoption);
        when(petRepository.findById(anyInt())).thenReturn(Optional.of(pet));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/adoption?petId=" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.person").isNotEmpty())
                .andExpect(jsonPath("$.pet").isNotEmpty())
                .andExpect(jsonPath("$.probationStartDate").value(probationStartDate.toString()))
                .andExpect(jsonPath("$.probationEndDate").value(probationEndDate.toString()))
                .andExpect(jsonPath("$.adoptionStatus").value(adoptionStatus.toString()));
    }

    @Test
    public void getAdoptionByPersonIdTest() throws Exception {
        when(adoptionRepository.findByPerson(any(Person.class))).thenReturn(adoption);
        when(personDogRepository.findById(anyInt())).thenReturn(Optional.of(person));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/adoption?personId=" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.person").isNotEmpty())
                .andExpect(jsonPath("$.pet").isNotEmpty())
                .andExpect(jsonPath("$.probationStartDate").value(probationStartDate.toString()))
                .andExpect(jsonPath("$.probationEndDate").value(probationEndDate.toString()))
                .andExpect(jsonPath("$.adoptionStatus").value(adoptionStatus.toString()));
    }

    @Test
    public void allAdoptionsTest() throws Exception {
        when(adoptionRepository.findAll()).thenReturn(new ArrayList<>(List.of(adoption)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/adoption")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }


    @Test
    public void editAdoptionTest() throws Exception {
        JSONObject petObject = new JSONObject();
        petObject.put("id", 1);
        petObject.put("name", pet.getName());
        petObject.put("petType", pet.getPetType());
        petObject.put("yearOfBirth", pet.getYearOfBirth());

        JSONObject personObject = new JSONObject();
        petObject.put("id", "1");
        personObject.put("chatID", person.getChatId());
        personObject.put("firstName", person.getFirstName());
        personObject.put("lastName", person.getLastName());
        personObject.put("phone", person.getPhone());
        personObject.put("email", person.getEmail());

        JSONObject adoptionObject = new JSONObject();
        adoptionObject.put("id", id);
        adoptionObject.put("person", personObject);
        adoptionObject.put("pet", petObject);
        adoptionObject.put("probationStartDate", probationStartDate);
        adoptionObject.put("probationEndDate", probationEndDate);
        adoptionObject.put("adoptionStatus", adoptionStatus);

        when(adoptionRepository.save(any(Adoption.class))).thenReturn(adoption);
        when(adoptionRepository.findById(anyInt())).thenReturn(Optional.of(adoption));

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/adoption")
                        .content(adoptionObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.person").isNotEmpty())
                .andExpect(jsonPath("$.pet").isNotEmpty())
                .andExpect(jsonPath("$.probationStartDate").value(probationStartDate.toString()))
                .andExpect(jsonPath("$.probationEndDate").value(probationEndDate.toString()))
                .andExpect(jsonPath("$.adoptionStatus").value(adoptionStatus.toString()));
    }

    @Test
    public void deleteAdoptionTest() throws Exception {
        doNothing().when(adoptionRepository).deleteById(anyInt());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/adoption/" + id))
                .andExpect(status().isOk());

        verify(adoptionRepository, only()).deleteById(anyInt());
    }
}
