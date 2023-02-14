package pro.sky.telegrambotshelter.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class UserContext {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
    private long chatId;
    private String lastCommand;

    public UserContext() {
    }

    public UserContext(long chatId, String lastCommand) {
        this.chatId = chatId;
        this.lastCommand = lastCommand;
    }

//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserContext that = (UserContext) o;
//        return id == that.id && chatId == that.chatId;
        return chatId == that.chatId;
    }

    @Override
    public int hashCode() {
//        return Objects.hash(id, chatId);
        return Objects.hash(chatId);
    }

    @Override
    public String toString() {
        return "UserContext{" +
//                "id=" + id +
                ", chatId=" + chatId +
                ", lastCommand='" + lastCommand + '\'' +
                '}';
    }
}
