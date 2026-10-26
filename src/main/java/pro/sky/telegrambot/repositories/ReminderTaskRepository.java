package pro.sky.telegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.ReminderTask;

import java.time.LocalDateTime;
import java.util.List;

public interface ReminderTaskRepository extends JpaRepository<ReminderTask, Long> {

    List<ReminderTask> findByDateTime(LocalDateTime dateTime);
    long countByChatId(Long chatId);
    List<ReminderTask> findByChatIdOrderByDateTimeAsc(Long chatId);
    ReminderTask findByIdAndChatId(Long id, Long chatId);
}
