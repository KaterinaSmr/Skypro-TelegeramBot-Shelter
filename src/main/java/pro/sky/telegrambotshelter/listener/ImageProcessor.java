package pro.sky.telegrambotshelter.listener;

import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pro.sky.telegrambotshelter.model.*;
import pro.sky.telegrambotshelter.model.adoption.Adoption;
import pro.sky.telegrambotshelter.model.adoption.AdoptionCat;
import pro.sky.telegrambotshelter.model.adoption.AdoptionDog;
import pro.sky.telegrambotshelter.model.adoptionReport.AdoptionReportCat;
import pro.sky.telegrambotshelter.model.adoptionReport.AdoptionReportDog;
import pro.sky.telegrambotshelter.service.*;
import pro.sky.telegrambotshelter.service.adoption.AdoptionCatService;
import pro.sky.telegrambotshelter.service.adoption.AdoptionDogService;
import pro.sky.telegrambotshelter.service.adoptionReport.AdoptionReportCatService;
import pro.sky.telegrambotshelter.service.adoptionReport.AdoptionReportDogService;
import pro.sky.telegrambotshelter.service.adoptionReport.AdoptionReportService;
import pro.sky.telegrambotshelter.service.person.PersonCatService;
import pro.sky.telegrambotshelter.service.person.PersonDogService;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;

/**
 * This class is for processing images received from users.
 *
 * @author Ekaterina Gorbacheva
 */
@Component
public class ImageProcessor extends Processor {

    public ImageProcessor(PersonDogService personDogService, PersonCatService personCatService,
                          AdoptionDogService adoptionDogService, AdoptionCatService adoptionCatService,
                          AdoptionReportDogService adoptionReportDogService, AdoptionReportCatService adoptionReportCatService,
                          PetService petService, UserContextService userContextService) {
        super(personDogService, personCatService, adoptionDogService, adoptionCatService,
                adoptionReportDogService, adoptionReportCatService, petService, userContextService);
        logger = LoggerFactory.getLogger(ImageProcessor.class);
    }

    /**
     * This method checks the last command sent by user. The image received from user is interpreted as daily
     * photo report only if the last command sent from user was a request to send daily report, otherwise, the
     * image is ignored. <br>
     * The last command is determind with the method {@link UserContextService#getLastCommand(long)} <br>
     *
     * @param chatId telegram chat Id
     * @param photos the array of photo metadata received from {@link TelegramBotUpdatesListener}
     */
    public void process(long chatId, PhotoSize[] photos) {
        String lastCommand = userContextService.getLastCommand(chatId);
        if (lastCommand != null && lastCommand.equals(REPORT)) {
            try {
                savePhotoReport(photos, chatId);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Не удалось сохранить фото. ChatId = " + chatId);
                sendMessage(chatId, "Ошибка сохранения отчета. Пожалуйста обратитесь к волонтеру");
            }
        }
    }

    /**
     * This method saves daily photo reports from adoptive people on probation.
     * If the user is not identified as active person with adopted animal on probation, then the photo is ignored,
     * and user is informed about this.
     * This method downloads the photo from telegram server and saves it locally to the
     * {@link ImageProcessor#reportsPath} directory under corresponding date and adoptionId
     * directories where they can later be accessed to be reviewed.
     * The limit is {@value MAX_FILES} messages a day. If the amount is exceeded, the users receives a notification to
     * call a volunteer.
     * If report saving is successful, then it is also saved to the "adoption_report_dog" or "adoption_report_cat" table
     * with the use of {@link AdoptionReportService}. The correct shelter (Cat shelter or Dog shelter) is determined
     * based on the chatId of a user. It checks which shelter has an adoption record with this chatId
     *
     * @param photos images received from user
     * @param chatId telegram chat identification for the user
     * @throws IOException
     */
    private void savePhotoReport(PhotoSize[] photos, long chatId) throws IOException {
        Adoption adoption = adoptionDogService.findByChatId(chatId);
        PetType petType = PetType.DOG;
        if (adoption == null) {
            adoption = adoptionCatService.findByChatId(chatId);
            petType = PetType.CAT;
        }
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
        logger.info("Downloading file: " + fullPath);

        int adoptionId = adoption.getId();
        String extension = getExtensionUtil(file.filePath());
        Path newFilePath = getFilePathUtil(adoptionId, extension, petType.toString());
        if (newFilePath == null) {
            logger.error("Error saving file for adoption: " + adoption.getId());
            sendMessage(chatId, "Не удалось сохранить фото в отчет. Превышено количество фото за " + LocalDate.now()
                    + ". Пожалуйста обратитесь к волонтеру");
            return;
        }
        try (InputStream in = new URL(fullPath).openStream();
             OutputStream out = Files.newOutputStream(newFilePath, StandardOpenOption.CREATE_NEW);
             BufferedInputStream bIn = new BufferedInputStream(in, 1024);
             BufferedOutputStream bOut = new BufferedOutputStream(out, 1024)
        ) {
            bIn.transferTo(bOut);
        } catch (Exception e){
            e.printStackTrace();
            logger.error("Failed file download. " + fullPath);
        }
        logger.info("File downloaded to: " + newFilePath);
        String contentType = Files.probeContentType(newFilePath);
        if (petType == PetType.DOG) {
            AdoptionReportDog adoptionReport = new AdoptionReportDog((AdoptionDog) adoption, newFilePath.toString(),
                    contentType, LocalDate.now());
            adoptionReportDogService.save(adoptionReport);
        } else {
            AdoptionReportCat adoptionReport = new AdoptionReportCat((AdoptionCat) adoption, newFilePath.toString(),
                    contentType, LocalDate.now());
            adoptionReportCatService.save(adoptionReport);
        }
        sendMessage(chatId, "Фото добавлено в отчет за " + LocalDate.now());
    }
}
