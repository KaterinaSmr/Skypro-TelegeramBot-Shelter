package pro.sky.telegrambotshelter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.telegrambotshelter.model.Adoption;
import pro.sky.telegrambotshelter.model.AdoptionStatus;
import pro.sky.telegrambotshelter.service.AdoptionService;

import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping("/adoption")
public class AdoptionController {
    private final AdoptionService adoptionService;

    public AdoptionController(AdoptionService adoptionService) {
        this.adoptionService = adoptionService;
    }

    @Operation(
            summary = "Все записи об усыновлении",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список всех записей об усыновлении",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Adoption.class))
                            )
                    )
            }
    )
    @GetMapping
    public Collection<Adoption> allAdoptions(){
        return adoptionService.getAllAdoptions();
    }

    @Operation(
            summary = "Поиск записи об усыновлении по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденная запись",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Adoption.class)
                            )
                    )
            }
    )
    @GetMapping(params = "petId")
    public ResponseEntity<Adoption> getByPetId(@Parameter(description = "id питомца", example = "1")
                                                   @RequestParam (required = false) Integer petId){
        Adoption adoption = adoptionService.findByPetId(petId);
        if (adoption == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(adoption);
    }

    @Operation(
            summary = "Поиск записи об усыновлении по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденная запись",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Adoption.class)
                            )
                    )
            }
    )
    @GetMapping(params = "personId")
    public ResponseEntity<Adoption> getByPersonId(@Parameter(description = "id человека", example = "1")
                                                      @RequestParam (required = false) Integer personId){
        Adoption adoption = adoptionService.findByPersonId(personId);
        if (adoption == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(adoption);
    }

    @Operation(
            summary = "Поиск записи об усыновлении по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденная запись",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Adoption.class)
                            )
                    )
            }
    )
    @GetMapping("/{adoptionId}")
    public ResponseEntity<Adoption> getById(@PathVariable Integer adoptionId){
        Adoption adoption = adoptionService.findById(adoptionId);
        if (adoption == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(adoption);
    }

    @Operation(
            summary = "Создание записи об усыновлении",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Создание записи об усыновлении в базе данных. Поле id не обязательно для заполнению. " +
                            "Значение id будет перезаписано автоматической последовательной нумерацией.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Adoption.class)
                    )
            )
    )
    @PostMapping
    public ResponseEntity<Adoption> addAdoption(@RequestBody Adoption adoption){
        Adoption adoptionSaved = adoptionService.save(adoption);
        if (adoptionSaved == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(adoption);
    }

    @Operation(
            summary = "Редактирование записи об усыновлении",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Редактирование информации об усыновлении. Поле id - обязательно для заполнения. " +
                            "Информация об усыновлении с указанным id будет перезаписана новыми значениями",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Adoption.class)
                    )
            )
    )
    @PutMapping()
    public ResponseEntity<Adoption> editAdoption(@RequestBody Adoption adoption){
        Adoption adoptionFound = adoptionService.edit(adoption);
        if (adoptionFound == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(adoption);
    }

    @Operation(
            summary = "Обновление статуса усыновления",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Статус успешного обновления"
                    )
            }
    )
    @PutMapping(name = "/{adoptionId}", params = "adoptionStatus")
    public ResponseEntity<Adoption> updateProbationStatus (@PathVariable Integer adoptionId,
                                                           @RequestParam (required = false) AdoptionStatus adoptionStatus){
        Adoption adoption = adoptionService.setNewStatus(adoptionId, adoptionStatus);
        if (adoption == null){
            ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(adoption);
    }

    @Operation(
            summary = "Обновление статуса усыновления",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Статус успешного обновления"
                    )
            }
    )
    @PutMapping(name = "/{adoptionId}", params = "probationEndDate")
    public ResponseEntity<Adoption> updateProbationEndDate (@PathVariable Integer adoptionId,
                                                           @RequestParam (required = false) LocalDate newDate){
        Adoption adoption = adoptionService.setNewProbationEndDate(adoptionId, newDate);
        if (adoption == null){
            ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(adoption);
    }

    @Operation(
            summary = "Удаление записи об усыновлении по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Статус успешного удаления"
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity deleteAdoption(@Parameter(description = "id Записи об усыновлении", example = "1")
                                             @PathVariable Integer id){
        adoptionService.delete(id);
        return ResponseEntity.ok().build();
    }

}
