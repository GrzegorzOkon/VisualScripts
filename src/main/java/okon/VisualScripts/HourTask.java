package okon.VisualScripts;

import java.util.List;
import java.util.TimerTask;

import static okon.VisualScripts.VisualScriptsWindow.scriptQueue;

public class HourTask extends TimerTask {
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
        startThreadPool(scriptQueue.size());
    }

    private void addScriptsToPool() {
        for (int i = 0; i < getInterfaceNames().size(); i++) {
            String interfaceName = getInterfaceNames().get(i);
            for (int j = 0; j < VisualScriptsWindow.scripts.size(); j++) {
                if (VisualScriptsWindow.scripts.get(j).getInterfaceName().equals(interfaceName)) {
                    VisualScriptsWindow.scriptQueue.add(VisualScriptsWindow.scripts.get(j));
                }
            }
        }
    }

    private void startThreadPool(int threadSum) {
        Thread[] threads = new Thread[threadSum];
        for (int i = 0; i < threadSum; i++) {
            threads[i] = new Thread(new ScriptConsumerThread());
        }
        for (int i = 0; i < threadSum; i++) {
            threads[i].start();
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                throw new AppException(e);
            }
        }
    }
}
