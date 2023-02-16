package pro.sky.telegrambotshelter.lilstener;

import com.pengrad.telegrambot.request.SendContact;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambotshelter.model.Adoption;
import pro.sky.telegrambotshelter.model.AdoptionReport;
import pro.sky.telegrambotshelter.model.AdoptionStatus;
import pro.sky.telegrambotshelter.model.Person;
import pro.sky.telegrambotshelter.service.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ScheduledJobsExecutor extends Processor {

    private final String DAILY_TEXT_REPORT_REMINDER="DAILY_TEXT_REPORT_REMINDER";
    private final String DAILY_PHOTO_REPORT_REMINDER="DAILY_PHOTO_REPORT_REMINDER";
    private final String PROBATION_EXTENDED_NOTIF = "PROBATION_EXTENDED_NOTIF";
    private final String PROBATION_FAILED_NOTIF = "PROBATION_FAILED_NOTIF";
    private final String PROBATION_SUCCESSFUL_NOTIF = "PROBATION_SUCCESSFUL_NOTIF";
    private final String REPORTS_MISSING_FOR2DAYS = "REPORTS_MISSING_FOR2DAYS";

    public ScheduledJobsExecutor(PersonService personService, AdoptionService adoptionService, PetService petService,
                                  AdoptionReportService adoptionReportService, UserContextService userContextService) {
        super(personService, petService, adoptionService, adoptionReportService, userContextService);
    }

    @Scheduled (cron = "0 0 12 * * *")
    public void dailyReportReminded(){
        List<Adoption> activeAdoptions = adoptionService.getAllActiveAdoptions();
        //sending reminder to those who hasn't sent text report
        activeAdoptions.stream()
                .filter(a -> adoptionReportService.findAllByAdoptionAndReportDate(a, LocalDate.now()).stream()
                        .noneMatch(adoptionReport -> adoptionReport.getMediaType().equals(MediaType.TEXT_PLAIN.toString())))
                .forEach(adoption -> sendMessage(adoption.getPerson().getChatId(), bundle.getString(DAILY_TEXT_REPORT_REMINDER)));
        //sending reminder to those who hasn't sent photo report
        activeAdoptions.stream()
                .filter(a -> adoptionReportService.findAllByAdoptionAndReportDate(a, LocalDate.now()).stream()
                        .noneMatch(adoptionReport -> adoptionReport.getMediaType().equals(MediaType.IMAGE_JPEG_VALUE)))
                .forEach(adoption -> sendMessage(adoption.getPerson().getChatId(), bundle.getString(DAILY_PHOTO_REPORT_REMINDER)));
    }

    @Scheduled(cron = "0 44 9 * * *")
    public void callVolunteerForInaccurateReports() {
        Set<Adoption> adoptionsWithReportsFor2Days = adoptionReportService
                .findAllByDateBetween(LocalDate.now().minusDays(2), LocalDate.now())
                .stream()
                .map(AdoptionReport::getAdoption)
                .collect(Collectors.toSet());
        adoptionService.getAllActiveAdoptions().stream()
                .filter(adoption -> !adoptionsWithReportsFor2Days.contains(adoption))
                .forEach(adoption -> {
                    sendMessage(volunteerChatId, bundle.getString(REPORTS_MISSING_FOR2DAYS)
                            + " Adoption ID: " + adoption.getId());
                    Person person = adoption.getPerson();
                    telegramBot.execute(new SendContact(volunteerChatId, person.getPhone(), person.getFirstName()));
                });
    }

    @Scheduled (cron = "0 0 15 * * *")
    public void adoptionStatusUpdateNotification(){
        //отправка уведомления о смене статуса испытательного срока + смена статуса
        adoptionService.getAllAdoptionsByStatus(AdoptionStatus.PROBATION_EXTENDED).forEach(adoption -> {
            sendMessage(adoption.getPerson().getChatId(), bundle.getString(PROBATION_EXTENDED_NOTIF)
            + adoption.getProbationEndDate());
            adoptionService.setNewStatus(adoption, AdoptionStatus.ON_PROBATION);
        });
        adoptionService.getAllAdoptionsByStatus(AdoptionStatus.PROBATION_FAILED).forEach(adoption -> {
            sendMessage(adoption.getPerson().getChatId(), bundle.getString(PROBATION_FAILED_NOTIF));
            adoptionService.setNewStatus(adoption, AdoptionStatus.ADOPTION_REFUSED);
        });
        adoptionService.getAllAdoptionsByStatus(AdoptionStatus.PROBATION_SUCCESSFUL).forEach(adoption -> {
            sendMessage(adoption.getPerson().getChatId(), bundle.getString(PROBATION_SUCCESSFUL_NOTIF));
            adoptionService.setNewStatus(adoption, AdoptionStatus.ADOPTION_CONFIRMED);
        });
    }
}
