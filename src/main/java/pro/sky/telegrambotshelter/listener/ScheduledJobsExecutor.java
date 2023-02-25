package pro.sky.telegrambotshelter.listener;

import com.pengrad.telegrambot.request.SendContact;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambotshelter.model.*;
import pro.sky.telegrambotshelter.service.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class contains scheduled jobs to be executed by the Telegram bot
 * @author Ekaterina Gorbacheva
 */
@Component
public class ScheduledJobsExecutor extends Processor {

    private final String DAILY_TEXT_REPORT_REMINDER="DAILY_TEXT_REPORT_REMINDER";
    private final String DAILY_PHOTO_REPORT_REMINDER="DAILY_PHOTO_REPORT_REMINDER";
    private final String PROBATION_EXTENDED_NOTIF = "PROBATION_EXTENDED_NOTIF";
    private final String PROBATION_FAILED_NOTIF = "PROBATION_FAILED_NOTIF";
    private final String PROBATION_SUCCESSFUL_NOTIF = "PROBATION_SUCCESSFUL_NOTIF";
    private final String REPORTS_MISSING_FOR2DAYS = "REPORTS_MISSING_FOR2DAYS";


    public ScheduledJobsExecutor(PersonDogService personDogService, PersonCatService personCatService,
                                 AdoptionDogService adoptionDogService, AdoptionCatService adoptionCatService,
                                 AdoptionReportDogService adoptionReportDogService, AdoptionReportCatService adoptionReportCatService,
                                 PetService petService, UserContextService userContextService) {
        super(personDogService, personCatService, adoptionDogService, adoptionCatService,
                adoptionReportDogService, adoptionReportCatService, petService, userContextService);
        logger = LoggerFactory.getLogger(ScheduledJobsExecutor.class);
    }

   /**
     * This method sends daily report reminder to all people with active adoptions on probation.<br>
     *  - If a person hasn't sent daily text report yet, he will receive reminder to send text report.<br>
     *  - If a person hasn't sent daily photo report yet, he will receive reminder to send photo report.<br>
     *  - If a person has sent no report on the selected day, he will receive both reminders.<br>
     * Active probations are selected according to the method {@link AdoptionService#getAllActiveProbations()}.
     * The presence of text or photo report is determined based on the records in the {@code adoption_report} table
     * with the current date value in the {@code report_date} table
     */
    @Scheduled (cron = "0 0 12 * * *")
    public void dailyReportReminder(){
        logger.info("Sending daily report reminders");
        List<AdoptionDog> activeAdoptionsDog = adoptionDogService.getAllActiveProbations();
        List<AdoptionCat> activeAdoptionsCat = adoptionCatService.getAllActiveProbations();
        //sending reminder to those who hasn't sent text report
        activeAdoptionsDog.stream()
                .filter(a -> adoptionReportDogService.findAllByAdoptionAndReportDate(a, LocalDate.now()).stream()
                        .noneMatch(adoptionReport -> adoptionReport.getMediaType().equals(MediaType.TEXT_PLAIN.toString())))
                .forEach(adoption -> sendMessage(adoption.getPerson().getChatId(), bundle.getString(DAILY_TEXT_REPORT_REMINDER)));
        activeAdoptionsCat.stream()
                .filter(a -> adoptionReportCatService.findAllByAdoptionAndReportDate(a, LocalDate.now()).stream()
                        .noneMatch(adoptionReport -> adoptionReport.getMediaType().equals(MediaType.TEXT_PLAIN.toString())))
                .forEach(adoption -> sendMessage(adoption.getPerson().getChatId(), bundle.getString(DAILY_TEXT_REPORT_REMINDER)));
        //sending reminder to those who hasn't sent photo report
        Set<String> photoMediaTypes = new HashSet<>(Set.of(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE,
                MediaType.IMAGE_PNG_VALUE));
        activeAdoptionsDog.stream()
                .filter(a -> adoptionReportDogService.findAllByAdoptionAndReportDate(a, LocalDate.now()).stream()
                        .noneMatch(adoptionReport -> photoMediaTypes.contains(adoptionReport.getMediaType())))
                .forEach(adoption -> sendMessage(adoption.getPerson().getChatId(), bundle.getString(DAILY_PHOTO_REPORT_REMINDER)));
        activeAdoptionsCat.stream()
                .filter(a -> adoptionReportCatService.findAllByAdoptionAndReportDate(a, LocalDate.now()).stream()
                        .noneMatch(adoptionReport -> photoMediaTypes.contains(adoptionReport.getMediaType())))
                .forEach(adoption -> sendMessage(adoption.getPerson().getChatId(), bundle.getString(DAILY_PHOTO_REPORT_REMINDER)));
    }

