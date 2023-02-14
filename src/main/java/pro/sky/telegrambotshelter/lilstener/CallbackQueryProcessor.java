package pro.sky.telegrambotshelter.lilstener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.telegrambotshelter.service.*;

@Component
public class CallbackQueryProcessor extends Processor {

    public CallbackQueryProcessor(PersonService personService, AdoptionService adoptionService, PetService petService,
                                  AdoptionReportService adoptionReportService, UserContextService userContextService) {
        super(personService, petService, adoptionService, adoptionReportService, userContextService);
    }

    public void setTelegramBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void process(long chatId, String data) {
        userContextService.save(chatId, data);
        switch (data) {
            case ABOUT_SHELTER -> sendMessage(chatId, "Info about our shelter");
            case ADDRESS -> sendMessage(chatId, "Some address info");
            case SAFETY_MEASURES -> sendMessage(chatId, "Some safety info");
            case SAVE_CONTACTS -> sendMessage(chatId, "Save contacts");
            case CALL_A_VOLUNTEER -> callAVolunteer(chatId);
            case GO_BACK -> sendStartMenu(chatId);
            case GET_A_DOG_INFO -> getADogInfo(chatId);
            case MEETING_DOG_INFO -> sendMessage(chatId, "Подготовка к первой встрече");
            case ADOPTION_DOCS -> sendMessage(chatId, "Список документов для усыновления");
            case ADOPTION_REFUSAL -> sendMessage(chatId, "Причины отказа в усыновлении");
            case TRANSPORTATION -> sendMessage(chatId, "Рекомендации по транспортировке");
            case HOUSE_ACCOMMODATION -> sendHouseMenu(chatId);
            case PUPPY_HOUSE_PREPARATION -> sendMessage(chatId, "Рекомендации по подготовке дома для щенка");
            case DOG_HOUSE_PREPARATION -> sendMessage(chatId, "Рекомендации по подготовке дома для взрослой собаки");
            case DOG_HANDICAP_HOUSE_PREP ->
                    sendMessage(chatId, "Рекомендации по подготовке дома для собаки с ограниченными возможностями");
            case CYNOLOGIST_ADVICE -> sendMessage(chatId, "Рекомендации кинолога");
            case CYNOLOGIST_LIST -> sendMessage(chatId, "Перечень проверенных кинологов");
        }
    }

    private void getADogInfo(long chatId) {
        InlineKeyboardButton[] keyboard = {new InlineKeyboardButton("Первая встреча").callbackData(MEETING_DOG_INFO),
                new InlineKeyboardButton("Документы").callbackData(ADOPTION_DOCS),
                new InlineKeyboardButton("Причины отказа").callbackData(ADOPTION_REFUSAL)};
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboard);
        sendMessage(new SendMessage(chatId, "Пожалуйста, выберите что Вас интересует:").replyMarkup(inlineKeyboardMarkup));
    }

    private void sendHouseMenu(long chatId) {
        InlineKeyboardButton[][] keyboard = {
                {new InlineKeyboardButton("Щенок").callbackData(PUPPY_HOUSE_PREPARATION),
                        new InlineKeyboardButton("Взрослая собака").callbackData(DOG_HOUSE_PREPARATION)},
                {new InlineKeyboardButton("Собака с органиченными возможностями").callbackData(DOG_HANDICAP_HOUSE_PREP)},
        };
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboard);
        sendMessage(new SendMessage(chatId, "Пожалуйста, выберите что Вас интересует:").replyMarkup(inlineKeyboardMarkup));
    }

}
