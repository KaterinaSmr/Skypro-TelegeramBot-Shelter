package pro.sky.telegrambotshelter;

public class PersonNotFoundException extends RuntimeException{
    public PersonNotFoundException() {
        super("Person not found");
    }
}
