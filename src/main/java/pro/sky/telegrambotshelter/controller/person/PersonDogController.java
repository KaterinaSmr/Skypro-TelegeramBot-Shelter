package pro.sky.telegrambotshelter.controller.person;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.sky.telegrambotshelter.model.person.PersonDog;
import pro.sky.telegrambotshelter.service.person.PersonDogService;

@RestController
@RequestMapping("/person_dog")
public class PersonDogController extends PersonRestController<PersonDog> {

    public PersonDogController(PersonDogService personService) {
        super(personService);
    }
}
