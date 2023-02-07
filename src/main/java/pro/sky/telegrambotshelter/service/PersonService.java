package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.Person;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.repository.PersonRepository;

import java.util.Collection;
import java.util.Optional;

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

    public Optional<Person> findPersonByChatId(long chatId){
        return personRepository.findByChatId(chatId);
    }

    public Person save(Person person) {
        if (findPersonByChatId(person.getChatId()).isPresent()){
            System.out.println("Дублированный chatId!!!!!!!!!!!!!!!!!!!!!!!!!!");
            return null;
        }
        return personRepository.save(person);
    }

    public void remove(int personId){
        personRepository.deleteById(personId);
    }

    public Person get(Integer personId) {
        return personRepository.findById(personId).orElse(null);
    }

    public Collection<Person> getAll() {
        return personRepository.findAll();
    }

    public Person edit(Person person) {
        Person personFound = get(person.getId());
        if (personFound == null){
            return null;
        }
        return personRepository.save(person);
    }

    public void delete(Integer id) {
        personRepository.deleteById(id);
    }
}
