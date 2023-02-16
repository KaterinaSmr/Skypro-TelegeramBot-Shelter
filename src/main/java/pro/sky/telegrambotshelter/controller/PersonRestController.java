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
import pro.sky.telegrambotshelter.model.Person;
import pro.sky.telegrambotshelter.service.PersonService;

import java.util.Collection;

@RestController
@RequestMapping("/person")
public class PersonRestController {
    private final PersonService personService;

    public PersonRestController(PersonService personService) {
        this.personService = personService;
    }

    @Operation(
            summary = "Все пользователи",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список всех пользователи",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Person.class))
                            )
                    )
            }
    )
    @GetMapping
    public Collection<Person> allPeople(){
        return personService.findAll();
    }

    @Operation(
            summary = "Поиск пользователя по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденный пользователь",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Person.class)
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Person> getPerson(@Parameter(description = "id пользователя", example = "1")
                                                @PathVariable Integer id){
        Person person = personService.findById(id);
        if (person == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(person);
    }

    @Operation(
            summary = "Редактирование информации о пользователе",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Редактирование информации о пользователе. Поле id - обязательно для заполнения. " +
                            "Информация о питомце с указанным id будет перезаписана новыми значениями",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Person.class)
                    )
            )
    )
    @PutMapping()
    public ResponseEntity<Person> editPet(@RequestBody Person person){
        Person personFound = personService.edit(person);
        if (personFound == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(personFound);
    }

    @Operation(
            summary = "Удаление записи пользователя по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Статус успешного удаления"
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@Parameter(description = "id пользователя", example = "1")
            @PathVariable Integer id){
        personService.delete(id);
        return ResponseEntity.ok().build();
    }
}
