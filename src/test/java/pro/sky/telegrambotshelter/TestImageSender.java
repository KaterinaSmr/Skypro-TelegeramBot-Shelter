package pro.sky.telegrambotshelter;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pro.sky.telegrambotshelter.listener.TelegramBotUpdatesListenerTest;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/testimage")
public class TestImageSender {
    @GetMapping
    public ResponseEntity sendTestImage(HttpServletResponse response) throws IOException, URISyntaxException {
        System.out.println("test image method called");
        Path filePath = Paths.get(TestImageSender.class.getResource("test.jpg").toURI());
        try(InputStream in = Files.newInputStream(filePath);
            OutputStream out = response.getOutputStream();
            BufferedInputStream bIn = new BufferedInputStream(in, 1024);
            BufferedOutputStream bOut = new BufferedOutputStream(out, 1024)){
            response.setStatus(200);
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            response.setContentLength((int) Files.size(filePath));
            bIn.transferTo(bOut);
        }
        return ResponseEntity.ok().build();
    }
}
