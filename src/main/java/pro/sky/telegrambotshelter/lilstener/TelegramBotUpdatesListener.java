package pro.sky.telegrambotshelter.lilstener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.service.PersonService;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final PersonService personService;
    private final TelegramBot telegramBot;

    private final String START = "/start";
    private final String INFO = "/info";
    private final String GETADOG = "/getadog";
    private final String REPORT = "/report";
    private final String CALLAVOLUNTEER = "/volunteer";

    public TelegramBotUpdatesListener(PersonService personService, TelegramBot telegramBot) {
        this.personService = personService;
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
            String message = update.message().text();
            long chatId = update.message().chat().id();
            if (message != null) {
                switch (message){
                    case START:{
                        respondStart(chatId, update.message().chat().firstName());
                        break;
                    }
                    default: {
                        sendMessage(chatId, "Команда не распознана");
                    }
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void sendMessage(long chatId, String message){
        telegramBot.execute(new SendMessage(chatId, message));
    }

    private void respondStart(long chatId, String firstName){
        String responseMessage ="Привет, " + firstName + "! Что Вы хотите сделать?\n" +
                INFO + " - Получить информацию о приюте\n" +
                GETADOG + " - Как взять собаку из приюта\n" +
                REPORT + " - Прислать отчет о питомце\n" +
                CALLAVOLUNTEER + " - Позвать волонтера";
        sendMessage(chatId, responseMessage);
    }
}
