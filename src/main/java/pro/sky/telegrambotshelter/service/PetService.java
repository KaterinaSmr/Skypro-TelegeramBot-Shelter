package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.Pet;
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

    public Pet save(String name, String kind, int year){
        Pet newPet = new Pet(name, kind, year);
        return save(newPet);
    }

    public Pet save(Pet pet){
        return petRepository.save(pet);
    }

    public Pet get(int id){
        return petRepository.findById(id).orElse(null);
    }

    public Collection<Pet> getAll(){
        return petRepository.findAll();
    }

    public Pet edit(Pet pet) {
        Pet petFound = get(pet.getId());
        if (petFound == null){
            return null;
        }
        return petRepository.save(pet);
    }

    public void delete(Integer id) {
        petRepository.deleteById(id);
    }
}

