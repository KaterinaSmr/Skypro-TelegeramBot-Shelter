package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.Person;
import pro.sky.telegrambotshelter.repository.PersonRepository;

import java.util.Collection;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public void add(Person newPerson){
        personRepository.save(newPerson);
    }

    public void add(long chatId, String firstName, String lastName, String phone, String email){
        Person newPerson = new Person(chatId, firstName, lastName, phone, email);
        personRepository.save(newPerson);
    }

    public Collection<Person> getPersonByChatId(long chatId){
        return personRepository.findAllByChatId(chatId);
    }

    public void remove(int personId){
        personRepository.deleteById(personId);
    }
}
