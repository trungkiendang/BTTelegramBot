import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
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
        int _s = cal.get(Calendar.SECOND);
        if (m % 3 == 0 && _s == 0)
            ErrorLogger.getInstance().log("Ping... " + h + ":" + m + ":" + _s);
        if (h == 17 && m == 30 && _s == 40) {
            BTRedMine.getInstance().RDP(exc.CHATID);
        }
    }


    void SendMessage(String msg, String chatId) {
        TelegramBot bot = new TelegramBot(exc.TOKEN);
        SendMessage request = new SendMessage(chatId, msg).parseMode(ParseMode.HTML).disableWebPagePreview(true)
                .disableNotification(true);
        // sync
        SendResponse sendResponse = bot.execute(request);

        boolean ok = sendResponse.isOk();
        if (!ok)
            ErrorLogger.getInstance().log("Err: " + sendResponse.errorCode() + "---" + sendResponse.description());
        Message message = sendResponse.message();
        //ErrorLogger.getInstance().log("Log: " + message.text());

//        BaseResponse response = bot.execute(request);
//        bot.execute(request, new Callback() {
//            @Override
//            public void onResponse(BaseRequest request, BaseResponse response) {
//                if (!response.isOk()) {
//                    System.out.println(response.errorCode() + "---" + response.description());
//                }
//            }
//
//            @Override
//            public void onFailure(BaseRequest request, IOException e) {
//                System.out.println("Err: " + e.getMessage());
//            }
//        });
    }
}
