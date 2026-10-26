package pro.sky.telegrambot.configuration;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.DeleteMyCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration //создаёт бин бота
public class TelegramBotConfiguration {

    @Value("${telegram.bot.token}")
    private String token;

    @Bean
    public TelegramBot telegramBot() {
        TelegramBot bot = new TelegramBot(token);
        bot.execute(new DeleteMyCommands());
        return bot;
    }

}

//    @Component — исполняется в базовом режиме
//    @Configuration + @Bean — вручную создает нужные объекты

//Controller	@RestController	Принимает HTTP-запросы (GET, POST) от браузера или клиента
//Service    	@Service	    Бизнес-логика (например, парсинг даты из сообщения)
//Repository	@Repository	    Общение с базой данных (сохранение задач)
//Model/Entity	@Entity	        Классы данных (например, Task)