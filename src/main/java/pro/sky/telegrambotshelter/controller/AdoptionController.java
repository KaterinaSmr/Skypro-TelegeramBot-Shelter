package pro.sky.telegrambotshelter.controller;

import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.telegrambotshelter.model.Adoption;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.service.AdoptionService;

import java.util.Collection;

@RestController
@RequestMapping("/adoption")
public class AdoptionController {
    private final AdoptionService adoptionService;

    public AdoptionController(AdoptionService adoptionService) {
        this.adoptionService = adoptionService;
    }

    @GetMapping
    public Collection<Adoption> allAdoptions(){
        return adoptionService.getAllAdoptions();
    }

    @GetMapping(params = "petId")
    public ResponseEntity<Adoption> getByPetId(@RequestParam Integer petId){
        Adoption adoption = adoptionService.getByPetId(petId);
        if (adoption == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(adoption);
    }

    @GetMapping(params = "personId")
    public ResponseEntity<Adoption> getByPersonId(@RequestParam Integer personId){
        Adoption adoption = adoptionService.getByPersonId(personId);
        if (adoption == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(adoption);
    }

    @GetMapping(params = "adoptionId")
    public ResponseEntity<Adoption> getById(@RequestParam Integer adoptionId){
        Adoption adoption = adoptionService.findById(adoptionId);
        if (adoption == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(adoption);
    }

    @PostMapping
    public ResponseEntity<Adoption> addAdoption(@RequestBody Adoption adoption){
        Adoption adoptionSaved = adoptionService.save(adoption);
        if (adoptionSaved == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(adoption);
    }

    @PutMapping()
    public ResponseEntity<Adoption> editPet(@RequestBody Adoption adoption){
        Adoption adoptionFound = adoptionService.edit(adoption);
        if (adoptionFound == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(adoption);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteAdoption(@PathVariable Integer id){
        adoptionService.delete(id);
        return ResponseEntity.ok().build();
    }




}