    /**
     * This method sends a notification to a volunteer with the contacts of adoptive parents with no reports for
     * last 2 days.<br>
     * A volunteer is defined by special {@code chatId} specified in the application.properties
     * The list of adoptive parents without reports for 2 days if determined from the {@code adoption_report} table.
     * Only active adoptions on probation are taken into account. Active adoptions on probation are determined by the
     * {@link AdoptionService#getAllActiveProbations()} method. <br>
     * The bot send text info and contact of a person with missing report. Contact included phone number and name from
     * {@code person} table
     */
    @Scheduled(cron = "0 0 13 * * *")
    public void callVolunteerForInaccurateReports() {
        logger.info("Sending contacts with bad report records to volunteer");

        Set<AdoptionDog> adoptionsWithReportsFor2DaysDog = adoptionReportDogService
                .findAllByDateBetween(LocalDate.now().minusDays(2), LocalDate.now())
                .stream()
                .map(AdoptionReport::getAdoption)
                .collect(Collectors.toSet());
        adoptionDogService.getAllActiveProbations().stream()
                .filter(adoption -> !adoptionsWithReportsFor2DaysDog.contains(adoption))
                .forEach(adoption -> {
                    sendMessage(volunteerChatId, bundle.getString(REPORTS_MISSING_FOR2DAYS)
                            + " Adoption ID: " + adoption.getId());
                    Person person = adoption.getPerson();
                    try {
                        telegramBot.execute(new SendContact(volunteerChatId, person.getPhone(), person.getFirstName()));
                    } catch (Exception e) {
                        logger.error("Contact sending failed");
                        e.printStackTrace();
                    }
                });
        Set<AdoptionCat> adoptionsWithReportsFor2DaysCat = adoptionReportCatService
                .findAllByDateBetween(LocalDate.now().minusDays(2), LocalDate.now())
                .stream()
                .map(AdoptionReport::getAdoption)
                .collect(Collectors.toSet());
        adoptionCatService.getAllActiveProbations().stream()
                .filter(adoption -> !adoptionsWithReportsFor2DaysCat.contains(adoption))
                .forEach(adoption -> {
                    sendMessage(volunteerChatId, bundle.getString(REPORTS_MISSING_FOR2DAYS)
                            + " Adoption ID: " + adoption.getId());
                    Person person = adoption.getPerson();
                    try {
                        telegramBot.execute(new SendContact(volunteerChatId, person.getPhone(), person.getFirstName()));
                    } catch (Exception e) {
                        logger.error("Contact sending failed");
                        e.printStackTrace();
                    }
                });
    }

    /**
     * This method sends adoption status change notification to adoptive people.
     * Only 3 adoption statuses are triggering status change notification. These are temporary statuses:
     * {@link AdoptionStatus#PROBATION_EXTENDED}, {@link AdoptionStatus#PROBATION_FAILED} and
     * {@link AdoptionStatus#PROBATION_FAILED} <br>
     * People with adoption in one of these statuses will receive status change notification.
     * As soon as the notification is sent, the status is updated to one of the regular statuses:<br>
     * {@link AdoptionStatus#PROBATION_EXTENDED} to {@link AdoptionStatus#ON_PROBATION}
     * {@link AdoptionStatus#PROBATION_FAILED} to {@link AdoptionStatus#ADOPTION_REFUSED}
     * {@link AdoptionStatus#PROBATION_FAILED} to {@link AdoptionStatus#ADOPTION_CONFIRMED}
     * After status is changed, these adoptions are no longer relevant to adoption status notification sending.
     */
    @Scheduled (cron = "0 0 15 * * *")
    public void adoptionStatusUpdateNotification(){
        logger.info("Sending status update notifications");
        //отправка уведомления о смене статуса испытательного срока + смена статуса
        EnumMap<AdoptionStatus, AdoptionStatus> statusMap = new EnumMap<>(AdoptionStatus.class);
        statusMap.put(AdoptionStatus.PROBATION_EXTENDED, AdoptionStatus.ON_PROBATION);
        statusMap.put(AdoptionStatus.PROBATION_FAILED, AdoptionStatus.ADOPTION_REFUSED);
        statusMap.put(AdoptionStatus.PROBATION_SUCCESSFUL, AdoptionStatus.ADOPTION_CONFIRMED);
        EnumMap<AdoptionStatus, String> messageMap = new EnumMap<>(AdoptionStatus.class);
        messageMap.put(AdoptionStatus.PROBATION_EXTENDED, bundle.getString(PROBATION_EXTENDED_NOTIF));
        messageMap.put(AdoptionStatus.PROBATION_FAILED, bundle.getString(PROBATION_FAILED_NOTIF));
        messageMap.put(AdoptionStatus.PROBATION_SUCCESSFUL, bundle.getString(PROBATION_SUCCESSFUL_NOTIF));
        adoptionDogService.getAllAdoptionsByStatus(statusMap.keySet().toArray(new AdoptionStatus[]{}))
                .forEach(adoption -> {
                    AdoptionStatus adoptionStatus = adoption.getAdoptionStatus();
                    sendMessage(adoption.getPerson().getChatId(), messageMap.get(adoptionStatus)
                            + adoption.getProbationEndDate());
                    adoptionDogService.setNewStatus(adoption, statusMap.get(adoptionStatus));
                    logger.info("Dog shelter status changed: " + adoptionStatus + " -> " + adoption.getAdoptionStatus() +
                            " for adoptionId: " + adoption.getId());
                });
        adoptionCatService.getAllAdoptionsByStatus(statusMap.keySet().toArray(new AdoptionStatus[]{}))
                .forEach(adoption -> {
                    AdoptionStatus adoptionStatus = adoption.getAdoptionStatus();
                    sendMessage(adoption.getPerson().getChatId(), messageMap.get(adoptionStatus)
                            + adoption.getProbationEndDate());
                    adoptionCatService.setNewStatus(adoption, statusMap.get(adoptionStatus));
                    logger.info("Cat shelter status changed: " + adoptionStatus + " -> " + adoption.getAdoptionStatus() +
                            " for adoptionId: " + adoption.getId());
                });
    }

}
