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
import pro.sky.telegrambotshelter.controller.person.PersonController;
import pro.sky.telegrambotshelter.model.person.PersonCat;
import pro.sky.telegrambotshelter.model.person.PersonDog;
import pro.sky.telegrambotshelter.repository.*;
import pro.sky.telegrambotshelter.service.*;
import pro.sky.telegrambotshelter.service.adoption.AdoptionCatService;
import pro.sky.telegrambotshelter.service.adoption.AdoptionDogService;
import pro.sky.telegrambotshelter.service.adoptionReport.AdoptionReportCatService;
import pro.sky.telegrambotshelter.service.adoptionReport.AdoptionReportDogService;
import pro.sky.telegrambotshelter.service.person.PersonCatService;
import pro.sky.telegrambotshelter.service.person.PersonDogService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class PersonControllerTest {

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
    private PersonController personController;

    private String url = "/newperson";
    private Long chatId;
    private PersonDog personDog;
    private PersonCat personCat;

    @BeforeEach
    public void setup(){
        chatId = 112233L;
        personDog = new PersonDog(chatId, "Ekaterina", "Gorbacheva", "+79998887766", "email@email.com");
        personCat = new PersonCat(chatId, "Ekaterina", "Gorbacheva", "+79998887766", "email@email.com");
    }

    @Test
    public void addPersonTest() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                        .get(url + "/DOG?chatId=" + chatId)
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
    }

    @Test
    public void savePersonDogTest() throws Exception{
        when(personDogRepository.save(any(PersonDog.class))).thenReturn(personDog);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(url + "/DOG/" + chatId)
                        .param("firstName", personDog.getFirstName())
                        .param("lastName", personDog.getLastName())
                        .param("phone", personDog.getPhone())
                        .param("email", personDog.getEmail())
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders
                        .post(url + "/DOG/" + chatId)
                        .param("firstName", personDog.getFirstName())
                        .param("lastName", personDog.getLastName())
                        .param("phone", personDog.getPhone())
                        .param("email", "email")
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk());

        when(personDogRepository.save(any(PersonDog.class))).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders
                        .post(url + "/DOG/" + chatId)
                        .param("firstName", personDog.getFirstName())
                        .param("lastName", personDog.getLastName())
                        .param("phone", personDog.getPhone())
                        .param("email", personDog.getEmail())
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
    }

    @Test
    public void savePersonCatTest() throws Exception{
        when(personCatRepository.save(any(PersonCat.class))).thenReturn(personCat);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(url + "/CAT/" + chatId)
                        .param("firstName", personCat.getFirstName())
                        .param("lastName", personCat.getLastName())
                        .param("phone", personCat.getPhone())
                        .param("email", personCat.getEmail())
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders
                        .post(url + "/CAT/" + chatId)
                        .param("firstName", personCat.getFirstName())
                        .param("lastName", personCat.getLastName())
                        .param("phone", personCat.getPhone())
                        .param("email", "email")
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk());

        when(personCatRepository.save(any(PersonCat.class))).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders
                        .post(url + "/CAT/" + chatId)
                        .param("firstName", personCat.getFirstName())
                        .param("lastName", personCat.getLastName())
                        .param("phone", personCat.getPhone())
                        .param("email", personCat.getEmail())
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
    }

}
