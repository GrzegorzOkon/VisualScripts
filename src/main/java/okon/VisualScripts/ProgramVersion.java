package okon.VisualScripts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProgramVersion {
    private static final Logger logger = LogManager.getLogger(ProgramVersion.class);
    private static String programName = "VisualScripts";
    private static String major = "1";
    private static String minor = "2";
    private static String release = "5";
    private static String revision = "20191121";

    public static String getTitleDescription() {
        logger.info("*** " + programName + " version " + major + "." + minor + "." + release + " ***");
        return programName + " version " + major + "." + minor + "." + release + " (rev. " + revision + ")";
    }
}