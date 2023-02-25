package pro.sky.telegrambotshelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import pro.sky.telegrambotshelter.model.Adoption;
import pro.sky.telegrambotshelter.model.PetType;
import pro.sky.telegrambotshelter.service.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * This abstract class is a parent for different type telegram message processors.
 * It contains common constants, fields and methods, such as message sending methods, or util methods
 * @author Ekaterina Gorbacheva
 */
public abstract class Processor {

    /**
     * Maximum quantity of report files per day.
     * This amount of pictures will be accepted for a daily report.
     * In addition, the same amount of text messages will be accepted for a daily report.
     */
    protected static final int MAX_FILES = 10;

    @Value("${reports.path.dir}")
    protected String reportsPath;
    @Value("${newperson.url}")
    protected String addPersonUrl;
    @Value("${cynologist.advice.url}")
    protected String cynologistAdviceUrl;
    @Value("${volunteer.chatId}")
    protected long volunteerChatId;

    private ResourceBundle catBundle;
    private ResourceBundle dogBundle;
    protected ResourceBundle bundle;

    protected final PersonDogService personDogService;
    protected final PersonCatService personCatService;
    protected final PetService petService;
    protected final AdoptionDogService adoptionDogService;
    protected final AdoptionCatService adoptionCatService;
    protected final AdoptionReportDogService adoptionReportDogService;
    protected final AdoptionReportCatService adoptionReportCatService;
    protected final UserContextService userContextService;
    protected Logger logger;
    protected TelegramBot telegramBot;

    protected static final String DOG = "DOG";
    protected static final String CAT = "CAT";
    protected static final String START = "/start";
    protected static final String INFO = "/info";
    protected static final String GET_A_PET = "/getapet";
    protected static final String REPORT = "/report";
    protected static final String CALL_A_VOLUNTEER = "/volunteer";
    protected static final String TEXT_ABOUT_SHELTER = "/about";
    protected static final String TEXT_ADDRESS = "/address";
    protected static final String TEXT_SAFETY = "/safety";
    protected static final String ORDER_PASS = "/orderpass";
    protected static final String GO_BACK = "/back";
    protected static final String GET_A_PET_INFO = "/getapetinfo";
    protected static final String TEXT_MEETING_A_PET = "/meetingpet";
    protected static final String TEXT_ADOPTION_DOCS ="/adoptiondocs";
    protected static final String TEXT_ADOPTION_REFUSAL ="/adoptionrefusal";
    protected static final String TEXT_TRANSPORTATION = "/transportation";
    protected static final String HOUSE_ACCOMMODATION = "/houseaccommodation";
    protected static final String TEXT_CUB_HOUSE_PREPARATION = "/cubhouse";
    protected static final String TEXT_ADULT_HOUSE_PREPARATION = "/adulthouse";
    protected static final String TEXT_HANDICAP_HOUSE_PREP ="/handicap";
    protected static final String TEXT_CYNOLOGIST_LIST ="/cynologlist";
    protected static final String SEND_WARNING="/warning";

    public Processor(PersonDogService personDogService, PersonCatService personCatService,
                     AdoptionDogService adoptionDogService, AdoptionCatService adoptionCatService,
                     AdoptionReportDogService adoptionReportDogService, AdoptionReportCatService adoptionReportCatService,
                     PetService petService, UserContextService userContextService) {
        this.personDogService = personDogService;
        this.personCatService = personCatService;
        this.petService = petService;
        this.adoptionReportDogService = adoptionReportDogService;
        this.adoptionReportCatService = adoptionReportCatService;
        this.adoptionDogService = adoptionDogService;
        this.adoptionCatService = adoptionCatService;
        this.userContextService = userContextService;
        dogBundle = ResourceBundle.getBundle("texts_dog");
        catBundle = ResourceBundle.getBundle("texts_cat");
        bundle = ResourceBundle.getBundle("texts_common");
    }
    
    public String getText(String key, PetType petType){
        if (petType == PetType.CAT){
            return catBundle.getString(key);
        } else if (petType == PetType.DOG){
            return dogBundle.getString(key);
        }
        return null;
    }

    public void setTelegramBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }
    protected void sendStartMenu(long chatId){
        String responseMessage ="Привет! Что Вы хотите сделать?\n" +
                INFO + " - Получить информацию о приюте\n" +
                GET_A_PET + " - Как взять собаку из приюта\n" +
                REPORT + " - Прислать отчет о питомце\n" +
                CALL_A_VOLUNTEER + " - Позвать волонтера";
        sendMessage(chatId, responseMessage);
    }

    protected void sendMessage(long chatId, String message){
        sendMessage(new SendMessage(chatId, message));
    }

    protected void sendMessage(SendMessage sendMessage){
        try {
            telegramBot.execute(sendMessage);
        } catch (Exception e) {
            logger.error("Message sending failed: + sendMessage: " + sendMessage);
            e.printStackTrace();
        }
    }

    /**
     * This is an util method to compose a file path and name for adoption report files.
     * Parent path is composed of {@link Processor#reportsPath} directory, then current date directory,
     * then adoptionId directory.
     * For files naming it uses consequential numeration of files from 1 to {@value MAX_FILES} plus extension got from
     * the {@link Processor#getExtensionUtil(String)}
     * @param adoptionId an identification of {@link Adoption} object relevant to current user
     * @param extension file extension ('.txt' for text reports and actual file extention for images received from users)
     * @return Path for a new report file, or null if the amount of files exceeds {@value MAX_FILES}
     * @throws IOException
     */
    protected Path getFilePathUtil(int adoptionId, String extension, String petType) throws IOException{
        Path parentPath = Path.of(reportsPath, LocalDate.now().toString(), petType, String.valueOf(adoptionId));
        Files.createDirectories(parentPath);
        int fileCounter = 1;
        File newFile;
        while (fileCounter <= MAX_FILES) {
            newFile = new File(parentPath.toString(), fileCounter++ + "." + extension);
            System.out.println(newFile);
            if (!newFile.exists()){
                return newFile.toPath();
            }
        }
        return null;
    }

    protected String getExtensionUtil(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    protected void callAVolunteer(long chatId) {
        sendMessage(chatId, "Опишите ваш запрос, я отправлю его волонтеру");
    }
}
