package pro.sky.telegrambotshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import pro.sky.telegrambotshelter.model.Adoption;
import pro.sky.telegrambotshelter.model.AdoptionReport;
import pro.sky.telegrambotshelter.model.Person;

import java.time.LocalDate;
import java.util.Collection;

@NoRepositoryBean
public interface AdoptionReportRepository<V extends AdoptionReport<S, T>, S extends Adoption<T>, T extends Person>
        extends JpaRepository<V, Integer> {
    Collection<V> findAllByAdoptionAndReportDate(S adoption, LocalDate reportDate);

    Collection<V> findAllByReportDate(LocalDate reportDate);

    Collection<V> findAllByReportDateBetween(LocalDate from, LocalDate to);
}
