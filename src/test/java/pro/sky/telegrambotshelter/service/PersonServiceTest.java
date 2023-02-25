package pro.sky.telegrambotshelter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambotshelter.model.Person;
import pro.sky.telegrambotshelter.model.PersonDog;
import pro.sky.telegrambotshelter.repository.PersonDogRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {
    @Mock
    private PersonDogRepository personDogRepository;

    @InjectMocks
    private PersonDogService personService;

    private int id;
    private long chatId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private PersonDog person;

    @BeforeEach
    public void setup() {
        id = 1;
        chatId = 444555666;
        firstName = "Ivan";
        lastName = "Ivanov";
        phone = "+79998887766";
        email = "test@gmail.com";
        person = new PersonDog(chatId, firstName, lastName, phone, email);
        person.setId(id);
    }

    @Test
    public void saveTest() {
        when(personDogRepository.findByChatId(person.getChatId())).thenReturn(Optional.empty());
        when(personDogRepository.save(any(PersonDog.class))).thenReturn(person);
        assertEquals(person, personService.save(person));

        long existingChatId = 111222333;
        person.setChatId(existingChatId);
        when(personDogRepository.findByChatId(existingChatId)).thenReturn(Optional.of(person));
        assertNull(personService.save(person));
    }

    @Test
    public void findPersonByChatIdTest() {
        when(personDogRepository.findByChatId(person.getChatId())).thenReturn(Optional.of(person));
        assertEquals(Optional.of(person), personService.findPersonByChatId(person.getChatId()));
    }

    @Test
    public void findByIdTest() {
        when(personDogRepository.findById(id)).thenReturn(Optional.of(person));
        when(personDogRepository.findById(2)).thenReturn(Optional.empty());

        assertEquals(person, personService.findById(id));
        assertNull(personService.findById(2));
    }

    @Test
    public void findAllTest() {
        when(personDogRepository.findAll()).thenReturn(new ArrayList<>(List.of(person)));
        assertEquals(new ArrayList<>(List.of(person)), personService.findAll());
    }

    @Test
    public void editTest() {
        when(personDogRepository.findById(id)).thenReturn(Optional.of(person));
        when(personDogRepository.save(any(PersonDog.class))).thenReturn(person);
        assertEquals(person, personService.edit(person));

        when(personDogRepository.findById(2)).thenReturn(Optional.empty());
        person.setId(2);
        assertNull(personService.edit(person));
    }

    @Test
    public void deleteTest() {
        doNothing().when(personDogRepository).deleteById(anyInt());

        personService.delete(id);
        verify(personDogRepository, only()).deleteById(anyInt());
    }
}
