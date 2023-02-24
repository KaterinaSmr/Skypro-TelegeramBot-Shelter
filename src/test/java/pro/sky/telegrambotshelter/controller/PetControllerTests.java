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
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.model.PetType;
import pro.sky.telegrambotshelter.repository.*;
import pro.sky.telegrambotshelter.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class PetControllerTests {

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
    private PetController petController;

    private int id;
    private String name;
    private String petType;
    private int yearOfBirth;
    private Pet pet;

    @BeforeEach
    public void setup() {
        id = 1;
        name = "Matroskin";
        petType = "CAT";
        yearOfBirth = 2015;
        pet = new Pet(name, PetType.valueOf(petType), yearOfBirth);
        pet.setId(id);
    }

    @Test
    public void addPetTest() throws Exception {
        JSONObject petObject = new JSONObject();
        petObject.put("name", name);
        petObject.put("petType", petType);
        petObject.put("yearOfBirth", yearOfBirth);
        Pet pet = new Pet(name, PetType.valueOf(petType), yearOfBirth);

        when(petRepository.save(any(Pet.class))).thenReturn(pet);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/pet")
                        .content(petObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.petType").value(petType))
                .andExpect(jsonPath("$.yearOfBirth").value(yearOfBirth));
    }

    @Test
    public void getPetTest() throws Exception {
        when(petRepository.findById(any(Integer.class))).thenReturn(Optional.of(pet));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/pet/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.petType").value(petType))
                .andExpect(jsonPath("$.yearOfBirth").value(yearOfBirth));
    }

    @Test
    public void allPetsTest() throws Exception {
        when(petRepository.findAll()).thenReturn(new ArrayList<>(List.of(pet)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/pet")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void editPetTest() throws Exception {
        JSONObject petObject = new JSONObject();
        petObject.put("name", name);
        petObject.put("petType", petType);
        petObject.put("yearOfBirth", yearOfBirth);

        when(petRepository.findById(any(Integer.class))).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenReturn(pet);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/pet")
                        .content(petObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.petType").value(petType))
                .andExpect(jsonPath("$.yearOfBirth").value(yearOfBirth));
    }

    @Test
    public void deletePetTest() throws Exception {
        doNothing().when(petRepository).deleteById(anyInt());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/pet/" + id))
                .andExpect(status().isOk());

        verify(petRepository, only()).deleteById(anyInt());
    }


}
