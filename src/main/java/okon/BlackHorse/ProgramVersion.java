package okon.BlackHorse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ProgramVersion {
    private static final Logger logger = LogManager.getLogger(ProgramVersion.class);
    private static String programName = "BlackHorse";
    private static String major = "1";
    private static String minor = "3";
    private static String release = "7";
    private static String revision = "20201206";

    public static String getTitleDescription() {
        logger.info("*** " + programName + " version " + major + "." + minor + "." + release + " ***");
        return programName + " version " + major + "." + minor + "." + release + " (rev. " + revision + ")";
    }

    public static String getProgramName() {
        return programName;
    }
}