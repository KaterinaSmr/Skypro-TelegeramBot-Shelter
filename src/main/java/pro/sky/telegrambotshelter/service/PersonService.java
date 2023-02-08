package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.Person;
import pro.sky.telegrambotshelter.repository.PersonRepository;

import java.util.Collection;
import java.util.Optional;

/**
 * A Service class to perform CRUD operations with the "person" table in database.
 * @author Ekaterina Gorbacheva
 */
@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Finds a {@link Person} object record in the "person" with the chatId specified.
     * Uses {@link PersonRepository}.
     * @param chatId telegram chat identification for a {@link Person} object
     * @return {@link Optional<Person>} of the search result
     */
    public Optional<Person> findPersonByChatId(long chatId){
        return personRepository.findByChatId(chatId);
    }

    /**
     * Saves a new {@link Person} object to a "person" table. Uses {@link PersonRepository}.
     * The  field in the new {@link Person} object should be unique. If the "person" table already contains a record
     * with the same chat_id value, then the new record is not saved, and this method returns {@code null}.
     * @param person new {@link Person} object to be saved
     * @return {@link Person} object just saved, or {@code null}. in case of duplicated chat_id
     */
    public Person save(Person person) {
        if (findPersonByChatId(person.getChatId()).isPresent()){
            return null;
        }
        return personRepository.save(person);
    }

    /**
     * Returns {@link Person} object from db table "person" by person ID. Uses {@link PersonRepository}.
     * @param personId identification of a person to be found
     * @return Person object with the id specified, or {@code null}. if not found
     */
    public Person get(Integer personId) {
        return personRepository.findById(personId).orElse(null);
    }

    /**
     * A method to get all records from the db table "person". Uses {@link PersonRepository}
     * @return {@link Collection} of all {@link Person} objects saved in db table "person"
     */
    public Collection<Person> getAll() {
        return personRepository.findAll();
    }

    /**
     * A method for saving updates of a {@link Person} object to the db table "person".
     * Uses {@link PersonRepository}
     * @param person {@link Person} object with updates to be saved
     * @return {@link Person} object with updated fields, or {@code null} if a person with the id specified is not found.
     * @throws org.hibernate.exception.ConstraintViolationException in case of duplicated chatId
     */
    public Person edit(Person person) {
        Person personFound = get(person.getId());
        if (personFound == null){
            return null;
        }
        return personRepository.save(person);
    }

    /**
     * Removes a record from the "person" table with this id. Uses {@link PersonRepository}
     * @param id identification of a {@link Person} to be removed from db table "person"
     */
    public void delete(Integer id) {
        personRepository.deleteById(id);
    }
}
