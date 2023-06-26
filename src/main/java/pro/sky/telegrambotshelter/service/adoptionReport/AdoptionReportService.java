package pro.sky.telegrambotshelter.service.adoptionReport;

import pro.sky.telegrambotshelter.model.adoption.Adoption;
import pro.sky.telegrambotshelter.model.adoptionReport.AdoptionReport;
import pro.sky.telegrambotshelter.model.person.Person;
import pro.sky.telegrambotshelter.repository.AdoptionReportRepository;
import pro.sky.telegrambotshelter.service.adoption.AdoptionService;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;

/**
 * A Service class to perform CRUD operations with the adoption report tables in database for Cat and Dog shelters.
 * @author Ekaterina Gorbacheva
 * @see AdoptionReportCatService
 * @see AdoptionReportDogService
 */
public abstract class AdoptionReportService<V extends AdoptionReport<S, T>, S extends Adoption<T>, T extends Person > {
    private final AdoptionReportRepository<V, S, T> adoptionReportRepository;
    private final AdoptionService<S, T> adoptionService;
    private final DateTimeFormatter formatter;

    public AdoptionReportService(AdoptionReportRepository<V, S, T> adoptionReportRepository,
                                 AdoptionService<S, T> adoptionService) {
        this.adoptionReportRepository = adoptionReportRepository;
        this.adoptionService = adoptionService;
        formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
    }

    public Collection<V> findAll(){
        return adoptionReportRepository.findAll();
    }

    /**
     * A method to receive a {@link Collection} of all {@link AdoptionReport} records saved in db table
     * "adoption_report_dog"/"adoption_report_cat" for a specific date. Uses {@link AdoptionReportRepository}
     * @param date in String format ("ddMMyyyy")
     * @return {@link Collection} of {@link AdoptionReport} objects with the date specified
     */
    public Collection<V> findAllByDate(String date){
        LocalDate reportDate = LocalDate.parse(date, formatter);
        return adoptionReportRepository.findAllByReportDate(reportDate);
    }

    public Collection<V> findAllByAdoptionAndReportDate(Integer adoptionId, String date){
        LocalDate reportDate = LocalDate.parse(date, formatter);
        S adoption = adoptionService.findById(adoptionId);
        return findAllByAdoptionAndReportDate(adoption, reportDate);
    }
    public Collection<V> findAllByAdoptionAndReportDate(S adoption, LocalDate reportDate){
        if (adoption == null){
            return null;
        }
        return adoptionReportRepository.findAllByAdoptionAndReportDate(adoption, reportDate);
    }

    public V save(V adoptionReport) {
        return adoptionReportRepository.save(adoptionReport);
    }

    public Collection<V> findAllByDateBetween(String from, String to) {
        LocalDate fromDate = LocalDate.parse(from, formatter);
        LocalDate toDate = LocalDate.parse(to, formatter);
        return adoptionReportRepository.findAllByReportDateBetween(fromDate, toDate);
    }

    public Collection<V> findAllByDateBetween(LocalDate from, LocalDate to){
        if (from == null || to == null || from.isAfter(to)){
            return null;
        }
        return adoptionReportRepository.findAllByReportDateBetween(from, to);
    }

    public Optional<V> findById(Integer id) {
        return adoptionReportRepository.findById(id);
    }

    /**
     * Removes a record from the "adoption_report_dog"/"adoption_report_cat" table with this id.
     * Uses {@link AdoptionReportRepository}
     * @param id identification of a {@link AdoptionReport} to be removed from db table "adoption_report_dog"/ "adoption_report_cat"
     * @param removeFile flag for whether to remove the report file represented by this record in table or not
     *                   true - to remove the file, false - to retain the file
     */
    public V delete(Integer id, boolean removeFile) {
        V adoptionReport = adoptionReportRepository.findById(id).orElse(null);
        if (adoptionReport == null){
            return null;
        }
        if (removeFile){
            try {
                File file = new File(adoptionReport.getFilePath());
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        adoptionReportRepository.deleteById(id);
        return adoptionReport;
    }
}
