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
import pro.sky.telegrambotshelter.service.*;

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

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final UserContextService userContextService;
    private final TextMessageProcessor textMessageProcessor;
    private final ImageProcessor imageProcessor;
    private final CallbackQueryProcessor callbackQueryProcessor;
    private final TelegramBot telegramBot;

    public TelegramBotUpdatesListener(UserContextService userContextService, TextMessageProcessor textMessageProcessor, ImageProcessor imageProcessor, CallbackQueryProcessor callbackQueryProcessor, TelegramBot telegramBot) {
        this.userContextService = userContextService;
        this.textMessageProcessor = textMessageProcessor;
        this.imageProcessor = imageProcessor;
        this.callbackQueryProcessor = callbackQueryProcessor;
        this.telegramBot = telegramBot;
        textMessageProcessor.setTelegramBot(telegramBot);
        callbackQueryProcessor.setTelegramBot(telegramBot);
        imageProcessor.setTelegramBot(telegramBot);
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
