package pro.sky.telegrambotshelter.lilstener;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendLocation;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import pro.sky.telegrambotshelter.service.*;

/**
 * This class is for processing Callback queries from users.
 * @author Ekaterina Gorbacheva
 */
@Component
public class CallbackQueryProcessor extends Processor {

    public CallbackQueryProcessor(PersonService personService, AdoptionService adoptionService, PetService petService,
                                  AdoptionReportService adoptionReportService, UserContextService userContextService) {
        super(personService, petService, adoptionService, adoptionReportService, userContextService);
    }

    public void process(long chatId, String data) {
        userContextService.save(chatId, data);
        switch (data) {
            case SAVE_CONTACTS:
                sendMessage(chatId, "Save contacts");
                break;
            case CALL_A_VOLUNTEER:
                callAVolunteer(chatId);
                break;
            case GO_BACK:
                sendStartMenu(chatId);
                break;
            case GET_A_DOG_INFO:
                getADogInfo(chatId);
                break;
            case HOUSE_ACCOMMODATION:
                sendHouseMenu(chatId);
                break;
            case TEXT_ADDRESS:
                SendLocation sendLocation = new SendLocation(chatId, 51.176379f,71.335729f);
                telegramBot.execute(sendLocation);
            case TEXT_ABOUT_SHELTER:
            case TEXT_SAFETY:
            case TEXT_MEETING_A_DOG:
            case TEXT_ADOPTION_DOCS:
            case TEXT_ADOPTION_REFUSAL:
            case TEXT_TRANSPORTATION:
            case TEXT_PUPPY_HOUSE_PREPARATION:
            case TEXT_DOG_HOUSE_PREPARATION:
            case TEXT_DOG_HANDICAP_HOUSE_PREP :
            case TEXT_CYNOLOGIST_LIST:{
                String message = bundle.getString(data);
                sendMessage(chatId, message);
            }
        }
    }

    private void getADogInfo(long chatId) {
        InlineKeyboardButton[] keyboard = {new InlineKeyboardButton("Первая встреча").callbackData(TEXT_MEETING_A_DOG),
                new InlineKeyboardButton("Документы").callbackData(TEXT_ADOPTION_DOCS),
                new InlineKeyboardButton("Причины отказа").callbackData(TEXT_ADOPTION_REFUSAL)};
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboard);
        sendMessage(new SendMessage(chatId, "Пожалуйста, выберите что Вас интересует:").replyMarkup(inlineKeyboardMarkup));
    }

    private void sendHouseMenu(long chatId) {
        InlineKeyboardButton[][] keyboard = {
                {new InlineKeyboardButton("Щенок").callbackData(TEXT_PUPPY_HOUSE_PREPARATION),
                        new InlineKeyboardButton("Взрослая собака").callbackData(TEXT_DOG_HOUSE_PREPARATION)},
                {new InlineKeyboardButton("Собака с органиченными возможностями").callbackData(TEXT_DOG_HANDICAP_HOUSE_PREP)},
        };
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboard);
        sendMessage(new SendMessage(chatId, "Пожалуйста, выберите что Вас интересует:").replyMarkup(inlineKeyboardMarkup));
    }

}
