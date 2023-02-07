package pro.sky.telegrambotshelter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.telegrambotshelter.model.Person;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.service.PersonService;

import java.util.Collection;

@RestController
@RequestMapping("/person")
public class PersonRestController {
    private final PersonService personService;

    public PersonRestController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public Collection<Person> allPeople(){
        return personService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable Integer id){
        Person person = personService.get(id);
        if (person == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(person);
    }

//    @PostMapping
//    public Person addPerson(@RequestBody Person person){
//        return personService.save(person);
//    }

    @PutMapping()
    public ResponseEntity<Person> editPet(@RequestBody Person person){
        Person personFound = personService.edit(person);
        if (personFound == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(personFound);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Integer id){
        personService.delete(id);
        return ResponseEntity.ok().build();
    }
}
