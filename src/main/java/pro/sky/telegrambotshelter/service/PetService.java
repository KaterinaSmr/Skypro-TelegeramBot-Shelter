package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.Person;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.model.PetType;
import pro.sky.telegrambotshelter.repository.PersonRepository;
import pro.sky.telegrambotshelter.repository.PetRepository;

import java.util.Collection;

/**
 * A Service class to perform CRUD operations with the "pet" table in database.
 * @author Ekaterina Gorbacheva
 */
@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Pet save(String name, PetType petType, int year){
        Pet newPet = new Pet(name, petType, year);
        return save(newPet);
    }

    public Pet save(Pet pet){
        return petRepository.save(pet);
    }

    public Pet findById(int id){
        return petRepository.findById(id).orElse(null);
    }

    public Collection<Pet> findAll(){
        return petRepository.findAll();
    }

    public Pet edit(Pet pet) {
        Pet petFound = findById(pet.getId());
        if (petFound == null){
            return null;
        }
        return petRepository.save(pet);
    }

    /**
     * Removes a record from the "pet" table with this id. Uses {@link PetRepository}
     * @param id identification of a {@link Pet} to be removed from db table "pet"
     */
    public void delete(Integer id) {
        petRepository.deleteById(id);
    }
}

