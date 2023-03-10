package pro.sky.telegrambotshelter.listener;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambotshelter.service.UserContextService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TelegramBotUpdatesListenerTest {
    @Mock
    private UserContextService userContextService;
    @Mock
    private TextMessageProcessor textMessageProcessor;
    @Mock
    private ImageProcessor imageProcessor;
    @Mock
    private CallbackQueryProcessor callbackQueryProcessor;
    @Mock
    private TelegramBot telegramBot;
    @Mock
    private ScheduledJobsExecutor scheduledJobsExecutor;
    @InjectMocks
    private TelegramBotUpdatesListener telegramBotUpdatesListener;

    @Test
    public void processTextMessageTest() throws URISyntaxException, IOException {
        String json = Files.readString(
                Paths.get(TelegramBotUpdatesListenerTest.class.getResource("text_update.json").toURI()));
        Update update = getUpdate(json, "/start");
        doNothing().when(textMessageProcessor).process(anyLong(), anyString(), anyInt());

        telegramBotUpdatesListener.process(Collections.singletonList(update));
        verify(textMessageProcessor, atLeastOnce()).process(update.message().chat().id(), update.message().text(),
                update.message().messageId());
    }
    @Test
    public void processImageTest() throws URISyntaxException, IOException {
        String json = Files.readString(
                Paths.get(TelegramBotUpdatesListenerTest.class.getResource("image_update.json").toURI()));
        Update update = getUpdate(json);
        doNothing().when(imageProcessor).process(anyLong(),any());

        telegramBotUpdatesListener.process(Collections.singletonList(update));
        verify(imageProcessor, atLeastOnce()).process(update.message().chat().id(), update.message().photo());
    }
    @Test
    public void processCallBackQueryTest() throws URISyntaxException, IOException {
        String json = Files.readString(
                Paths.get(TelegramBotUpdatesListenerTest.class.getResource("callbackquery_update.json").toURI()));
        Update update = getUpdate(json);
        System.out.println(update);
        doNothing().when(callbackQueryProcessor).process(anyLong(),anyString());

        telegramBotUpdatesListener.process(Collections.singletonList(update));
        verify(callbackQueryProcessor, atLeastOnce()).process(update.callbackQuery().message().chat().id(),
                update.callbackQuery().data());
    }

    private Update getUpdate(String json, String replaced) {
        return BotUtils.fromJson(json.replace("%command%", replaced), Update.class);
    }
    private Update getUpdate(String json) {
        return BotUtils.fromJson(json, Update.class);
    }

}
