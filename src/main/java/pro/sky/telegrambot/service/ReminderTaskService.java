package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.telegrambot.model.ReminderTask;
import pro.sky.telegrambot.repositories.ReminderTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ReminderTaskService {

    private static final Logger logger = LoggerFactory.getLogger(ReminderTaskService.class);

    private final ReminderTaskRepository repository;
    private static final int MAX_NUMBER_REMINDERS = 30;
    private static final int MAX_MESSAGE_LENGTH = 255;

    private static final DateTimeFormatter FULL_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"); //переводит в формат dd.MM.yyyy HH:mm
    private static final Pattern PATTERN = Pattern.compile("(\\d{2})\\.(\\d{2})\\.(\\d{4}) (\\d{2}):(\\d{2}) (.+)"); //Pattern.compile компилирует регулярное выражение. вроде шаблона для сравнения

    public ReminderTaskService(ReminderTaskRepository repository) {
        this.repository = repository;
    }

    public String processAndSaveTask(long chatId, String messageText) {
        if (repository.countByChatId(chatId) >= MAX_NUMBER_REMINDERS) {
            return "Вы достигди лимита активных напоминалок.";
        }
        Matcher matcher = PATTERN.matcher(messageText); //Matcher применяет шаблон к строке.
        if (!matcher.matches()) { // проверяет на соответствие шаблону
            logger.warn("Сообщение не соответствует формату: {}", messageText);
            return "Сообщение не соответствует формату";
        }
        try {
            String dateTimeString = matcher.group(1) + "." + matcher.group(2) + "." + matcher.group(3) +
                    " " + matcher.group(4) + ":" + matcher.group(5);
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, FULL_FORMATTER); // переводит строку в объект даты, используя FORMATTER
            if (dateTime.isBefore(LocalDateTime.now())) {
                logger.warn("Попытка создать задачу на прошлое время: {}", dateTime);
                return "Было указано прошедшее время";
            }
            String taskMessage = matcher.group(6);
            if (taskMessage.isEmpty()) {
                logger.warn("Попытка создать задачу без текста сообщения: {}", dateTime);
                return "Текст напоминалки не был введен";
            }
            if (taskMessage.length() > MAX_MESSAGE_LENGTH) {
                return "Сообщение слишком длинное. Сократите его до " + MAX_MESSAGE_LENGTH + " символов.";
            }
            repository.save(new ReminderTask(chatId, taskMessage, dateTime));
            logger.info("Задача сохранена: {}", taskMessage);
            return "Напоминание сохранено";

        } catch (Exception e) {
            logger.error("Ошибка при парсинге даты: {}", e.getMessage());
            return "Необходимый формат: дд.мм.гггг чч:мм Текст задачи";
        }
    }

    public String getUserTasks(long chatId) {
        List<ReminderTask> tasks = repository.findByChatIdOrderByDateTimeAsc(chatId);
        if (tasks.isEmpty()) {
            return "У вас пока нет активных напоминаний";
        }
        StringBuilder response = new StringBuilder("Ваши напоминалки:\n"); //StringBuilder — это изменяемый (mutable) буфер
        for (ReminderTask task : tasks) {
            response.append("ID: ").append(task.getId()) //append добавляет текст в конец
                    .append(" | ").append(task.getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
                    .append(" | ").append(task.getMessage())
                    .append("\n");
        }
        response.append("\nЧтобы удалить, напишите: /delete ID");
        return response.toString();
    }

    public String deleteTask(long chatId, long taskId) {
        ReminderTask task = repository.findByIdAndChatId(taskId, chatId);
        if (task == null) {
            return "Напоминалка с таким ID не найдена";
        }
        repository.delete(task);
        logger.info("Задача удалена: taskId {}", taskId);
        return "Напоминалка удалена\n" + task.getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + " | " + task.getMessage();
    }
}