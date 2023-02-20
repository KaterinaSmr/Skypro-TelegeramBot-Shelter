package pro.sky.telegrambotshelter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.model.PetType;
import pro.sky.telegrambotshelter.repository.PetRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PetServiceTest {
    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetService petService;

    private int id;
    private String name;
    private PetType petType;
    private int yearOfBirth;
    private Pet pet;

    @BeforeEach
    public void setup() {
        id = 1;
        name = "Коржик";
        petType = PetType.CAT;
        yearOfBirth = 2020;
        pet = new Pet(name, petType, yearOfBirth);
        pet.setId(id);
    }

    @Test
    public void saveTest(){
        when(petRepository.save(any(Pet.class))).thenReturn(pet);
        assertEquals(pet,petService.save(pet));
        assertEquals(pet, petService.save(name, petType, yearOfBirth));
    }

    @Test
    public void findByIdTest(){
        when(petRepository.findById(id)).thenReturn(Optional.of(pet));
        when(petRepository.findById(2)).thenReturn(Optional.empty());

        assertEquals(pet, petService.findById(id));
        assertNull(petService.findById(2));
    }

    @Test
    public void findAllTest(){
        when(petRepository.findAll()).thenReturn(new ArrayList<>(List.of(pet)));
        assertEquals(new ArrayList<>(List.of(pet)), petService.findAll());
    }

    @Test
    public void editTest(){
        when(petRepository.findById(id)).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenReturn(pet);
        assertEquals(pet, petService.edit(pet));

        when(petRepository.findById(2)).thenReturn(Optional.empty());
        pet.setId(2);
        assertNull(petService.edit(pet));
    }

    @Test
    public void deleteTest(){
        doNothing().when(petRepository).deleteById(anyInt());

        petService.delete(id);
        verify(petRepository, only()).deleteById(anyInt());
    }
}
