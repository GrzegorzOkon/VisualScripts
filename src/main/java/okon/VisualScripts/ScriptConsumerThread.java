package okon.VisualScripts;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static okon.VisualScripts.VisualScriptsWindow.scriptQueue;

public class ScriptConsumerThread implements Runnable {
    private static final Logger logger = LogManager.getLogger(ScriptConsumerThread.class);

    public void run(Script script) {
        execute(script);
    }

    @Override
    public void run() {
        while (!scriptQueue.isEmpty()) {
            Script script = null;
            synchronized (scriptQueue) {
                if (!scriptQueue.isEmpty()) {
                    script = scriptQueue.poll();
                }
            }
            if (script != null) {
                execute(script);
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                logger.error(script.getName() + " - " + e.getMessage());
            }
        }
    }

    private void execute(Script script) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        StopWatch executionWatch = new StopWatch();
        processBuilder.command(script.getCommand());
        processBuilder.directory(new File(script.getPath()));
        executionWatch.start();
        try {
            Process process = processBuilder.start();
            logger.info(script.getName() + " is starting...");
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                logger.info(script.getName() + " is finished (exec time: " + executionWatch.getTime(TimeUnit.SECONDS) + " sec.)");
            } else {
                logger.error(script.getName() + " is finished with error (exec time: " + executionWatch.getTime(TimeUnit.SECONDS) + " sec.)");
            }
        } catch (IOException e) {
            logger.error(script.getName() + " - " + e.getMessage());
        } catch (InterruptedException e) {
            logger.error(script.getName() + " - " + e.getMessage());
        }
        executionWatch.stop();
    }
}
