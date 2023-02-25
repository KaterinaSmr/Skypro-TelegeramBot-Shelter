package pro.sky.telegrambotshelter.listener;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendLocation;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pro.sky.telegrambotshelter.model.PetType;
import pro.sky.telegrambotshelter.service.*;

/**
 * This class is for processing Callback queries from users.
 * @author Ekaterina Gorbacheva
 */
@Component
public class CallbackQueryProcessor extends Processor {

    public CallbackQueryProcessor(PersonDogService personDogService, PersonCatService personCatService,
                                  AdoptionDogService adoptionDogService, AdoptionCatService adoptionCatService,
                                  AdoptionReportDogService adoptionReportDogService, AdoptionReportCatService adoptionReportCatService,
                                  PetService petService, UserContextService userContextService) {
        super(personDogService, personCatService, adoptionDogService, adoptionCatService,
                adoptionReportDogService, adoptionReportCatService, petService, userContextService);
        logger = LoggerFactory.getLogger(CallbackQueryProcessor.class);
    }

    public void process(long chatId, String data) {
        userContextService.save(chatId, data);
        PetType petType = userContextService.getPetType(chatId);
        switch (data) {
            case DOG:
            case CAT:
                userContextService.save(chatId, data, PetType.valueOf(data));
                processShelterSelection(chatId);
                break;
            case CALL_A_VOLUNTEER:
                callAVolunteer(chatId);
                break;
            case GO_BACK:
                sendStartMenu(chatId);
                break;
            case GET_A_PET_INFO:
                getAPetInfo(chatId);
                break;
            case HOUSE_ACCOMMODATION:
                sendHouseMenu(chatId, petType);
                break;
            case TEXT_ADDRESS:
                sendLocation(chatId, petType);
            case ORDER_PASS:
            case TEXT_ABOUT_SHELTER:
            case TEXT_SAFETY:
            case TEXT_MEETING_A_PET:
            case TEXT_ADOPTION_DOCS:
            case TEXT_ADOPTION_REFUSAL:
            case TEXT_TRANSPORTATION:
            case TEXT_CUB_HOUSE_PREPARATION:
            case TEXT_ADULT_HOUSE_PREPARATION:
            case TEXT_HANDICAP_HOUSE_PREP:
            case TEXT_CYNOLOGIST_LIST:{
                String message = getText(data, petType);
                sendMessage(chatId, message);
            }
        }
    }

    private void processShelterSelection(long chatId) {
        if (chatId == volunteerChatId) {
            sendMessage(chatId, "Пожалуйста введите adoption id, по которому необходимо отправить" +
                    " уведомление о недостаточной частоте и детальности отчетов");
            userContextService.save(chatId, SEND_WARNING);
        } else {
            sendStartMenu(chatId);
        }
    }

    private void sendLocation(long chatId, PetType petType){
        SendLocation sendLocation = new SendLocation(chatId, 51.176379f, 71.335729f);
        if (petType == PetType.CAT) {
            sendLocation = new SendLocation(chatId, 52.176379f, 70.335729f);
        }
        try {
            telegramBot.execute(sendLocation);
        } catch (Exception e) {
            logger.error("Unable to send location to :" + chatId);
            e.printStackTrace();
        }
    }

    private void getAPetInfo(long chatId) {
        InlineKeyboardButton[] keyboard = {new InlineKeyboardButton("Первая встреча").callbackData(TEXT_MEETING_A_PET),
                new InlineKeyboardButton("Документы").callbackData(TEXT_ADOPTION_DOCS),
                new InlineKeyboardButton("Причины отказа").callbackData(TEXT_ADOPTION_REFUSAL)};
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboard);
        sendMessage(new SendMessage(chatId, "Пожалуйста, выберите что Вас интересует:").replyMarkup(inlineKeyboardMarkup));
    }

    private void sendHouseMenu(long chatId, PetType petType) {
        InlineKeyboardButton[][] keyboard = {
                {new InlineKeyboardButton(petType == PetType.CAT ? "Котенок" :"Щенок").callbackData(TEXT_CUB_HOUSE_PREPARATION),
                        new InlineKeyboardButton(petType == PetType.CAT ?"Взрослый кот" : "Взрослая собака").callbackData(TEXT_ADULT_HOUSE_PREPARATION)},
                {new InlineKeyboardButton((petType == PetType.CAT ? "Кошка" : "Собака")
                        + " с органиченными возможностями").callbackData(TEXT_HANDICAP_HOUSE_PREP)},
        };
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboard);
        sendMessage(new SendMessage(chatId, "Пожалуйста, выберите что Вас интересует:").replyMarkup(inlineKeyboardMarkup));
    }

}
