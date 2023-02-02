package pro.sky.telegrambotshelter.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.Adoption;
import pro.sky.telegrambotshelter.model.AdoptionReport;
import pro.sky.telegrambotshelter.repository.AdoptionReportRepository;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collection;

@Service
public class AdoptionReportService {
    @Value("${reports.path.dir}")
    private String reportsDir;
    private final AdoptionReportRepository adoptionReportRepository;

    public AdoptionReportService(AdoptionReportRepository adoptionReportRepository, AdoptionService adoptionService) {
        this.adoptionReportRepository = adoptionReportRepository;
    }

    public Collection<AdoptionReport> findAll(){
        return adoptionReportRepository.findAll();
    }

    public Collection<AdoptionReport> findAllByAdoptionAndReportDate(Adoption adoption, LocalDate date){
        return adoptionReportRepository.findAllByAdoptionAndReportDate(adoption, date);
    }

    public void addReport(Adoption adoption, String mediaType, String text, LocalDate date) {
        Path filePath = Path.of(reportsDir, String.valueOf(adoption.getId()), date.toString(), "1.txt");
        AdoptionReport report = new AdoptionReport(adoption, filePath.toString(), mediaType, text, date);
        adoptionReportRepository.save(report);
    }


}