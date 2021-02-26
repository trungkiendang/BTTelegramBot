import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.List;

public class exc {


    public static void main(String[] args) {
        JobDetail job = JobBuilder.newJob(BotJob.class)
                .withIdentity("Telegram bot", "BT").build();

        Trigger trig = TriggerBuilder.newTrigger()
                .withIdentity("cronTrigger", "BT")
                .withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?"))
                .build();

        Scheduler schedule = null;
        try {
            schedule = new StdSchedulerFactory().getScheduler();
            schedule.start();
            schedule.scheduleJob(job, trig);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        TelegramBot bot = new TelegramBot(BotJob.TOKEN);
        bot.setUpdatesListener(new UpdatesListener() {
            @Override
            public int process(List<Update> updates) {
                for (Update u : updates) {
                    if (u.message().chat().id() == -564253083) {
                        if (u.message().text().contains("/start")) {
                             new BotJob().SendMessage(BTRedMine.getInstance().Report());
                            //BTRedMine.getInstance().Report();
//                            String[] lst = BTRedMine.getInstance().getAllProjects().stream().map(project -> project.name).toArray(String[]::new);
//
//                            Keyboard replyKeyboardMarkup = new ReplyKeyboardMarkup(lst).oneTimeKeyboard(true)   // optional
//                                    .resizeKeyboard(true)    // optional
//                                    .selective(true);        // optional
//                            SendResponse response = bot.execute(new SendMessage(u.message().chat().id(), "Hello!").parseMode(ParseMode.HTML) .replyMarkup(replyKeyboardMarkup));
//                            boolean ok = response.isOk();
//                            Message message = response.message();
                        }
                    }
                }
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        });
    }
}
