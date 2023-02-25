package pro.sky.telegrambotshelter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import pro.sky.telegrambotshelter.model.*;
import pro.sky.telegrambotshelter.repository.*;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdoptionReportServiceTest {
    @Mock
    private AdoptionDogRepository adoptionRepository;
    @Mock
    private PersonDogRepository personDogRepository;
    @Mock
    private PetRepository petRepository;
    @Mock
    private AdoptionReportDogRepository adoptionReportRepository;

    @InjectMocks
    private PersonDogService personService;
    @InjectMocks
    private PetService petService;
    private AdoptionDogService adoptionService;
    private AdoptionReportDogService adoptionReportService;

    private int id;
    private AdoptionDog adoption;
    private String filePath;
    private String mediaType;
    private LocalDate reportDate;
    private AdoptionReportDog adoptionReport;
    private DateTimeFormatter formatter;

    @BeforeEach
    public void setup() {
        id = 1;
        PersonDog person = new PersonDog(444555666, "Ivan", "Ivanov", "+79998887766", "test@gmail.com");
        person.setId(1);
        Pet pet = new Pet("Коржик", PetType.DOG, 2020);
        pet.setId(1);
        adoption = new AdoptionDog(person, pet, LocalDate.now().minusDays(10), LocalDate.now().plusDays(20),
                AdoptionStatus.ON_PROBATION);
        adoption.setId(1);
        filePath = Path.of("reports", LocalDate.now().toString(), String.valueOf(adoption.getId()), "1.txt").toString();
        mediaType = MediaType.TEXT_PLAIN_VALUE;
        reportDate = LocalDate.now();
        adoptionReport = new AdoptionReportDog(adoption, filePath, mediaType, reportDate);

        adoptionService = new AdoptionDogService(adoptionRepository, personService, petService);
        adoptionReportService = new AdoptionReportDogService(adoptionReportRepository, adoptionService);
        formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
    }

    @Test
    public void findAllTest() {
        when(adoptionReportRepository.findAll()).thenReturn(new ArrayList<>(List.of(adoptionReport)));
        assertEquals(new ArrayList<>(List.of(adoptionReport)), adoptionReportService.findAll());
    }

    @Test
    public void findAllByDateTest() {
        when(adoptionReportRepository.findAllByReportDate(any(LocalDate.class)))
                .thenReturn(new ArrayList<>(List.of(adoptionReport)));
        assertEquals(new ArrayList<>(List.of(adoptionReport)), adoptionReportService.findAllByDate(LocalDate.now().format(formatter)));
    }

    @Test
    public void findAllByAdoptionAndReportDateTest() {
        when(adoptionRepository.findById(anyInt())).thenReturn(Optional.of(adoption));
        when(adoptionReportRepository.findAllByAdoptionAndReportDate(any(AdoptionDog.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>(List.of(adoptionReport)));
        assertEquals(new ArrayList<>(List.of(adoptionReport)), adoptionReportService.findAllByAdoptionAndReportDate(
                adoption, LocalDate.now()
        ));
        assertEquals(new ArrayList<>(List.of(adoptionReport)), adoptionReportService.findAllByAdoptionAndReportDate(
                adoption.getId(), LocalDate.now().format(formatter)
        ));

        when(adoptionRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertNull(adoptionReportService.findAllByAdoptionAndReportDate(
                adoption.getId(), LocalDate.now().format(formatter)));
    }

    @Test
    public void saveTest() {
        when(adoptionReportRepository.save(any(AdoptionReportDog.class))).thenReturn(adoptionReport);
        assertEquals(adoptionReport, adoptionReportService.save(adoptionReport));
    }

    @Test
    public void findAllByDateBetweenTest() {
        when(adoptionReportRepository.findAllByReportDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>(List.of(adoptionReport)));
        assertEquals(new ArrayList<>(List.of(adoptionReport)), adoptionReportService.findAllByDateBetween(
                LocalDate.now().minusDays(2), LocalDate.now()
        ));
        assertEquals(new ArrayList<>(List.of(adoptionReport)), adoptionReportService.findAllByDateBetween(
                LocalDate.now().minusDays(2).format(formatter), LocalDate.now().format(formatter)
        ));

        assertNull(adoptionReportService.findAllByDateBetween(LocalDate.now(), LocalDate.now().minusDays(2)));
    }

    @Test
    public void findByIdTest() {
        when(adoptionReportRepository.findById(anyInt())).thenReturn(Optional.of(adoptionReport));
        assertEquals(Optional.of(adoptionReport), adoptionReportService.findById(1));
    }

    @Test
    public void deleteTest() {
        doNothing().when(adoptionReportRepository).deleteById(anyInt());
        when(adoptionReportRepository.findById(anyInt())).thenReturn(Optional.of(adoptionReport));
        assertEquals(adoptionReport, adoptionReportService.delete(id, false));
    }


}
