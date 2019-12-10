package okon.VisualScripts;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ScriptRunner {
    private static final Logger logger = LogManager.getLogger(ScriptRunner.class);

    public static void run(Script script) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        StopWatch executionWatch = new StopWatch();
        processBuilder.command(script.getCommand());
        processBuilder.directory(new File(script.getPath()));
        executionWatch.start();
        try {
            Process process = processBuilder.start();
            logger.info(script.getName() + " is starting...");
            System.out.println(script.getName() + " is starting...");
            process.waitFor(15, TimeUnit.SECONDS);
            if (!process.isAlive()) {
                logger.info(script.getName() + " is finished (exec time: " + executionWatch.getTime(TimeUnit.SECONDS) + " sec.)");
                System.out.println(script.getName() + " is finished (exec time: " + executionWatch.getTime(TimeUnit.SECONDS) + " sec.)");
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(script.getName() + " - " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error(script.getName() + " - " + e.getMessage());
        }
        executionWatch.stop();
    }
}
