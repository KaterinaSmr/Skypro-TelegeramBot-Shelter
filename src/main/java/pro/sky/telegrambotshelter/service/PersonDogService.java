package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.PersonDog;
import pro.sky.telegrambotshelter.repository.PersonDogRepository;

@Service
public class PersonDogService extends PersonService<PersonDog> {

    public PersonDogService(PersonDogRepository personRepository) {
        super(personRepository);
    }
}
