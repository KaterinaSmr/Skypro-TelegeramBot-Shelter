package pro.sky.telegrambotshelter.service.person;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.person.PersonDog;
import pro.sky.telegrambotshelter.repository.PersonDogRepository;

@Service
public class PersonDogService extends PersonService<PersonDog> {

    public PersonDogService(PersonDogRepository personRepository) {
        super(personRepository);
    }
}
