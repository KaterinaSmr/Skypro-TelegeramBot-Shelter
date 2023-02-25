package pro.sky.telegrambotshelter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pro.sky.telegrambotshelter.model.Person;
import pro.sky.telegrambotshelter.model.PersonCat;
import pro.sky.telegrambotshelter.model.PersonDog;
import pro.sky.telegrambotshelter.service.PersonCatService;
import pro.sky.telegrambotshelter.service.PersonDogService;

import javax.validation.Valid;

/**
 * This controller displays a form for saving contact information to a user.
 * An html page with the form is shown.
 * User may enter his/her personal information and send a request to save it.
 * This controller will save it to the database table "person_cat" and "person_dog
 * This controller performs some checks for whether the information provided is valid or not
 * as it is required by the {@link Person} class
 * @see PersonCatService
 * @see PersonDogService
 * @see PersonDog
 * @see PersonCat
 */
@Controller
@RequestMapping("/newperson")
public class PersonController {
    private final PersonDogService personDogService;
    private final PersonCatService personCatService;

    public PersonController(PersonDogService personDogService, PersonCatService personCatService) {
        this.personDogService = personDogService;
        this.personCatService = personCatService;
    }

    @GetMapping("/{petType}")
    public String addPerson(@PathVariable String petType,
            @RequestParam("chatId") Long chatId, Model model) {
        model.addAttribute("person", new Person(chatId));
        model.addAttribute("petType", petType);
        return "add_person";
    }

    @PostMapping("/DOG/{chatId}")
    public String savePersonDog(@PathVariable ("chatId") Long chatId, @ModelAttribute ("person")
                    @Valid PersonDog person, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) {
            return "add_person";
        }
        if (personDogService.save(person) == null){
            model.addAttribute("message", "Ошибка сохранения, пожалуйста обратитесь к волонтеру.");
        } else {
            model.addAttribute("message", "Спасибо, данные сохранены! Вы можете закрыть страницу.");
        }
        return "end";
    }

    @PostMapping("/CAT/{chatId}")
    public String savePersonCat(@PathVariable ("chatId") Long chatId,
                             @ModelAttribute ("person") @Valid PersonCat person, BindingResult bindingResult,
                             Model model){
        if (bindingResult.hasErrors()) {
            return "add_person";
        }
        if (personCatService.save(person) == null){
            model.addAttribute("message", "Ошибка сохранения, пожалуйста обратитесь к волонтеру.");
        } else {
            model.addAttribute("message", "Спасибо, данные сохранены! Вы можете закрыть страницу.");
        }
        return "end";
    }

}
