package pro.sky.telegrambotshelter.lilstener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.ForwardMessage;
import com.pengrad.telegrambot.request.SendContact;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Value;
import pro.sky.telegrambotshelter.model.Adoption;
import pro.sky.telegrambotshelter.service.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;

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

    protected ResourceBundle bundle;

    protected final PersonService personService;
    protected final PetService petService;
    protected final AdoptionService adoptionService;
    protected final AdoptionReportService adoptionReportService;
    protected final UserContextService userContextService;
    protected TelegramBot telegramBot;

    protected static final String START = "/start";
    protected static final String INFO = "/info";
    protected static  final String GET_A_DOG = "/getadog";
    protected static  final String REPORT = "/report";
    protected static  final String CALL_A_VOLUNTEER = "/volunteer";
    protected static  final String TEXT_ABOUT_SHELTER = "/about";
    protected static  final String TEXT_ADDRESS = "/address";
    protected static  final String TEXT_SAFETY = "/safety";
    protected static  final String SAVE_CONTACTS = "/contacts";
    protected static  final String GO_BACK = "/back";
    protected static  final String GET_A_DOG_INFO = "/getadoginfo";
    protected static  final String TEXT_MEETING_A_DOG = "/meetingdog";
    protected static  final String TEXT_ADOPTION_DOCS ="/adoptiondocs";
    protected static  final String TEXT_ADOPTION_REFUSAL ="/adoptionrefusal";
    protected static  final String TEXT_TRANSPORTATION = "/transportation";
    protected static  final String HOUSE_ACCOMMODATION = "/houseaccommodation";
    protected static  final String TEXT_PUPPY_HOUSE_PREPARATION = "/puppyhouse";
    protected static  final String TEXT_DOG_HOUSE_PREPARATION = "/doghouse";
    protected static  final String TEXT_DOG_HANDICAP_HOUSE_PREP ="/doghandicap";
    protected static  final String TEXT_CYNOLOGIST_LIST ="/cynologlist";

    public Processor(PersonService personService, PetService petService, AdoptionService adoptionService,
                     AdoptionReportService adoptionReportService, UserContextService userContextService) {
        this.personService = personService;
        this.petService = petService;
        this.adoptionReportService = adoptionReportService;
        this.adoptionService = adoptionService;
        this.userContextService = userContextService;
        bundle = ResourceBundle.getBundle("texts", Locale.ROOT);
    }

    public void setTelegramBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }
    protected void sendStartMenu(long chatId){
        String responseMessage ="Привет! Что Вы хотите сделать?\n" +
                INFO + " - Получить информацию о приюте\n" +
                GET_A_DOG + " - Как взять собаку из приюта\n" +
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
//            logger.error("Message sending failed: + sendMessage: " + sendMessage);
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
    protected Path getFilePathUtil(int adoptionId, String extension) throws IOException{
        Path parentPath = Path.of(reportsPath, LocalDate.now().toString(), String.valueOf(adoptionId));
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
