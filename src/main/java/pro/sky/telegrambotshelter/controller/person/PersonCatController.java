package pro.sky.telegrambotshelter.controller.person;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.sky.telegrambotshelter.model.person.PersonCat;
import pro.sky.telegrambotshelter.service.person.PersonCatService;

@RestController
@RequestMapping("/person_cat")
public class PersonCatController extends PersonRestController<PersonCat> {

    public PersonCatController(PersonCatService personService) {
        super(personService);
    }
}
