package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.ReminderTask;
import pro.sky.telegrambot.repositories.ReminderTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReminderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ReminderScheduler.class);
    private final ReminderTaskRepository repository;
    private final TelegramBot telegramBot;

    public ReminderScheduler(ReminderTaskRepository repository, TelegramBot telegramBot) {
        this.repository = repository;
        this.telegramBot = telegramBot;
    }

    @Scheduled(cron = "0 0/1 * * * *") // Этот метод будет запускаться каждую минуту (в 00 секунд)
    public void sendNotifications() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES); //возмет текущее время и обрежет его до минут
        List<ReminderTask> tasksToSend = repository.findByDateTime(now);

        for (ReminderTask task : tasksToSend) {
            telegramBot.execute(new SendMessage(task.getChatId(), "Напоминание: " + task.getMessage()));
            logger.info("Отправлено напоминание: {}", task.getMessage());
            repository.delete(task);
        }
    }
}
