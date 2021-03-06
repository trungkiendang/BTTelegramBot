import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class ErrorLogger {
    private final Logger logger;
    private static ErrorLogger _instance;

    public static ErrorLogger getInstance() {
        if (_instance == null)
            _instance = new ErrorLogger();
        return _instance;
    }

    public ErrorLogger() {
        logger = Logger.getAnonymousLogger();
        configure();
    }

    private void configure() {
        try {
            String logsDirectoryFolder = "logs";
            Files.createDirectories(Paths.get(logsDirectoryFolder));
            FileHandler fileHandler = new FileHandler(logsDirectoryFolder + File.separator + getCurrentTimeString() + ".log");
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        addCloseHandlersShutdownHook();
    }

    private void addCloseHandlersShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            for (Handler handler : logger.getHandlers()) {
                handler.close();
            }
        }));
    }

    private String getCurrentTimeString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(new Date());
    }

    public void log(Exception exception) {
        logger.log(Level.SEVERE, "", exception);
        System.out.println(exception.getMessage());
    }

    public void log(String msg) {
        logger.log(Level.INFO, msg);
        System.out.println(msg);
    }

}