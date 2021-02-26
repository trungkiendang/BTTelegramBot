import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Calendar;
import java.util.Date;


public class BotJob implements Job {

    public static final String TOKEN = "1666976863:AAHcO2Q9IkQsPOTPyYWlChBeLjcQxIob61E";
    private static final String CHATID = "-564253083";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //Check time
        Calendar cal = Calendar.getInstance();
        int h = cal.get(Calendar.HOUR_OF_DAY);
        int m = cal.get(Calendar.MINUTE);
        System.out.println(">>>> " + h);
        if (h == 8 && m == 0) {
            SendMessage(BTRedMine.getInstance().Report());
        }
    }


    void SendMessage(String msg) {
        TelegramBot bot = new TelegramBot(TOKEN);
        SendMessage request = new SendMessage(CHATID, msg);
        // sync
        SendResponse sendResponse = bot.execute(request);
        boolean ok = sendResponse.isOk();
        Message message = sendResponse.message();
    }
}
