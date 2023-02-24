package pro.sky.telegrambotshelter.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@MappedSuperclass
public class AdoptionReport<S extends Adoption<T>, T extends Person> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne()
    @JoinColumn(name = "adoption_id")
    private S adoption;
    private String filePath;
    private String mediaType;
    private LocalDate reportDate;

    public AdoptionReport() {
    }

    public AdoptionReport(S adoption, String filePath, String mediaType, LocalDate date) {
        this.adoption = adoption;
        this.filePath = filePath;
        this.mediaType = mediaType;
        this.reportDate = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public S getAdoption() {
        return adoption;
    }

    public void setAdoption(S adoption) {
        this.adoption = adoption;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate date) {
        this.reportDate = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdoptionReport that = (AdoptionReport) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AdoptionReport{" +
                "id=" + id +
                ", adoption=" + adoption +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
