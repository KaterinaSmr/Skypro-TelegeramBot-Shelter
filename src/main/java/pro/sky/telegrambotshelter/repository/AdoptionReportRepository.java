package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambotshelter.model.Adoption;
import pro.sky.telegrambotshelter.model.AdoptionReport;

import java.time.LocalDate;
import java.util.Collection;

public interface AdoptionReportRepository extends JpaRepository<AdoptionReport, Integer> {

    Collection<AdoptionReport> findAllByAdoptionAndReportDate(Adoption adoption, LocalDate reportDate);
}
