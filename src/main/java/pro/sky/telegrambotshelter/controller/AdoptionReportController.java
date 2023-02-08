package pro.sky.telegrambotshelter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.sky.telegrambotshelter.model.AdoptionReport;
import pro.sky.telegrambotshelter.service.AdoptionReportService;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/adoption_reports")
public class AdoptionReportController {
    private final AdoptionReportService adoptionReportService;

    public AdoptionReportController(AdoptionReportService adoptionReportService) {
        this.adoptionReportService = adoptionReportService;
    }

    @GetMapping
    public Collection<AdoptionReport> allReports(){
        return adoptionReportService.findAll();
    }

    @GetMapping(params = "date")
    public Collection<AdoptionReport> getReportsByDate(@RequestParam("date") String date){
       return adoptionReportService.findAllByDate(date);
    }

    @GetMapping(params ={"fromDate", "toDate"})
    public Collection<AdoptionReport> getReportsByDateBetween(@RequestParam("fromDate") String fromDate,
                                                              @RequestParam("toDate") String toDate){
        return adoptionReportService.findAllByDateBetween(fromDate, toDate);
    }

    @GetMapping(params = {"adoptionId", "date"})
    public Collection<AdoptionReport> getReportsByAdoptionIdAndDate(@RequestParam("adoptionId") Integer adoptionId,
                                                                    @RequestParam("date") String date){
        return adoptionReportService.findAllByAdoptionAndReportDate(adoptionId, date);
    }

    @GetMapping(params = "reportId")
    public ResponseEntity<AdoptionReport> getById(@RequestParam("reportId") Integer id,  HttpServletResponse response) throws IOException {
        Optional<AdoptionReport> op = adoptionReportService.findById(id);
        if (op.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        AdoptionReport adoptionReport = op.get();
        Path filePath = Path.of(adoptionReport.getFilePath());
        try(InputStream in = Files.newInputStream(filePath);
            OutputStream out = response.getOutputStream();
            BufferedInputStream bIn = new BufferedInputStream(in, 1024);
            BufferedOutputStream bOut = new BufferedOutputStream(out, 1024)){
            response.setStatus(200);
            response.setContentType(adoptionReport.getMediaType());
            response.setCharacterEncoding("utf-8");
            response.setContentLength((int) Files.size(filePath));
            in.transferTo(out);
        }
        return ResponseEntity.ok().build();
    }

}
