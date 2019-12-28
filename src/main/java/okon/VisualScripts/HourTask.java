package okon.VisualScripts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HourTask extends TimerTask {
    private static final Logger logger = LogManager.getLogger(HourTask.class);
    private final Queue<ScriptTask> tasks = new LinkedList<>();
    private final Hour hour;

    public HourTask(Hour hour) { this.hour = hour; }

    public String getAlias() {
        return hour.getAlias();
    }

    public String getDigits() {
        return hour.getDigits();
    }

    public List<String> getInterfaceNames() {
        return hour.getInterfaceNames();
    }

    @Override
    public void run() {
        addScriptsToPool();
        startThreadPool();
    }

    private void addScriptsToPool() {
        for (int i = 0; i < getInterfaceNames().size(); i++) {
            String interfaceName = getInterfaceNames().get(i);
            for (int j = 0; j < VisualScriptsWindow.scripts.size(); j++) {
                if (VisualScriptsWindow.scripts.get(j).getInterfaceName().equals(interfaceName)) {
                    tasks.add(new ScriptTask(VisualScriptsWindow.scripts.get(j)));
                }
            }
        }
    }

    private void startThreadPool() {
        ExecutorService service = null;
        try {
            List<Future<Boolean>> futures = Executors.newFixedThreadPool(3).invokeAll(tasks);
        } catch (Exception e) {
            logger.error("Exception in hour " + hour.getAlias()+ " - " + e.getMessage());
        } finally {
            if (service != null) service.shutdown();
        }
    }
}
