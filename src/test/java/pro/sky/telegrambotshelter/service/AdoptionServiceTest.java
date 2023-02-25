package pro.sky.telegrambotshelter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambotshelter.model.*;
import pro.sky.telegrambotshelter.repository.AdoptionDogRepository;
import pro.sky.telegrambotshelter.repository.PersonDogRepository;
import pro.sky.telegrambotshelter.repository.PetRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdoptionServiceTest {
    @Mock
    private AdoptionDogRepository adoptionRepository;
    @Mock
    private PersonDogRepository personDogRepository;
    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PersonDogService personService;
    @InjectMocks
    private PetService petService;

    private int id;
    private PersonDog person;
    private Pet pet;
    private LocalDate probationStartDate;
    private LocalDate probationEndDate;
    private AdoptionStatus adoptionStatus;
    private AdoptionDog adoption;
    private AdoptionDogService adoptionService;

    @BeforeEach
    public void setup() {
        id = 1;
        person = new PersonDog(444555666, "Ivan", "Ivanov", "+79998887766", "test@gmail.com");
        person.setId(id);
        pet = new Pet("Коржик", PetType.CAT, 2020);
        pet.setId(id);
        probationStartDate = LocalDate.now().minusDays(10);
        probationEndDate = LocalDate.now().plusDays(20);
        adoptionStatus = AdoptionStatus.ON_PROBATION;
        adoption = new AdoptionDog(person, pet, probationStartDate, probationEndDate, adoptionStatus);
        adoption.setId(id);

        adoptionService = new AdoptionDogService(adoptionRepository, personService, petService);
    }

    @Test
    public void findByIdTest() {
        when(adoptionRepository.findById(id)).thenReturn(Optional.of(adoption));
        when(adoptionRepository.findById(2)).thenReturn(Optional.empty());

        assertEquals(adoption, adoptionService.findById(id));
        assertNull(adoptionService.findById(2));
    }

    @Test
    public void findByChatIdTest() {
        when(personDogRepository.findByChatId(person.getChatId())).thenReturn(Optional.of(person));
        when(adoptionRepository.findByPerson(any(PersonDog.class))).thenReturn(adoption);
        assertEquals(adoption, adoptionService.findByChatId(person.getChatId()));

        when(personDogRepository.findByChatId(person.getChatId())).thenReturn(Optional.empty());
        assertNull(adoptionService.findByChatId(person.getChatId()));
    }

    @Test
    public void findByPetIdTest() {
        when(petRepository.findById(anyInt())).thenReturn(Optional.of(pet));
        when(adoptionRepository.findByPet(any(Pet.class))).thenReturn(adoption);
        assertEquals(adoption, adoptionService.findByPetId(pet.getId()));

        when(petRepository.findById(pet.getId())).thenReturn(Optional.empty());
        assertNull(adoptionService.findByPetId(pet.getId()));
    }

    @Test
    public void findByPersonIdTest() {
        when(personDogRepository.findById(anyInt())).thenReturn(Optional.of(person));
        when(adoptionRepository.findByPerson(any(PersonDog.class))).thenReturn(adoption);
        assertEquals(adoption, adoptionService.findByPersonId(person.getId()));

        when(personDogRepository.findById(person.getId())).thenReturn(Optional.empty());
        assertNull(adoptionService.findByPersonId(person.getId()));
    }

    @Test
    public void saveTest() {
        when(personDogRepository.findById(person.getId())).thenReturn(Optional.empty());
        when(petRepository.findById(pet.getId())).thenReturn(Optional.empty());
        when(adoptionRepository.save(any(AdoptionDog.class))).thenReturn(adoption);
        assertEquals(adoption, adoptionService.save(adoption));

        person.setId(2);
        when(personDogRepository.findById(person.getId())).thenReturn(Optional.of(person));
        when(adoptionRepository.findByPerson(any(PersonDog.class))).thenReturn(adoption);
        assertNull(adoptionService.save(adoption));

        when(personDogRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(petRepository.findById(pet.getId())).thenReturn(Optional.of(pet));
        when(adoptionRepository.findByPet(any(Pet.class))).thenReturn(adoption);
        assertNull(adoptionService.save(adoption));
    }

    @Test
    public void editTest() {
        when(adoptionRepository.findById(id)).thenReturn(Optional.of(adoption));
        when(adoptionRepository.save(any(AdoptionDog.class))).thenReturn(adoption);
        assertEquals(adoption, adoptionService.edit(adoption));

        when(adoptionRepository.findById(2)).thenReturn(Optional.empty());
        adoption.setId(2);
        assertNull(adoptionService.edit(adoption));
    }

    @Test
    public void deleteTest() {
        doNothing().when(adoptionRepository).deleteById(anyInt());
        adoptionService.delete(id);
        verify(adoptionRepository, only()).deleteById(anyInt());
    }

    @Test
    public void setNewStatusTest() {
        when(adoptionRepository.save(any(AdoptionDog.class))).thenReturn(adoption);
        when(adoptionRepository.findById(id)).thenReturn(Optional.of(adoption));
        assertEquals(adoption, adoptionService.setNewStatus(adoption, AdoptionStatus.ADOPTION_CONFIRMED));
        assertEquals(adoption, adoptionService.setNewStatus(adoption.getId(), AdoptionStatus.ON_PROBATION));

        adoption.setId(2);
        when(adoptionRepository.findById(adoption.getId())).thenReturn(Optional.empty());
        assertNull(adoptionService.setNewStatus(adoption.getId(), AdoptionStatus.ADOPTION_REFUSED));
    }

    @Test
    public void setNewProbationEndDateTest() {
        when(adoptionRepository.save(any(AdoptionDog.class))).thenReturn(adoption);
        when(adoptionRepository.findById(id)).thenReturn(Optional.of(adoption));
        assertEquals(adoption, adoptionService.setNewProbationEndDate(adoption, LocalDate.now().plusDays(30)));
        assertEquals(adoption, adoptionService.setNewProbationEndDate(adoption.getId(), "01042023"));

        adoption.setId(2);
        when(adoptionRepository.findById(adoption.getId())).thenReturn(Optional.empty());
        assertNull(adoptionService.setNewProbationEndDate(adoption.getId(), "01042023"));
    }

    @Test
    public void getAllAdoptionsTes() {
        when(adoptionRepository.findAll()).thenReturn(new ArrayList<>(List.of(adoption)));
        assertEquals(new ArrayList<>(List.of(adoption)), adoptionService.getAllAdoptions());
    }

    @Test
    public void getAllAdoptionsByStatusTest() {
        when(adoptionRepository.findAllByAdoptionStatusIn(any()))
                .thenReturn(new ArrayList<>(List.of(adoption)));
        assertEquals(new ArrayList<>(List.of(adoption)), adoptionService.getAllAdoptionsByStatus(
                AdoptionStatus.ON_PROBATION, AdoptionStatus.PROBATION_EXTENDED
        ));
    }

    @Test
    public void getAllActiveProbationsTest() {
        when(adoptionRepository.findAllByAdoptionStatusIn(any()))
                .thenReturn(new ArrayList<>(List.of(adoption)));
        assertEquals(new ArrayList<>(List.of(adoption)), adoptionService.getAllActiveProbations());
    }
}
