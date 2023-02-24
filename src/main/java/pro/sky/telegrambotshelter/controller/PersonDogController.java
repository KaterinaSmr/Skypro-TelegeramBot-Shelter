package pro.sky.telegrambotshelter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.sky.telegrambotshelter.model.PersonDog;
import pro.sky.telegrambotshelter.service.PersonDogService;

@RestController
@RequestMapping("/person_dog")
public class PersonDogController extends PersonRestController<PersonDog> {

    public PersonDogController(PersonDogService personService) {
        super(personService);
    }
}
