package pro.sky.telegrambotshelter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pro.sky.telegrambotshelter.model.Person;
import pro.sky.telegrambotshelter.service.PersonService;

import javax.validation.Valid;

@Controller
@RequestMapping("/person")
public class PersonController {
    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping()
    public String addPerson(@RequestParam("chatId") Long chatId,
                            Model model) {
        Person person = new Person(chatId);
        person.setPhone("+7");
        model.addAttribute("person", person);
        return "add_person";
    }

    @PostMapping("/{chatId}")
    public String savePerson(@PathVariable ("chatId") Long chatId,
                             @ModelAttribute ("person") @Valid Person person, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return "add_person";
        }
        personService.save(person);
        return "success";
    }

}
