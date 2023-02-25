package pro.sky.telegrambotshelter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.telegrambotshelter.model.Adoption;
import pro.sky.telegrambotshelter.model.AdoptionReport;
import pro.sky.telegrambotshelter.model.Person;
import pro.sky.telegrambotshelter.service.AdoptionReportService;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;


public class AdoptionReportController <V extends AdoptionReport<S, T>, S extends Adoption<T>, T extends Person> {
    private final AdoptionReportService<V, S, T> adoptionReportService;

    public AdoptionReportController(AdoptionReportService<V, S, T> adoptionReportService) {
        this.adoptionReportService = adoptionReportService;
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список всех отчетов",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = AdoptionReport.class))
                    )
            )
    })
    @GetMapping
    public Collection<V> allReports(){
        return adoptionReportService.findAll();
    }

    @Operation(
            summary = "Поиск отчетов по датам, id усыновления или id отчета",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденные отчеты",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = AdoptionReport.class))
                            )
                    )
            }
    )
    @GetMapping(params = "reportDate")
    public Collection<V> getReportsByDate(@Parameter(description = "Дата в формате ДДММГГГГ", example = "07022023")
            @RequestParam(name = "reportDate", required = false) String date){
       return adoptionReportService.findAllByDate(date);
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список отчетов за указанный промежуток времени",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = AdoptionReport.class))
                    )
            )
    })
    @GetMapping(params ={"fromDate", "toDate"})
    public Collection<V> getReportsByDateBetween(
            @Parameter(description = "Начало периода в формате ДДММГГГГ", example = "25012023") @RequestParam(name = "fromDate", required = false) String fromDate,
            @Parameter(description = "Конец периода выборки в формате ДДММГГГГ", example = "10022023") @RequestParam(name = "toDate", required = false) String toDate){
        return adoptionReportService.findAllByDateBetween(fromDate, toDate);
    }

    @Operation(
            summary = "Поиск отчетов по датам, id усыновления или id отчета",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденные отчеты",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = AdoptionReport.class))
                            )
                    )
            },
            parameters = {
                    @Parameter(name = "adoptionId", description = "id усыновления", example = "1"),
                    @Parameter(name = "date", description = "Дата отчета в формате ДДММГГГГ", example = "25012023")
            }
    )
    @GetMapping(params = {"adoptionId", "date"})
    public ResponseEntity<Collection<V>> getReportsByAdoptionIdAndDate(
            @RequestParam(name = "adoptionId", required = false) Integer adoptionId,
            @RequestParam(name = "date", required = false) String date) {
        Collection<V> reports = adoptionReportService.findAllByAdoptionAndReportDate(adoptionId, date);
        if (reports == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(reports);
    }


    @Operation(
            summary = "Поиск отчета по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденный отчет в фомате фото или текста",
                            content = {
                                    @Content(mediaType = MediaType.IMAGE_PNG_VALUE),
                                    @Content(mediaType = MediaType.IMAGE_GIF_VALUE),
                                    @Content(mediaType = MediaType.IMAGE_JPEG_VALUE),
                                    @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)
                            }
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<V> getById(
            @Parameter(description = "id отчета", example = "17")
            @PathVariable Integer id, HttpServletResponse response) throws IOException {
        Optional<V> op = adoptionReportService.findById(id);
        if (op.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        V adoptionReport = op.get();
        Path filePath = Path.of(adoptionReport.getFilePath());
        try(InputStream in = Files.newInputStream(filePath);
            OutputStream out = response.getOutputStream();
            BufferedInputStream bIn = new BufferedInputStream(in, 1024);
            BufferedOutputStream bOut = new BufferedOutputStream(out, 1024)){
            response.setStatus(200);
            response.setContentType(adoptionReport.getMediaType());
            response.setCharacterEncoding("utf-8");
            response.setContentLength((int) Files.size(filePath));
            bIn.transferTo(bOut);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Удаление записи отчета по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешно удалено"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Отчет не найден"
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<V> delete(@PathVariable Integer id, @RequestParam ("removeFile") Boolean removeFile){
        V removed = adoptionReportService.delete(id, removeFile);
        if (removed == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(removed);
    }
}
