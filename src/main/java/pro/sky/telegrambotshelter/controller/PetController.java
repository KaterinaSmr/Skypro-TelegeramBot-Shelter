package pro.sky.telegrambotshelter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.service.PetService;

import java.util.Collection;

@RestController
@RequestMapping("/pet")
public class PetController {
    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public Collection<Pet> allPets(){
        return petService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> getPet(@PathVariable Integer id){
        Pet pet = petService.get(id);
        if (pet == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pet);
    }

    @PostMapping
    public Pet addPet(@RequestBody Pet pet){
        return petService.save(pet);
    }

    @PutMapping()
    public ResponseEntity<Pet> editPet(@RequestBody Pet pet){
        Pet petFound = petService.edit(pet);
        if (petFound == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(petFound);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Integer id){
        petService.delete(id);
        return ResponseEntity.ok().build();
    }


}
