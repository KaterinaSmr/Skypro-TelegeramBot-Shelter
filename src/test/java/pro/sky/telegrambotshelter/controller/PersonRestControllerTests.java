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
import pro.sky.telegrambotshelter.controller.person.PersonCatController;
import pro.sky.telegrambotshelter.controller.person.PersonDogController;
import pro.sky.telegrambotshelter.model.person.PersonDog;
import pro.sky.telegrambotshelter.repository.*;
import pro.sky.telegrambotshelter.service.*;
import pro.sky.telegrambotshelter.service.adoption.AdoptionCatService;
import pro.sky.telegrambotshelter.service.adoption.AdoptionDogService;
import pro.sky.telegrambotshelter.service.adoptionReport.AdoptionReportCatService;
import pro.sky.telegrambotshelter.service.adoptionReport.AdoptionReportDogService;
import pro.sky.telegrambotshelter.service.person.PersonCatService;
import pro.sky.telegrambotshelter.service.person.PersonDogService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class PersonRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetRepository petRepository;
    @MockBean
    private PersonDogRepository personRepository;
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
    private PersonDogController personController;
	@InjectMocks
	private PersonCatController personCatController;

	private String url = "/person_dog";

	private int id;
	private long chatId;
	private String firstName;
	private String lastName;
	private String phone;
	private String email;
	private PersonDog person;

	@BeforeEach
	public void setup(){
		id = 1;
		chatId = 444555666;
		firstName = "Ivan";
		lastName = "Ivanov";
		phone = "+79998881122";
		email = "email@gmail.com";
		person = new PersonDog(chatId, firstName, lastName, phone, email);
		person.setId(id);
	}

    @Test
    public void getPersonTest() throws Exception {
        when(personRepository.findById(anyInt())).thenReturn(Optional.of(person));
        mockMvc.perform(MockMvcRequestBuilders
                        .get(url + "/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.chatId").value(chatId))
				.andExpect(jsonPath("$.firstName").value(firstName))
				.andExpect(jsonPath("$.lastName").value(lastName))
				.andExpect(jsonPath("$.phone").value(phone))
				.andExpect(jsonPath("$.email").value(email));

		when(personRepository.findById(anyInt())).thenReturn(Optional.empty());
		mockMvc.perform(MockMvcRequestBuilders
						.get(url + "/" + id)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
    }

	@Test
	public  void allPetsTest() throws Exception{
		when(personRepository.findAll()).thenReturn(new ArrayList<>(List.of(person)));

		mockMvc.perform(MockMvcRequestBuilders
					.get(url)
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	public void editPetTest() throws Exception{
		JSONObject personObject = new JSONObject();
		personObject.put("chatID", chatId);
		personObject.put("firstName", firstName);
		personObject.put("lastName", lastName);
		personObject.put("phone", phone);
		personObject.put("email", email);

		when(personRepository.findById(any(Integer.class))).thenReturn(Optional.of(person));
		when(personRepository.save(any(PersonDog.class))).thenReturn(person);

		mockMvc.perform(MockMvcRequestBuilders
						.put(url)
						.content(personObject.toString())
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.chatId").value(chatId))
				.andExpect(jsonPath("$.firstName").value(firstName))
				.andExpect(jsonPath("$.lastName").value(lastName))
				.andExpect(jsonPath("$.phone").value(phone))
				.andExpect(jsonPath("$.email").value(email));

		when(personRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

		mockMvc.perform(MockMvcRequestBuilders
						.put(url)
						.content(personObject.toString())
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	public void deletePetTest() throws Exception{
		doNothing().when(personRepository).deleteById(anyInt());

		mockMvc.perform(MockMvcRequestBuilders
				.delete(url + "/" + id))
				.andExpect(status().isOk());

		verify(personRepository, only()).deleteById(anyInt());
	}


}
