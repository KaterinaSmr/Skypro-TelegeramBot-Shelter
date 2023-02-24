package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.PersonCat;
import pro.sky.telegrambotshelter.repository.PersonCatRepository;

@Service
public class PersonCatService extends PersonService<PersonCat> {

    public PersonCatService(PersonCatRepository personRepository) {
        super(personRepository);
    }
}
