package pro.sky.telegrambotshelter.listener;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.ForwardMessage;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pro.sky.telegrambotshelter.model.*;
import pro.sky.telegrambotshelter.model.adoption.Adoption;
import pro.sky.telegrambotshelter.model.adoption.AdoptionCat;
import pro.sky.telegrambotshelter.model.adoption.AdoptionDog;
import pro.sky.telegrambotshelter.model.adoptionReport.AdoptionReportCat;
import pro.sky.telegrambotshelter.model.adoptionReport.AdoptionReportDog;
import pro.sky.telegrambotshelter.service.*;
import pro.sky.telegrambotshelter.service.adoption.AdoptionCatService;
import pro.sky.telegrambotshelter.service.adoption.AdoptionDogService;
import pro.sky.telegrambotshelter.service.adoption.AdoptionService;
import pro.sky.telegrambotshelter.service.adoptionReport.AdoptionReportCatService;
import pro.sky.telegrambotshelter.service.adoptionReport.AdoptionReportDogService;
import pro.sky.telegrambotshelter.service.adoptionReport.AdoptionReportService;
import pro.sky.telegrambotshelter.service.person.PersonCatService;
import pro.sky.telegrambotshelter.service.person.PersonDogService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

@Component
public class TextMessageProcessor extends Processor {

    public TextMessageProcessor(PersonDogService personDogService, PersonCatService personCatService,
                                AdoptionDogService adoptionDogService, AdoptionCatService adoptionCatService,
                                AdoptionReportDogService adoptionReportDogService, AdoptionReportCatService adoptionReportCatService,
                                PetService petService, UserContextService userContextService) {
        super(personDogService, personCatService, adoptionDogService, adoptionCatService,
                adoptionReportDogService, adoptionReportCatService, petService, userContextService);
        logger = LoggerFactory.getLogger(TextMessageProcessor.class);
    }

    public void process(long chatId, String message, int messageId) {
        if (chatId == volunteerChatId) {
            switch (message) {
                case SEND_WARNING -> {
                    userContextService.save(chatId, SEND_WARNING);
                    requestShelterType(chatId, "Для усыновителя какого приюта необходимо отправить уведомление " +
                            "о недостаточной частоте и детальности отчетов");
                }
                default -> processVolunteerRequest(chatId, message);
            }
        } else {
            switch (message) {
                case START -> {
                    requestShelterType(chatId, bundle.getString(message));
                    userContextService.save(chatId, message);
                }
                case INFO -> {
                    sendInfoSubmenu(chatId);
                    userContextService.save(chatId, message);
                }
                case REPORT -> {
                    sendReportInfo(chatId);
                    userContextService.save(chatId, message);
                }
                case GET_A_PET -> {
                    sendGetAPetSubmenu(chatId);
                    userContextService.save(chatId, message);
                }
                case CALL_A_VOLUNTEER -> {
                    callAVolunteer(chatId);
                    userContextService.save(chatId, message);
                }
                default -> processUnknownRequest(chatId, message, messageId);
            }
        }
    }

