import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.List;

public class exc {
    public static final String TOKEN = "1666976863:AAHcO2Q9IkQsPOTPyYWlChBeLjcQxIob61E";
    public static final String CHATID = "-564253083";//"-1001424313111";

    public static void main(String[] args) {
        System.out.println("---Start bot telegram---");

        JobDetail job = JobBuilder.newJob(BotJob.class)
                .withIdentity("Telegram bot", "BT").build();

        Trigger trig = TriggerBuilder.newTrigger()
                .withIdentity("cronTrigger", "BT")
                .withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?"))
                .build();

        Scheduler schedule = null;
        try {
            schedule = new StdSchedulerFactory().getScheduler();
            System.out.println("---Start scheduler---");
            schedule.start();
            schedule.scheduleJob(job, trig);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        TelegramBot bot = new TelegramBot(TOKEN);
        bot.setUpdatesListener(new UpdatesListener() {
            @Override
            public int process(List<Update> updates) {
                for (Update u : updates)
                    try {
                        if (u.message().chat().id() == Long.parseLong(CHATID)) {
                            if (u.message().text() != null) {
                                String msg = u.message().text();
                                if (msg.contains("/help"))
                                    new BotJob().SendMessage("Bot hỗ trợ việc kiểm tra công việc trên hệ thống Redmine - B&T \n" +
                                            "Sử dụng cú pháp: <b>/check</b> để kiểm tra trạng thái các công việc.");
                                if (msg.contains("/check")) {
                                    new BotJob().SendMessage("<i>Đang lấy thông tin công việc, vui lòng đợi...</i>");
                                    List<String> lstStr = BTRedMine.getInstance().RDP();
                                    for (String s : lstStr) {
                                        new BotJob().SendMessage(s);
                                    }

                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        });
    }
}
