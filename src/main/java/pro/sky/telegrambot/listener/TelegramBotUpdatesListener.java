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
                    telegramBot.execute(new SendMessage(chatId,
                            "Это бот-напоминалка. Отправь сообщение в формате: дд.мм.гггг чч:мм Текст задачи"));
                    return;
                }
                telegramBot.execute(new SendMessage(chatId, taskService.processAndSaveTask(chatId, messageText)));
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