    private void requestShelterType(long chatId, String message){
        InlineKeyboardButton[]keyboard = {new InlineKeyboardButton("Собаки").callbackData(DOG),
                new InlineKeyboardButton("Кошки").callbackData(CAT)};
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboard);
        sendMessage(new SendMessage(chatId, message).replyMarkup(inlineKeyboardMarkup));
    }

    /**
     * This method is for processing messages from volunteers. Volunteer is defined by the specific chat Id difined
     * in application.properties file.<br>
     * This method sends a warning notification to an adoptive parent with bad daily report history.
     * First, it checks if the last command sent by volunteer was the request to send such a notification.
     * This is checked by the {@link UserContextService#getLastCommand(long)} method. If the last command is
     * not correct, then no actions taken<br>
     * If the last command is correct, Then it waits for an adoption Id for whom it concerns, and then performs
     * sending of a warning notification itself
     */
    private void processVolunteerRequest(long chatId, String message) {
        String lastCommand = userContextService.getLastCommand(chatId);
        PetType shelter = userContextService.getPetType(chatId);
        if (chatId == volunteerChatId && lastCommand != null && lastCommand.equals(SEND_WARNING) && shelter != null) {
            try {
                int adoptionId = Integer.parseInt(message);
                Adoption adoption = shelter == PetType.DOG ? adoptionDogService.findById(adoptionId)
                        : adoptionCatService. findById(adoptionId);
                if (adoption == null) {
                    sendMessage(chatId, "Не найдена запись об усыновлении с adoption id = " + adoptionId +
                            ". Пожалуйста введите корректный номер id записи об усыновлении. ");
                    return;
                }
                sendMessage(adoption.getPerson().getChatId(), bundle.getString(SEND_WARNING));
                userContextService.save(chatId, "", null);
                sendMessage(chatId, "Сообщение отправлено");
            } catch (NumberFormatException e) {
                e.printStackTrace();
                    sendMessage(chatId, "Некорректный формат. Пожалуйста пришлите целочисленное значение adoption Id.");
                }
        }
    }

    /**
     * This method processes unknown messages from users.
     * The method checks for the last command sent by user with the {@link UserContextService#getLastCommand(long)}
     * method, and acts accordingly <br>
     * 1) if the last user command was a request to send daily report, then this message is saved as daily report by the
     * {@link TextMessageProcessor#saveTextReport} method.
     * The chatId is the key to identify whether the user has active probation adoptions or not, and also in which
     * shelter - Cat or Dog. This is done with the use of {@link AdoptionService#findByChatId(long)} method. Messages from users without active probation will be ignored.
     * <br>
     * 2) if the last command was to call a volunteer, then this message is forwarded to a volunteer.
     * 3) no action is taken in other cases
     *
     * @param chatId  telegram chat identification of the user
     * @param message text message sent from user
     */

    private void processUnknownRequest(long chatId, String message, int messageId) {
        Adoption adoption = adoptionDogService.findByChatId(chatId);
        PetType petType= PetType.DOG;
        if (adoption == null){
            adoption = adoptionCatService.findByChatId(chatId);
            petType = PetType.CAT;
        }
        String lastCommand = userContextService.getLastCommand(chatId);
        if (lastCommand != null) {
            switch (lastCommand) {
                case REPORT:
                    if (adoption == null) {
                        sendMessage(chatId, "Не найдены сведения по вашему усыновлению. " +
                                "Пожалуйста обратитесь к волонтеру.");
                        return;
                    } else {
                        try {
                            saveTextReport(chatId, adoption, message, petType);
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error("Error saving text report for adoption id: " + adoption.getId());
                            sendMessage(chatId, "Ошибка сохранения отчета. Пожалуйста обратитесь к волонтеру");
                        }
                    }
                    break;
                case CALL_A_VOLUNTEER:
                    try {
                        ForwardMessage forwardMessage = new ForwardMessage(volunteerChatId, chatId, messageId);
                        telegramBot.execute(forwardMessage);
                        sendMessage(chatId, "Спасибо! Ваше сообщение передано волонтеру. Пожалуйста ожидайте, " +
                                "волонтер скоро с вами свяжется");
                    } catch (Exception e) {
                        logger.error("Error forwarding request for volunteer from user with chat id " + chatId);
                        e.printStackTrace();
                    }
            }
        } else {
            sendMessage(chatId, "Команда не распознана \uD83E\uDD37\u200D♀️");
        }
    }

    private void sendInfoSubmenu(long chatId) {
        PetType petType = userContextService.getPetType(chatId);
        String path = addPersonUrl+ petType.toString() + "?chatId=" + chatId;
        InlineKeyboardButton[][] keyboard = {
                {new InlineKeyboardButton("О приюте").callbackData(TEXT_ABOUT_SHELTER),
                        new InlineKeyboardButton("Расписание, адрес, \nсхема проезда").callbackData(TEXT_ADDRESS)},
                {new InlineKeyboardButton("Заказать пропуск").callbackData(ORDER_PASS),
                        new InlineKeyboardButton("Правила безопасности").callbackData(TEXT_SAFETY)},
                {new InlineKeyboardButton("Оставить контактные данные").url(path),
                        new InlineKeyboardButton("Позвать волонтера").callbackData(CALL_A_VOLUNTEER)},
                {new InlineKeyboardButton("<< Вернуться").callbackData(GO_BACK)}
        };
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboard);
        sendMessage(new SendMessage(chatId, "Пожалуйста, выберите что Вас интересует:").replyMarkup(inlineKeyboardMarkup));
    }

    private void sendGetAPetSubmenu(long chatId) {
        PetType petType = userContextService.getPetType(chatId);
        String path = addPersonUrl+ petType.toString() + "?chatId=" + chatId;

        InlineKeyboardButton[] additionalButtonForDogs = {new InlineKeyboardButton("Советы кинолога").url(cynologistAdviceUrl),
                new InlineKeyboardButton("Список кинологов").callbackData(TEXT_CYNOLOGIST_LIST)};
        if (petType == PetType.CAT){
            additionalButtonForDogs = new InlineKeyboardButton[]{};
        }
        InlineKeyboardButton[][] keyboard = {
                {new InlineKeyboardButton("Как взять питомца").callbackData(GET_A_PET_INFO),
                        new InlineKeyboardButton("Транспортировка").callbackData(TEXT_TRANSPORTATION),
                        new InlineKeyboardButton("Подготовка дома").callbackData(HOUSE_ACCOMMODATION)},
                additionalButtonForDogs,
                {new InlineKeyboardButton("Оставить контактные данные").url(path),
                        new InlineKeyboardButton("Позвать волонтера").callbackData(CALL_A_VOLUNTEER)},
                {new InlineKeyboardButton("<< Вернуться").callbackData(GO_BACK)}
        };

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboard);
        sendMessage(new SendMessage(chatId, "Пожалуйста, выберите что Вас интересует:").replyMarkup(inlineKeyboardMarkup));
    }

    private void sendReportInfo(long chatId) {
        String message = String.format(bundle.getString(REPORT), MAX_FILES, MAX_FILES);
        sendMessage(chatId, message);
    }

    /**
     * This method saves messages from users as daily reports.
     * Files are saved in the {@link TextMessageProcessor#reportsPath}, under the directory with relevant shelter, date
     * and adoptionId from {@link Adoption} where they can later be accessed to be reviewed.
     * The limit is {@value MAX_FILES} messages a day. If the amount is exceeded, the users receives a notification to
     * call a volunteer.
     * If report saving is successful, then it is also saved to the "adoption_repot" table with the use of
     * {@link AdoptionReportService}
     *
     * @param chatId   of a reporting person
     * @param adoption {@link Adoption} object corresponding to the user
     * @param message  text message received from user to be saved as report
     * @throws IOException
     */
    private void saveTextReport(long chatId, Adoption adoption, String message, PetType petType) throws IOException {
        Path newFilePath = getFilePathUtil(adoption.getId(), "txt", petType.toString());
        if (newFilePath == null) {
            sendMessage(chatId, "Не удалось сохранить отчет. Превышено количество сообщений за " + LocalDate.now()
                    + ". Пожалуйста обратитесь к волонтеру");
            return;
        }
        try (FileWriter out = new FileWriter(newFilePath.toFile());
             BufferedWriter bOut = new BufferedWriter(out, 1024)
        ) {
            bOut.write(message);
        } catch (Exception e){
            e.printStackTrace();
            logger.error("Failed to save text report. AdoptionId=" + adoption.getId() + " text: " + message);
        }
        logger.info("Saved report file " + newFilePath);
        String contentType = Files.probeContentType(newFilePath);
        if (petType == PetType.DOG) {
            AdoptionReportDog adoptionReport = new AdoptionReportDog((AdoptionDog) adoption, newFilePath.toString(),
                    contentType, LocalDate.now());
            adoptionReportDogService.save(adoptionReport);
        } else if (petType == PetType.CAT){
            AdoptionReportCat adoptionReport = new AdoptionReportCat((AdoptionCat) adoption, newFilePath.toString(),
                    contentType, LocalDate.now());
            adoptionReportCatService.save(adoptionReport);
        }
        sendMessage(chatId, "Сообщение добавлено в отчет за " + LocalDate.now());
    }


}
