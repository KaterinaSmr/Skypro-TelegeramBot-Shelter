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
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.model.PetType;
import pro.sky.telegrambotshelter.service.PetService;

import java.util.Collection;

@RestController
@RequestMapping("/pet")
public class PetController {
    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @Operation(
            summary = "Все питомцы",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список всех питомцев",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Pet.class))
                            )
                    )
            }
    )
    @GetMapping
    public Collection<Pet> allPets(){
        return petService.findAll();
    }

    @Operation(
            summary = "Поиск питомца по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденный питомец",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Pet> getPet(@Parameter(description = "id питомца", example = "1") @PathVariable Integer id){
        Pet pet = petService.findById(id);
        if (pet == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pet);
    }

    @Operation(
            summary = "Сохранение информации о питомце",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Создание записи о питомце в базе данных. Поле id не обязательно для заполнению. " +
                            "Значение id будет перезаписано автоматической последовательной нумерацией.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Pet.class)
                    )
            )
    )
    @PostMapping
    public Pet addPet(@RequestBody Pet pet){
        return petService.save(pet);
    }


    @Operation(
            summary = "Редактирование информации о питомце",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Редактирование информации о питомце. Поле id - обязательно для заполнения. " +
                            "Информация о питомце с указанным id будет перезаписана новыми значениями",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Pet.class)
                    )
            )
    )
    @PutMapping()
    public ResponseEntity<Pet> editPet(@RequestBody Pet pet){
        Pet petFound = petService.edit(pet);
        if (petFound == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(petFound);
    }

    @Operation(
            summary = "Удаление записи питомца по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Статус успешного удаления"
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Integer id){
        petService.delete(id);
        return ResponseEntity.ok().build();
    }
}
