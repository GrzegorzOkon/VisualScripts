package okon.VisualScripts;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class JarRunner {
    private static final Logger logger = LogManager.getLogger(JarRunner.class);

    public static void run(Script script) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        StopWatch executionWatch = new StopWatch();
        processBuilder.command("java", "-jar", script.getName() + ".jar");
        processBuilder.directory(new File(script.getPath()));
        executionWatch.start();
        try {
            Process process = processBuilder.start();
            logger.info(script.getName() + " starts...");
            process.waitFor(15, TimeUnit.SECONDS);
            if (!process.isAlive()) {
                logger.info(script.getName() + " finishes... (exec time: " + executionWatch.getTime(TimeUnit.SECONDS) + " sec.)");
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
