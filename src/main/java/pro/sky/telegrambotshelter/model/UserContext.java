package pro.sky.telegrambotshelter.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class UserContext {
    @Id
    private long chatId;
    private String lastCommand;
    @Enumerated(EnumType.STRING)
    @Column(name = "pet_type")
    private PetType petType;

    public UserContext() {
    }

    public UserContext(long chatId, String lastCommand) {
        this.chatId = chatId;
        this.lastCommand = lastCommand;
        this.petType = petType;
    }

    public UserContext(long chatId, String lastCommand, PetType petType) {
        this.chatId = chatId;
        this.lastCommand = lastCommand;
        this.petType = petType;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getLastCommand() {
        return lastCommand;
    }

    public void setLastCommand(String lastCommand) {
        this.lastCommand = lastCommand;
    }

    public PetType getPetType() {
        return petType;
    }

    public void setPetType(PetType petType) {
        this.petType = petType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserContext that = (UserContext) o;
        return chatId == that.chatId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId);
    }

    @Override
    public String toString() {
        return "UserContext{" +
                ", chatId=" + chatId +
                ", lastCommand='" + lastCommand + '\'' +
                '}';
    }
}
