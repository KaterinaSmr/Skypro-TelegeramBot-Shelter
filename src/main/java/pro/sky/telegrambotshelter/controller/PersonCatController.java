package pro.sky.telegrambotshelter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.sky.telegrambotshelter.model.PersonCat;
import pro.sky.telegrambotshelter.service.PersonCatService;

@RestController
@RequestMapping("/person_cat")
public class PersonCatController extends PersonRestController<PersonCat> {

    public PersonCatController(PersonCatService personService) {
        super(personService);
    }
}
