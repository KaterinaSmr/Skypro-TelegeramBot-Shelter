package pro.sky.telegrambotshelter.controller.adoption;

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
import pro.sky.telegrambotshelter.model.adoption.Adoption;
import pro.sky.telegrambotshelter.model.adoption.AdoptionStatus;
import pro.sky.telegrambotshelter.model.person.Person;
import pro.sky.telegrambotshelter.service.adoption.AdoptionService;

import java.util.Collection;

public class AdoptionController <S extends Adoption<T>, T extends Person>  {
    AdoptionService<S,T> adoptionService;

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
    public Collection<S> allAdoptions(){
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
    public ResponseEntity<S> getByPetId(@Parameter(description = "id питомца", example = "1")
                                                   @RequestParam (required = false) Integer petId){
        S adoption = adoptionService.findByPetId(petId);
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
    public ResponseEntity<S> getByPersonId(@Parameter(description = "id человека", example = "1")
                                                      @RequestParam (required = false) Integer personId){
        S adoption = adoptionService.findByPersonId(personId);
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
    public ResponseEntity<S> getById(@PathVariable Integer adoptionId){
        S adoption = adoptionService.findById(adoptionId);
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
    public ResponseEntity<S> addAdoption(@RequestBody S adoption){
        S adoptionSaved = adoptionService.save(adoption);
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
    @PutMapping("/edit")
    public ResponseEntity<S> editAdoption(@RequestBody S adoption){
        S adoptionFound = adoptionService.edit(adoption);
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
    @PutMapping(params = {"adoptionId", "adoptionStatus"})
    public ResponseEntity<S> updateProbationStatus (@RequestParam Integer adoptionId,
                                                    @RequestParam (required = false) AdoptionStatus adoptionStatus){
        S adoption = adoptionService.setNewStatus(adoptionId, adoptionStatus);
        if (adoption == null){
            return ResponseEntity.notFound().build();
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
    @PutMapping(params = {"adoptionId", "probationEndDate"})
    public ResponseEntity<S> updateProbationEndDate (
            @RequestParam Integer adoptionId,
            @RequestParam (required = false) String probationEndDate){
        S adoption = adoptionService.setNewProbationEndDate(adoptionId, probationEndDate);
        if (adoption == null){
            return ResponseEntity.notFound().build();
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
