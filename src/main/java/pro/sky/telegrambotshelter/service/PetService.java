package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.repository.PetRepository;

import java.util.Collection;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Pet add(String name, String kind, int year){
        Pet newPet = new Pet(name, kind, year);
        return add(newPet);
    }

    public Pet add(Pet pet){
        return petRepository.save(pet);
    }

    public Pet get(int id){
        return petRepository.findById(id).orElse(null);
    }

    public Collection<Pet> getAll(){
        return petRepository.findAll();
    }


}

