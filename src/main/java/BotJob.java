import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class BotJob implements Job {



    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //Check time
        Calendar cal = Calendar.getInstance();
        int h = cal.get(Calendar.HOUR_OF_DAY);
        int m = cal.get(Calendar.MINUTE);
        if (h == 16 && m == 59) {
            List<String> lstStr = BTRedMine.getInstance().RDP();
            for (String s: lstStr) {
                new BotJob().SendMessage(s);
            }
        }
    }


    void SendMessage(String msg) {
        TelegramBot bot = new TelegramBot(exc.TOKEN);
        SendMessage request = new SendMessage(exc.CHATID, msg).parseMode(ParseMode.HTML);
        // sync
        SendResponse sendResponse = bot.execute(request);
        boolean ok = sendResponse.isOk();
        Message message = sendResponse.message();
    }
}
