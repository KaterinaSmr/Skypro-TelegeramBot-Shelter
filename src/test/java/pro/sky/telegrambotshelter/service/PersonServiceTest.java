package pro.sky.telegrambotshelter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambotshelter.model.Person;
import pro.sky.telegrambotshelter.repository.PersonRepository;

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
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    private int id;
    private long chatId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private Person person;

    @BeforeEach
    public void setup() {
        id = 1;
        chatId = 444555666;
        firstName = "Ivan";
        lastName = "Ivanov";
        phone = "+79998887766";
        email = "test@gmail.com";
        person = new Person(chatId, firstName, lastName, phone, email);
        person.setId(id);
    }

    @Test
    public void saveTest() {
        when(personRepository.findByChatId(person.getChatId())).thenReturn(Optional.empty());
        when(personRepository.save(any(Person.class))).thenReturn(person);
        assertEquals(person, personService.save(person));

        long existingChatId = 111222333;
        person.setChatId(existingChatId);
        when(personRepository.findByChatId(existingChatId)).thenReturn(Optional.of(person));
        assertNull(personService.save(person));
    }

    @Test
    public void findPersonByChatIdTest() {
        when(personRepository.findByChatId(person.getChatId())).thenReturn(Optional.of(person));
        assertEquals(Optional.of(person), personService.findPersonByChatId(person.getChatId()));
    }

    @Test
    public void findByIdTest() {
        when(personRepository.findById(id)).thenReturn(Optional.of(person));
        when(personRepository.findById(2)).thenReturn(Optional.empty());

        assertEquals(person, personService.findById(id));
        assertNull(personService.findById(2));
    }

    @Test
    public void findAllTest() {
        when(personRepository.findAll()).thenReturn(new ArrayList<>(List.of(person)));
        assertEquals(new ArrayList<>(List.of(person)), personService.findAll());
    }

    @Test
    public void editTest() {
        when(personRepository.findById(id)).thenReturn(Optional.of(person));
        when(personRepository.save(any(Person.class))).thenReturn(person);
        assertEquals(person, personService.edit(person));

        when(personRepository.findById(2)).thenReturn(Optional.empty());
        person.setId(2);
        assertNull(personService.edit(person));
    }

    @Test
    public void deleteTest() {
        doNothing().when(personRepository).deleteById(anyInt());

        personService.delete(id);
        verify(personRepository, only()).deleteById(anyInt());
    }
}
