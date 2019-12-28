package okon.VisualScripts;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class ScriptTask implements Callable<Boolean> {
    private static final Logger logger = LogManager.getLogger(ScriptTask.class);
    private final Script script;

    public ScriptTask(Script script) {
        this.script = script;
    }

    @Override
    public Boolean call() {
        if (script != null) {
            execute();
        }
        return true;
    }

    public void execute() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        StopWatch executionWatch = new StopWatch();
        processBuilder.command(script.getCommand());
        processBuilder.directory(new File(script.getPath()));
        executionWatch.start();
        try {
            Process process = processBuilder.start();
            logger.info(script.getName() + " is starting...");
            process.waitFor(5, TimeUnit.MINUTES);
            if (!process.isAlive()) {
                logger.info(script.getName() + " is finished (exec time: " + executionWatch.getTime(TimeUnit.SECONDS) + " sec.)");
            } else {
                process.destroyForcibly();
                logger.error(script.getName() + " is destroyed (exec time: " + executionWatch.getTime(TimeUnit.SECONDS) + " sec.)");
            }
        } catch (IOException e) {
            logger.error(script.getName() + " - " + e.getMessage());
        } catch (InterruptedException e) {
            logger.error(script.getName() + " - " + e.getMessage());
        }
        executionWatch.stop();
    }
}
