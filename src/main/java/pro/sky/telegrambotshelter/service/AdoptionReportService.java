package pro.sky.telegrambotshelter.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.Adoption;
import pro.sky.telegrambotshelter.model.AdoptionReport;
import pro.sky.telegrambotshelter.repository.AdoptionReportRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;

@Service
public class AdoptionReportService {
    private final AdoptionReportRepository adoptionReportRepository;
    private final AdoptionService adoptionService;
    private final DateTimeFormatter formatter;

    public AdoptionReportService(AdoptionReportRepository adoptionReportRepository, AdoptionService adoptionService, AdoptionService adoptionService1) {
        this.adoptionReportRepository = adoptionReportRepository;
        this.adoptionService = adoptionService1;
        formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
    }

    public Collection<AdoptionReport> findAll(){
        return adoptionReportRepository.findAll();
    }

    public Collection<AdoptionReport> findAllByDate(String date){
        LocalDate reportDate = LocalDate.parse(date, formatter);
        return adoptionReportRepository.findAllByReportDate(reportDate);
    }

    public Collection<AdoptionReport> findAllByAdoptionAndReportDate(Integer adoptionId, String date){
        LocalDate reportDate = LocalDate.parse(date, formatter);
        Adoption adoption = adoptionService.findById(adoptionId);
        return adoptionReportRepository.findAllByAdoptionAndReportDate(adoption, reportDate);
    }

    public void save(AdoptionReport adoptionReport) {
        adoptionReportRepository.save(adoptionReport);
    }

    public Collection<AdoptionReport> findAllByDateBetween(String from, String to) {
        LocalDate fromDate = LocalDate.parse(from, formatter);
        LocalDate toDate = LocalDate.parse(to, formatter);
        return adoptionReportRepository.findAllByReportDateBetween(fromDate, toDate);
    }

    public Optional<AdoptionReport> findById(Integer id) {
        return adoptionReportRepository.findById(id);
    }
}
