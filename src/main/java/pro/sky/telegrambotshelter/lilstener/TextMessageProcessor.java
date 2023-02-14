package pro.sky.telegrambotshelter.lilstener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.telegrambotshelter.model.Adoption;
import pro.sky.telegrambotshelter.model.AdoptionReport;
import pro.sky.telegrambotshelter.service.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

@Component
public class TextMessageProcessor extends Processor {

    public TextMessageProcessor(PersonService personService, AdoptionService adoptionService, PetService petService,
                                AdoptionReportService adoptionReportService, UserContextService userContextService) {
        super(personService, petService, adoptionService, adoptionReportService, userContextService);
    }

    public void setTelegramBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void process(long chatId, String message) {
        switch (message) {
            case START -> {
                sendStartMenu(chatId);
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
            case GET_A_DOG -> {
                sendGetADogSubmenu(chatId);
                userContextService.save(chatId, message);
            }
            case CALL_A_VOLUNTEER -> {
                callAVolunteer(chatId);
                userContextService.save(chatId, message);
            }
            default -> processUnknownRequest(chatId, message);
        }
    }


    /**
     * This method treats unknown messages from registered users with active adoptions as probation daily report messages.
     * The text message content is saved as '.txt' file with the
     * {@link TextMessageProcessor#saveTextReport(long, Adoption, String)} method.
     * Files are saved in the {@link TextMessageProcessor#reportsPath}, where they can later be accessed
     * to be reviewed.
     * The chatId is the key to identify whether the user has active probation adoptions or not with the use of
     * {@link AdoptionService#findByChatId(long)} method.
     * Messages from users without active probation will be ignored.
     *
     * @param chatId  telegram chat identification of the user
     * @param message text message sent from user
     */
//    private void processUnknownRequest(long chatId, String message) {
//        Adoption adoption = adoptionService.findByChatId(chatId);
//        if (adoption != null) {
//            try {
//                saveTextReport(chatId, adoption, message);
//            } catch (Exception e) {
//                e.printStackTrace();
////                logger.error("Не удалось сохранить отчет. ChatId = " + chatId + ". Message: " + message);
//                sendMessage(chatId, "Ошибка сохранения отчета. Пожалуйста обратитесь к волонтеру");
//            }
//        } else {
//            sendMessage(chatId, "Команда не распознана \uD83E\uDD37\u200D♀️");
//        }
//    }

    private void processUnknownRequest(long chatId, String message) {
        Adoption adoption = adoptionService.findByChatId(chatId);
        String lastCommand = userContextService.getLastCommand(chatId);
        if (adoption != null && lastCommand != null && lastCommand.equals(REPORT)) {
            try {
                saveTextReport(chatId, adoption, message);
            } catch (Exception e) {
                e.printStackTrace();
//                logger.error("Не удалось сохранить отчет. ChatId = " + chatId + ". Message: " + message);
                sendMessage(chatId, "Ошибка сохранения отчета. Пожалуйста обратитесь к волонтеру");
            }
        } else {
            sendMessage(chatId, "Команда не распознана \uD83E\uDD37\u200D♀️");
        }
    }

    private void sendInfoSubmenu(long chatId) {
        String path = addPersonUrl + chatId;
        InlineKeyboardButton[][] keyboard = {
                {new InlineKeyboardButton("О приюте").callbackData(ABOUT_SHELTER),
                        new InlineKeyboardButton("Расписание, адрес, \nсхема проезда").callbackData(ADDRESS),
                        new InlineKeyboardButton("Правила безопасности").callbackData(SAFETY_MEASURES)},
                {new InlineKeyboardButton("Оставить контактные данные").url(path),
                        new InlineKeyboardButton("Позвать волонтера").callbackData(CALL_A_VOLUNTEER)},
                {new InlineKeyboardButton("<< Вернуться").callbackData(GO_BACK)}
        };
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboard);
        sendMessage(new SendMessage(chatId, "Пожалуйста, выберите что Вас интересует:").replyMarkup(inlineKeyboardMarkup));
    }

    private void sendGetADogSubmenu(long chatId) {
        String path = addPersonUrl + chatId;
        InlineKeyboardButton[][] keyboard = {
                {new InlineKeyboardButton("Как взять собаку").callbackData(GET_A_DOG_INFO),
                        new InlineKeyboardButton("Транспортировка").callbackData(TRANSPORTATION),
                        new InlineKeyboardButton("Подготовка дома").callbackData(HOUSE_ACCOMMODATION)},
                {new InlineKeyboardButton("Советы кинолога").url(cynologistAdviceUrl),
                        new InlineKeyboardButton("Список кинологов").callbackData(CYNOLOGIST_LIST)},
                {new InlineKeyboardButton("Оставить контактные данные").url(path),
                        new InlineKeyboardButton("Позвать волонтера").callbackData(CALL_A_VOLUNTEER)},
                {new InlineKeyboardButton("<< Вернуться").callbackData(GO_BACK)}
        };
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboard);
        sendMessage(new SendMessage(chatId, "Пожалуйста, выберите что Вас интересует:").replyMarkup(inlineKeyboardMarkup));
    }

    private void sendReportInfo(long chatId) {
        sendMessage(chatId, """
                Ежедневный отчет должен включать:\n 
                1) Фото животного. Пожалуйста отправьте от 1 до """
                + MAX_FILES + """
                 фото.\n 
                2) Текстовый отчет. Пожалуйста отправьте текстовый отчет отдельным от фото сообщением (от 1 до"""
                + MAX_FILES + """
                  сообщений). Отчет должен включать: 
                - Рацион животного. 
                - Общее самочувствие и привыкание к новому месту.
                - Изменение в поведении: отказ от старых привычек, приобретение новых.
                """);
    }

    /**
     * This method saves messages from users as dayily reports.
     * Files are saved in the {@link TextMessageProcessor#reportsPath}, under the directory with relevant date
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
    private void saveTextReport(long chatId, Adoption adoption, String message) throws IOException {
        Path newFilePath = getFilePathUtil(adoption.getId(), "txt");
        if (newFilePath == null) {
            sendMessage(chatId, "Не удалось сохранить отчет. Превышено количество сообщений за " + LocalDate.now()
                    + ". Пожалуйста обратитесь к волонтеру");
            return;
        }
        try (FileWriter out = new FileWriter(newFilePath.toFile());
             BufferedWriter bOut = new BufferedWriter(out, 1024)
        ) {
            bOut.write(message);
        }
//        logger.info("Saved report file " + newFilePath);
        String contentType = Files.probeContentType(newFilePath);
        AdoptionReport adoptionReport = new AdoptionReport(adoption, newFilePath.toString(),
                contentType, LocalDate.now());
        adoptionReportService.save(adoptionReport);
        sendMessage(chatId, "Сообщение добавлено в отчет за " + LocalDate.now());
    }


}
