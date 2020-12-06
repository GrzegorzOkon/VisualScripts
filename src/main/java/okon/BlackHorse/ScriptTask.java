package okon.BlackHorse;

import okon.BlackHorse.utils.TimeConfigurator;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class ScriptTask implements Callable<Message> {
    private static final Logger logger = LogManager.getLogger(ScriptTask.class);
    private final Script script;

    public ScriptTask(Script script) {
        this.script = script;
    }

    @Override
    public Message call() {
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder();
        StopWatch executionWatch = new StopWatch();
        List<String> extendedCommand = script.getCommand();
        extendedCommand.add("-a");
        processBuilder.command(extendedCommand);
        processBuilder.directory(new File(script.getPath()));
        executionWatch.start();
        try {
            Process process = processBuilder.start();
            logger.info(script.getName() + " is starting...");
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                 BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                StringJoiner sj = new StringJoiner(System.getProperty("line.separator"));
                inputReader.lines().iterator().forEachRemaining(sj::add);
                result = sj.toString();
                process.waitFor(5, TimeUnit.MINUTES);
                if (!process.isAlive()) {
                    logger.info(script.getName() + " is finished in " + TimeConfigurator.toProperUnitOfMeasure(Duration.ofMillis(executionWatch.getTime(TimeUnit.MILLISECONDS))));
                } else {
                    process.destroyForcibly();
                    logger.error(script.getName() + " is destroyed in " + TimeConfigurator.toProperUnitOfMeasure(Duration.ofMillis(executionWatch.getTime(TimeUnit.MILLISECONDS))));
                }
            }
        } catch (IOException e) {
            logger.error(script.getName() + " - " + e.getMessage());
        } catch (InterruptedException e) {
            logger.error(script.getName() + " - " + e.getMessage());
        }
        executionWatch.stop();
        return new Message(script.getName(), result);
    }
}
