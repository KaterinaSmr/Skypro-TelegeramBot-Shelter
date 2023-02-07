package pro.sky.telegrambotshelter.lilstener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;

import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.model.Adoption;
import pro.sky.telegrambotshelter.model.AdoptionReport;
import pro.sky.telegrambotshelter.service.AdoptionReportService;
import pro.sky.telegrambotshelter.service.AdoptionService;
import pro.sky.telegrambotshelter.service.PersonService;
import pro.sky.telegrambotshelter.service.PetService;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private static final int MAX_FILES = 10;

    @Value("${newperson.path}")
    private String addPersonPath;

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final PersonService personService;
    private final PetService petService;
    private final AdoptionService adoptionService;
    private final AdoptionReportService adoptionReportService;

    private final TelegramBot telegramBot;

    private static final String START = "/start";
    private static final String INFO = "/info";
    private static  final String GET_A_DOG = "/getadog";
    private static  final String REPORT = "/report";
    private static  final String CALL_A_VOLUNTEER = "/volunteer";
    private static  final String ABOUT_SHELTER = "/about";
    private static  final String ADDRESS = "/address";
    private static  final String SAFETY_MEASURES = "/safety";
    private static  final String SAVE_CONTACTS = "/contacts";
    private static  final String GO_BACK = "/back";
    private static  final String GET_A_DOG_INFO = "/getadoginfo";
    private static  final String MEETING_DOG_INFO = "/meetingdog";
    private static  final String ADOPTION_DOCS ="/adoptiondocs";
    private static  final String ADOPTION_REFUSAL="/adoptionrefusal";
    private static  final String TRANSPORTATION = "/transportation";
    private static  final String HOUSE_ACCOMMODATION = "/houseaccommodation";
    private static  final String PUPPY_HOUSE_PREPARATION = "/puppyhouse";
    private static  final String DOG_HOUSE_PREPARATION = "/doghouse";
    private static  final String DOG_HANDICAP_HOUSE_PREP="/doghandicap";
    private static  final String CYNOLOGIST_ADVICE ="/cynologadvice";
    private static  final String CYNOLOGIST_LIST="/cynologlist";
    private static final String YES = "/yes";
    private static final String NO = "/no" ;

    public TelegramBotUpdatesListener(PersonService personService, PetService petService, AdoptionService adoptionService, AdoptionReportService adoptionReportService, TelegramBot telegramBot) {
        this.personService = personService;
        this.petService = petService;
        this.adoptionService = adoptionService;
        this.adoptionReportService = adoptionReportService;
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    public void init(){
        telegramBot.setUpdatesListener(this);
    }


    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update " + update);
            if (update.message() != null) {
                String message = update.message().text();
                long chatId = update.message().chat().id();
                if (message != null) {
                    switch (message) {
                        case START:
                            sendStartMenu(chatId, update.message().chat().firstName());
                            break;
                        case INFO:
                            sendInfoSubmenu(chatId);
                            break;
                        case REPORT:
                            sendReportInfo(chatId);
                            break;
                        case GET_A_DOG:
                            sendGetADogSubmenu(chatId);
                            break;
                        case CALL_A_VOLUNTEER:
                            callAVolunteer(chatId);
                            break;
                        default:
                            processUnknownRequest(chatId, message);
                    }
                } else if (update.message().photo()!=null){
                    PhotoSize[] photos = update.message().photo();
                    try {
                        savePhotoReport(photos, chatId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("Не удалось сохранить фото. ChatId = " + chatId);
                        sendMessage(chatId, "Ошибка сохранения отчета. Пожалуйста обратитесь к волонтеру");
                    }
                }
            } else if(update.callbackQuery() != null){
                String data = update.callbackQuery().data();
                long chatId = update.callbackQuery().message().chat().id();
                switch (data){
                    case ABOUT_SHELTER:
                        sendAboutShelter(chatId);
                        break;
                    case ADDRESS:
                        sendAddressData(chatId);
                        break;
                    case SAFETY_MEASURES:
                        sendSafetyInfo(chatId);
                        break;
                    case SAVE_CONTACTS:
                        saveContacts(chatId);
                        break;
                    case CALL_A_VOLUNTEER:
                        callAVolunteer(chatId);
                        break;
                    case GO_BACK:
                        sendStartMenu(chatId, update.callbackQuery().message().chat().firstName());
                        break;
                    case GET_A_DOG_INFO:
                        getADogInfo(chatId);
                        break;
                    case MEETING_DOG_INFO:
                        sendFirstMeetingInfo(chatId);
                        break;
                    case ADOPTION_DOCS:
                        sendAdoptionDocumentsInfo(chatId);
                        break;
                    case ADOPTION_REFUSAL:
                        sendRefusalInfo(chatId);
                        break;
                    case TRANSPORTATION:
                        sendTransportationIfo(chatId);
                        break;
                    case HOUSE_ACCOMMODATION:
                        sendHouseMenu(chatId);
                        break;
                    case PUPPY_HOUSE_PREPARATION:
                        sendPuppyHousePrepDetails(chatId);
                        break;
                    case DOG_HOUSE_PREPARATION:
                        sendDogHousePrepDetails(chatId);
                        break;
                    case DOG_HANDICAP_HOUSE_PREP:
                        sendDogHandicapHousePrepDetails(chatId);
                        break;
                    case CYNOLOGIST_ADVICE:
                        sendCynologistAdvice(chatId);
                        break;
                    case CYNOLOGIST_LIST:
                        sendCynologistList(chatId);
                        break;
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void processUnknownRequest(long chatId, String message) {
        Adoption adoption = adoptionService.findByChatId(chatId);
        if (adoption != null) {
            try {
                saveTextReport(chatId, adoption, message);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Не удалось сохранить отчет. ChatId = " + chatId + ". Message: " + message);
                sendMessage(chatId,"Ошибка сохранения отчета. Пожалуйста обратитесь к волонтеру");
            }
        } else {
            sendMessage(chatId,"Команда не распознана");
        }
    }

    private void sendReportInfo(long chatId) {
        sendMessage(chatId,"""
                Ежедневный отчет должен включать:\n 
                1) Фото животного. Пожалуйста отправьте от 1 до """
                + MAX_FILES + """
                фото.\n 
                2) Текстовый отчет. Пожалуйста отправьте текстовый отчет отдельным от фото сообщением (от 1 до"""
                + MAX_FILES + """
                сообщений. Отчет должен включать: 
                - Рацион животного. 
                - Общее самочувствие и привыкание к новому месту.
                - Изменение в поведении: отказ от старых привычек, приобретение новых.
                """);

//        System.out.println("waiting for update");
//            GetUpdates getUpdates = new GetUpdates().limit(1).offset(0).timeout(2000);
//            GetUpdatesResponse updatesResponse = telegramBot.execute(getUpdates);
//            List<Update> updates = updatesResponse.updates();
//            if (updates != null && updates.isEmpty()) {
//                Update update = updates.get(0);
//                System.out.println(update);
//            }

//        telegramBot.execute(getUpdates, new Callback<GetUpdates, GetUpdatesResponse>() {
//            @Override
//            public void onResponse(GetUpdates request, GetUpdatesResponse response) {
//                List<Update> updates = response.updates();
//                System.out.println(updates);
//            }
//
//            @Override
//            public void onFailure(GetUpdates request, IOException e) {
//
//            }
//        });
//        System.out.println("Method end");

    }

    private void saveTextReport(long chatId, Adoption adoption, String message) throws IOException {
        Path newFilePath = getFilePath(adoption.getId(),"txt");
        if (newFilePath == null){
            sendMessage(chatId,"Не удалось сохранить отчет. Превышено количество сообщений за" + LocalDate.now()
                    +  ". Пожалуйста обратитесь к волонтеру");
            return;
        }
        try(FileWriter out = new FileWriter(newFilePath.toFile());
            BufferedWriter bOut = new BufferedWriter(out, 1024)
        ){
            out.write(message);
        }

        AdoptionReport adoptionReport = new AdoptionReport(adoption, newFilePath.toString(),
                "text", "", LocalDate.now());
        adoptionReportService.save(adoptionReport);
        sendMessage(chatId, "Сообщение добавлено в отчет за " + LocalDate.now());
    }

    private void savePhotoReport(PhotoSize[] photos, long chatId) throws IOException {
        Adoption adoption = adoptionService.findByChatId(chatId);
        if (adoption == null){
            sendMessage(chatId, "Не найдены сведения по вашему усыновлению. " +
                    "Пожалуйста обратитесь к волонтеру.");
            return;
        }
        PhotoSize p = Arrays.stream(photos).max(Comparator.comparingInt(PhotoSize::fileSize)).orElse(null);
        GetFile request = new GetFile(p.fileId());
        GetFileResponse getFileResponse= telegramBot.execute(request);
        com.pengrad.telegrambot.model.File file = getFileResponse.file();

        String fullPath = telegramBot.getFullFilePath(file);
        logger.info("Downloading file: " + fullPath);

        int adoptionId = adoption.getId();
        String extension = getExtension(file.filePath());
        Path newFilePath = getFilePath(adoptionId, extension);
        if (newFilePath == null){
            sendMessage(chatId,"Не удалось сохранить фото в отчет. Превышено количество фото за" + LocalDate.now()
                    +  "Пожалуйста обратитесь к волонтеру");
            return;
        }

        try(InputStream in = new URL(fullPath).openStream();
            OutputStream out = Files.newOutputStream(newFilePath, StandardOpenOption.CREATE_NEW);
            BufferedInputStream bIn = new BufferedInputStream(in, 1024);
            BufferedOutputStream bOut = new BufferedOutputStream(out, 1024)
        ){
            in.transferTo(out);
        }
        logger.info("File downloaded to: " + newFilePath);

        AdoptionReport adoptionReport = new AdoptionReport(adoption, newFilePath.toString(),
                "photo", "text", LocalDate.now());
        adoptionReportService.save(adoptionReport);
        sendMessage(chatId, "Фото добавлено в отчет за " + LocalDate.now());
    }

    private Path getFilePath(int adoptionId, String extension) throws IOException{
        Path parentPath = Path.of("reports", LocalDate.now().toString(), String.valueOf(adoptionId));
        Files.createDirectories(parentPath);
        int fileCounter = 1;
        File newFile = null;
        while (fileCounter <= MAX_FILES) {
            newFile = new File(parentPath.toString(), fileCounter++ + "." + extension);
            System.out.println(newFile);
            if (!newFile.exists()){
                return newFile.toPath();
            }
        }
        return null;
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private void sendMessage(long chatId, String message){
        try {
            telegramBot.execute(new SendMessage(chatId, message));
        } catch (Exception e) {
            logger.error("Message sending failed: chatId = " + chatId + " message: " + message);
            e.printStackTrace();
        }
    }

    private void sendStartMenu(long chatId, String firstName){
        String responseMessage ="Привет, " + firstName + "! Что Вы хотите сделать?\n" +
                INFO + " - Получить информацию о приюте\n" +
                GET_A_DOG + " - Как взять собаку из приюта\n" +
                REPORT + " - Прислать отчет о питомце\n" +
                CALL_A_VOLUNTEER + " - Позвать волонтера";
        sendMessage(chatId, responseMessage);
    }

    private void sendInfoSubmenu(long chatId) {
        String path = addPersonPath + chatId;
        InlineKeyboardButton[][] keyboard = {
                {new InlineKeyboardButton("О приюте").callbackData(ABOUT_SHELTER),
                        new InlineKeyboardButton("Расписание, адрес, \nсхема проезда").callbackData(ADDRESS),
                        new InlineKeyboardButton("Правила безопасности").callbackData(SAFETY_MEASURES)},
                {new InlineKeyboardButton("Оставить контактные данные").url(path),
                        new InlineKeyboardButton("Позвать волонтера").callbackData(CALL_A_VOLUNTEER)},
                {new InlineKeyboardButton("<< Вернуться").callbackData(GO_BACK)}
        };
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboard);
        SendMessage message = new SendMessage(chatId, "Пожалуйста, выберите что Вас интересует:").replyMarkup(inlineKeyboardMarkup);
        telegramBot.execute(message);
    }


    private void sendGetADogSubmenu(long chatId) {
        String path = addPersonPath + chatId;
        InlineKeyboardButton[][] keyboard = {
                {new InlineKeyboardButton("Как взять собаку").callbackData(GET_A_DOG_INFO),
                        new InlineKeyboardButton("Транспортировка").callbackData(TRANSPORTATION),
                        new InlineKeyboardButton("Подготовка дома").callbackData(HOUSE_ACCOMMODATION)},
                {new InlineKeyboardButton("Советы кинолога").callbackData(CYNOLOGIST_ADVICE),
                        new InlineKeyboardButton("Список кинологов").callbackData(CYNOLOGIST_LIST)},
                {new InlineKeyboardButton("Оставить контактные данные").url(path),
                        new InlineKeyboardButton("Позвать волонтера").callbackData(CALL_A_VOLUNTEER)},
                {new InlineKeyboardButton("<< Вернуться").callbackData(GO_BACK)}
        };
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboard);
        SendMessage message = new SendMessage(chatId, "Пожалуйста, выберите что Вас интересует:").replyMarkup(inlineKeyboardMarkup);
        telegramBot.execute(message);
    }

    private void getADogInfo(long chatId){
        InlineKeyboardButton[] keyboard = {new InlineKeyboardButton("Первая встреча").callbackData(MEETING_DOG_INFO),
                        new InlineKeyboardButton("Документы").callbackData(ADOPTION_DOCS),
                        new InlineKeyboardButton("Причины отказа").callbackData(ADOPTION_REFUSAL)};
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboard);
        SendMessage message = new SendMessage(chatId, "Пожалуйста, выберите что Вас интересует:").replyMarkup(inlineKeyboardMarkup);
        telegramBot.execute(message);
    }

    private void sendHouseMenu(long chatId) {
        InlineKeyboardButton[][] keyboard = {
                {new InlineKeyboardButton("Щенок").callbackData(PUPPY_HOUSE_PREPARATION),
                        new InlineKeyboardButton("Взрослая собака").callbackData(DOG_HOUSE_PREPARATION)},
                {new InlineKeyboardButton("Собака с органиченными возможностями").callbackData(DOG_HANDICAP_HOUSE_PREP)},
        };
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboard);
        SendMessage message = new SendMessage(chatId, "Пожалуйста, выберите что Вас интересует:").replyMarkup(inlineKeyboardMarkup);
        telegramBot.execute(message);
    }


    private void sendTransportationIfo(long chatId){
        sendMessage(chatId, "Рекомендации по транспортировке");
    }
    private void sendFirstMeetingInfo(long chatId){
        sendMessage(chatId, "Подготовка к первой встрече");
    }

    private void sendAdoptionDocumentsInfo(long chatId){
        sendMessage (chatId, "Список документов для усыновления");
    }

    private void sendRefusalInfo(long chatId){
        sendMessage(chatId, "Причины отказа в усыновлении");
    }

    private void callAVolunteer(long chatId) {
        sendMessage(chatId, "Call a volunteer");
    }

    private void saveContacts(long chatId) {
        sendMessage(chatId, "Save contacts");
    }

    private void sendSafetyInfo(long chatId) {
        sendMessage(chatId, "Some safety info");
    }

    private void sendAddressData(long chatId) {
        sendMessage(chatId, "Some address info");
    }

    private void sendAboutShelter(long chatId) {
        sendMessage(chatId, "Info about our shelter");

    }

    private void sendDogHandicapHousePrepDetails(long chatId) {
        sendMessage(chatId, "Рекомендации по подготовке дома для собаки с ограниченными возможностями");
    }

    private void sendDogHousePrepDetails(long chatId) {
        sendMessage(chatId, "Рекомендации по подготовке дома для взрослой собаки");
    }

    private void sendPuppyHousePrepDetails(long chatId) {
        sendMessage(chatId, "Рекомендации по подготовке дома для щенка");
    }

    private void sendCynologistList(long chatId) {
        sendMessage(chatId, "Перечень проверенных кинологов");
    }

    private void sendCynologistAdvice(long chatId) {
        sendMessage(chatId, "Рекомендации кинолога");
    }


}
