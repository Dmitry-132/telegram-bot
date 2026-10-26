package pro.sky.telegrambot.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "reminder_task")
public class ReminderTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", nullable = false) //явное назначение ячейки
    private Long chatId;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    public ReminderTask() {
    }

    public ReminderTask(Long chatId, String message, LocalDateTime dateTime) {
        this.chatId = chatId;
        this.message = message;
        this.dateTime = dateTime;
    }

    public ReminderTask(Long id, Long chatId, String message, LocalDateTime dateTime) {
        this.id = id;
        this.chatId = chatId;
        this.message = message;
        this.dateTime = dateTime;
    }

    public Long getId() {
        return id;
    }

    public Long getChatId() {
        return chatId;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "ReminderTask{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", message='" + message + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}
