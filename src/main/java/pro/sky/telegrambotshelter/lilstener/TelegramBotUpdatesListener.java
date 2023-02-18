package pro.sky.telegrambotshelter.lilstener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambotshelter.service.UserContextService;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * This class listens to updates in telegram and redirects them to the right message processor
 * depending on the incoming message type
 * @author Ekaterina Gorbacheva
 */
@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final UserContextService userContextService;
    private final TextMessageProcessor textMessageProcessor;
    private final ImageProcessor imageProcessor;
    private final CallbackQueryProcessor callbackQueryProcessor;
    private final TelegramBot telegramBot;
    private final ScheduledJobsExecutor scheduledJobsExecutor;

    public TelegramBotUpdatesListener(UserContextService userContextService, TextMessageProcessor textMessageProcessor, ImageProcessor imageProcessor, CallbackQueryProcessor callbackQueryProcessor, TelegramBot telegramBot, ScheduledJobsExecutor scheduledJobsExecutor) {
        this.userContextService = userContextService;
        this.textMessageProcessor = textMessageProcessor;
        this.imageProcessor = imageProcessor;
        this.callbackQueryProcessor = callbackQueryProcessor;
        this.telegramBot = telegramBot;
        this.scheduledJobsExecutor = scheduledJobsExecutor;
        textMessageProcessor.setTelegramBot(telegramBot);
        callbackQueryProcessor.setTelegramBot(telegramBot);
        imageProcessor.setTelegramBot(telegramBot);
        scheduledJobsExecutor.setTelegramBot(telegramBot);
    }

    @PostConstruct
    public void init(){
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update " + update);
            if (update.callbackQuery() != null){
                String data = update.callbackQuery().data();
                long chatId = update.callbackQuery().message().chat().id();
                callbackQueryProcessor.process(chatId, data);
            } else if (update.message() != null) {
                String message = update.message().text();
                long chatId = update.message().chat().id();
                int messageId = update.message().messageId();
                if (message != null) {
                   textMessageProcessor.process(chatId, message, messageId);
                } else if (update.message().photo()!=null){
                    PhotoSize[] photos = update.message().photo();
                    imageProcessor.process(chatId, photos);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
