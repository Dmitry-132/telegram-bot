package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import pro.sky.telegrambot.service.ReminderTaskService;

import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private ReminderTaskService taskService;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update.message().chat().id()); //.message().chat().id() для сокращения лога
            if (update.message() != null && update.message().text() != null) {
                String messageText = update.message().text();
                long chatId = update.message().chat().id();

                if (messageText.startsWith("/start")) {
                    telegramBot.execute(new SendMessage(chatId, "Это бот-напоминалка.\n\n" +
                            "Отправь сообщение в формате:\n" +
                            " \"дд.мм.гггг чч:мм Текст напоминалки\"\n" +
                            "И этот бот напомнит небе о ней в указанное время\n\n" +
                            "Ты также можешь посмотреть свои напоминалки командой /list\n" +
                            "И удалить ненужные через /delete ID"));
                    return;
                }
                if (messageText.equals("/list")) {
                    String tasks = taskService.getUserTasks(chatId);
                    telegramBot.execute(new SendMessage(chatId, tasks));
                    return;
                }
                if (messageText.startsWith("/delete")) {
                    try {
                        String[] parts = messageText.split(" "); // разбивает строку по пробелам
                        if (parts.length != 2) { //позже можно сделать удаление нескольких задач одной командой
                            telegramBot.execute(new SendMessage(chatId, "Неверный формат. Напишите: /delete ID"));
                            return;
                        }
                        long taskId = Long.parseLong(parts[1]); //превращает строку в число
                        String result = taskService.deleteTask(chatId, taskId);
                        telegramBot.execute(new SendMessage(chatId, result));
                    } catch (NumberFormatException e) { //catch только для ошибки парсинга числа
                        telegramBot.execute(new SendMessage(chatId, "ID должен быть числом. Напишите: /delete 123"));
                    }
                    return;
                }
                telegramBot.execute(new SendMessage(chatId, taskService.processAndSaveTask(chatId, messageText)));
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
