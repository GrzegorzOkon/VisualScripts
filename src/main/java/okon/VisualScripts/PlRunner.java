package okon.VisualScripts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class PlRunner {
    private static final Logger logger = LogManager.getLogger(PlRunner.class);

    public static void run(Script script) {
        ProcessBuilder pb = new ProcessBuilder("perl", script.getName() + ".pl");
        pb.directory(new File(script.getPath()));
        try {
            Process p = pb.start();
            logger.info(script.getName());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(script.getName() + " - " + e.getMessage());
        }
    }
}