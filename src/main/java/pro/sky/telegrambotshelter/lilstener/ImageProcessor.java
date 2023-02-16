package pro.sky.telegrambotshelter.lilstener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;
import org.springframework.stereotype.Component;
import pro.sky.telegrambotshelter.model.Adoption;
import pro.sky.telegrambotshelter.model.AdoptionReport;
import pro.sky.telegrambotshelter.service.*;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;

@Component
public class ImageProcessor extends Processor {

    public ImageProcessor(PersonService personService, AdoptionService adoptionService, PetService petService,
                          AdoptionReportService adoptionReportService, UserContextService userContextService) {
        super(personService, petService, adoptionService, adoptionReportService, userContextService);
    }

    public void process(long chatId, PhotoSize[] photos) {
        String lastCommand = userContextService.getLastCommand(chatId);
        if (lastCommand != null && lastCommand.equals(REPORT)) {
            try {
                savePhotoReport(photos, chatId);
            } catch (Exception e) {
                e.printStackTrace();
//                        logger.error("Не удалось сохранить фото. ChatId = " + chatId);
                sendMessage(chatId, "Ошибка сохранения отчета. Пожалуйста обратитесь к волонтеру");
            }
        }
    }

    /**
     * This method threats all incoming images from user as daily report.
     * If the user is not identified as active person with adopted animal on probation, then the photo is ignored,
     * and user is informed about this.
     * This method downloads the photo from telegram server and saves it locally to the
     * {@link ImageProcessor#reportsPath} directory under corresponding date and adoptionId
     * directories where they can later be accessed to be reviewed.
     * The limit is {@value MAX_FILES} messages a day. If the amount is exceeded, the users receives a notification to
     * call a volunteer.
     * If report saving is successful, then it is also saved to the "adoption_repot" table with the use of
     * {@link AdoptionReportService}
     *
     * @param photos images received from user
     * @param chatId telegram chat identification for the user
     * @throws IOException
     */
    private void savePhotoReport(PhotoSize[] photos, long chatId) throws IOException {
        Adoption adoption = adoptionService.findByChatId(chatId);
        if (adoption == null) {
            sendMessage(chatId, "Не найдены сведения по вашему усыновлению. " +
                    "Пожалуйста обратитесь к волонтеру.");
            return;
        }
        PhotoSize p = Arrays.stream(photos).max(Comparator.comparingInt(PhotoSize::fileSize)).orElse(null);
        p.fileSize();
        GetFile request = new GetFile(p.fileId());
        GetFileResponse getFileResponse = telegramBot.execute(request);
        com.pengrad.telegrambot.model.File file = getFileResponse.file();

        String fullPath = telegramBot.getFullFilePath(file);
//        logger.info("Downloading file: " + fullPath);

        int adoptionId = adoption.getId();
        String extension = getExtensionUtil(file.filePath());
        Path newFilePath = getFilePathUtil(adoptionId, extension);
        if (newFilePath == null) {
            sendMessage(chatId, "Не удалось сохранить фото в отчет. Превышено количество фото за " + LocalDate.now()
                    + ". Пожалуйста обратитесь к волонтеру");
            return;
        }

        try (InputStream in = new URL(fullPath).openStream();
             OutputStream out = Files.newOutputStream(newFilePath, StandardOpenOption.CREATE_NEW);
             BufferedInputStream bIn = new BufferedInputStream(in, 1024);
             BufferedOutputStream bOut = new BufferedOutputStream(out, 1024)
        ) {
            in.transferTo(out);
        }
//        logger.info("File downloaded to: " + newFilePath);
        String contentType = Files.probeContentType(newFilePath);
        AdoptionReport adoptionReport = new AdoptionReport(adoption, newFilePath.toString(),
                contentType, LocalDate.now());
        adoptionReportService.save(adoptionReport);
        sendMessage(chatId, "Фото добавлено в отчет за " + LocalDate.now());
    }


}
