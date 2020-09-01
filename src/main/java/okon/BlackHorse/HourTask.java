package okon.BlackHorse;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HourTask extends TimerTask {
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
        startThreadPool(3);
    }

    private void addScriptsToPool() {
        for (int i = 0; i < getInterfaceNames().size(); i++) {
            String interfaceName = getInterfaceNames().get(i);
            for (int j = 0; j < BlackHorseWindow.scripts.size(); j++) {
                if (BlackHorseWindow.scripts.get(j).getInterfaceName().equals(interfaceName)) {
                    tasks.add(new ScriptTask(BlackHorseWindow.scripts.get(j)));
                }
            }
        }
    }

    private void startThreadPool(int nThreads) {
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        submitAll(executor, tasks);
    }

    private List<Future<Message>> submitAll(ExecutorService executor, Collection<? extends Callable> tasks) {
        List<Future<Message>> result = new ArrayList<>(tasks.size());
        for (Callable task : tasks) {
            result.add(executor.submit(task));
        }
        return result;
    }
}