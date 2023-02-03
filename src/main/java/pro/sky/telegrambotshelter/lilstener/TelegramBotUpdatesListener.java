package pro.sky.telegrambotshelter.lilstener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.service.AdoptionReportService;
import pro.sky.telegrambotshelter.service.AdoptionService;
import pro.sky.telegrambotshelter.service.PersonService;
import pro.sky.telegrambotshelter.service.PetService;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final PersonService personService;
    private final PetService petService;
    private final AdoptionService adoptionService;
    private final AdoptionReportService adoptionReportService;

    private final TelegramBot telegramBot;

    private final String START = "/start";
    private final String INFO = "/info";
    private final String GET_A_DOG = "/getadog";
    private final String REPORT = "/report";
    private final String CALL_A_VOLUNTEER = "/volunteer";
    private final String ABOUT_SHELTER = "/about";
    private final String ADDRESS = "/address";
    private final String SAFETY_MEASURES = "/safety";
    private final String SAVE_CONTACTS = "/contacts";
    private final String GO_BACK = "/back";

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
                if (message != null) {
                    long chatId = update.message().chat().id();
                    switch (message) {
                        case START:
                            sendStartMenu(chatId, update.message().chat().firstName());
                            break;
                        case INFO:
                            sendInfoSubmenu(chatId);
                            break;
                        case GET_A_DOG:
                            sendGetADogSubmenu(chatId);
                            break;
                        case CALL_A_VOLUNTEER:
                            callAVolunteer(chatId);
                            break;
                        default: {
                            sendMessage(chatId, "Команда не распознана");
                        }
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
                        savaContacts(chatId);
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




    private void sendMessage(long chatId, String message){
        telegramBot.execute(new SendMessage(chatId, message));
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
        InlineKeyboardButton[][] keyboard = {
                {new InlineKeyboardButton("О приюте").callbackData(ABOUT_SHELTER),
                        new InlineKeyboardButton("Расписание, адрес, \nсхема проезда").callbackData(ADDRESS),
                        new InlineKeyboardButton("Правила безопасности").callbackData(SAFETY_MEASURES)},
                {new InlineKeyboardButton("Оставить контактные данные").callbackData(SAVE_CONTACTS),
                        new InlineKeyboardButton("Позвать волонтера").callbackData(CALL_A_VOLUNTEER)},
                {new InlineKeyboardButton("<< Вернуться").callbackData(GO_BACK)}
        };
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboard);
        SendMessage message = new SendMessage(chatId, "Please select").replyMarkup(inlineKeyboardMarkup);
        telegramBot.execute(message);
    }

    private final String GET_A_DOG_INFO = "/getadoginfo";
    private final String MEETING_DOG_INFO = "/meetingdog";
    private final String ADOPTION_DOCS ="/adoptiondocs";
    private final String ADOPTION_REFUSAL="/adoptionrefusal";

    private final String TRANSPORTATION = "/transportation";

    private final String HOUSE_ACCOMMODATION = "/houseaccommodation";
    private final String PUPPY_HOUSE_PREPARATION = "/puppyhouse";
    private final String DOG_HOUSE_PREPARATION = "/doghouse";
    private final String DOG_HANDICAP_HOUSE_PREP="/doghandicap";

    private final String CYNOLOGIST_ADVICE ="/cynologadvice";
    private final String CYNOLOGIST_LIST="/cynologlist";


    private void sendGetADogSubmenu(long chatId) {
        InlineKeyboardButton[][] keyboard = {
                {new InlineKeyboardButton("Как взять собаку").callbackData(GET_A_DOG_INFO),
                        new InlineKeyboardButton("Транспортировка").callbackData(TRANSPORTATION),
                        new InlineKeyboardButton("Подготовка дома").callbackData(HOUSE_ACCOMMODATION)},
                {new InlineKeyboardButton("Советы кинолога").callbackData(CYNOLOGIST_ADVICE),
                        new InlineKeyboardButton("Список кинологов").callbackData(CYNOLOGIST_LIST)},
                {new InlineKeyboardButton("Оставить контактные данные").callbackData(SAVE_CONTACTS),
                        new InlineKeyboardButton("Позвать волонтера").callbackData(CALL_A_VOLUNTEER)},
                {new InlineKeyboardButton("<< Вернуться").callbackData(GO_BACK)}
        };
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboard);
        SendMessage message = new SendMessage(chatId, "Please select").replyMarkup(inlineKeyboardMarkup);
        telegramBot.execute(message);
    }

    private void getADogInfo(long chatId){
        InlineKeyboardButton[] keyboard = {new InlineKeyboardButton("Первая встреча").callbackData(MEETING_DOG_INFO),
                        new InlineKeyboardButton("Документы").callbackData(ADOPTION_DOCS),
                        new InlineKeyboardButton("Причины отказа").callbackData(ADOPTION_REFUSAL)};
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboard);
        SendMessage message = new SendMessage(chatId, "Please select").replyMarkup(inlineKeyboardMarkup);
        telegramBot.execute(message);
    }

    private void sendHouseMenu(long chatId) {
        InlineKeyboardButton[][] keyboard = {
                {new InlineKeyboardButton("Щенок").callbackData(PUPPY_HOUSE_PREPARATION),
                        new InlineKeyboardButton("Взрослая собака").callbackData(DOG_HOUSE_PREPARATION)},
                {new InlineKeyboardButton("Собака с органиченными возможностями").callbackData(DOG_HANDICAP_HOUSE_PREP)},
        };
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboard);
        SendMessage message = new SendMessage(chatId, "Please select").replyMarkup(inlineKeyboardMarkup);
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

    private void savaContacts(long chatId) {
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
