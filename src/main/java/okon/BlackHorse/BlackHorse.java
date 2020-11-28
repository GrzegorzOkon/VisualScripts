package okon.BlackHorse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class BlackHorse extends Observable {
    private static final Logger logger = LogManager.getLogger(BlackHorse.class);
    private final boolean[] scripts = new boolean[BlackHorseWindow.scripts.size()];
    private final boolean[] schedulers = new boolean[BlackHorseWindow.hours.size()];
    private final boolean[] options = new boolean[2];
    private int tab = 0;
    private int hour = -1;
    private Timer timer = null;

    void setTab(int index) { tab = index; }

    boolean getScript(int index) { return scripts[index]; }

    void setScript(int index, boolean newState) { scripts[index] = newState; }

    void setHour(int index) { this.hour = index; }

    boolean getScheduler(int index) { return schedulers[index]; }

    void setScheduler(int index, boolean newState) { schedulers[index] = newState; }

    boolean getOption(int index) { return options[index]; };

    void setEven(boolean newState) {
        options[0] = newState;
        if (options[0] == true) {
            checkEven();
        }
    }

    void setOdd(boolean newState) {
        options[1] = newState;
        if (options[1] == true) {
            checkOdd();
        }
    }

    void cleanScript(int index, boolean newState) {
        scripts[index] = newState;
        setChanged();
        notifyObservers(new int[]{0, index});
    }

    void cleanHour() {
        if (isHourSelected()) {
            setChanged();
            notifyObservers(new int[]{1, hour});
            hour = -1;
        }
    }

    void cleanScheduler(int index, boolean newState) {
        schedulers[index] = newState;
        setChanged();
        notifyObservers(new int[]{2, index});
    }

    void cleanOption(int index, boolean newState) {
        options[index] = newState;
        setChanged();
        notifyObservers(new int[]{3, index});
    }

    void activateCleanButton() {
        switch (tab) {
            case 0:
                for (int i = 0; i < scripts.length; i++) {
                    if (scripts[i] == true) {
                        cleanScript(i, false);
                    }
                }
                break;
            case 1:
                cleanHour();
                break;
            case 2:
                for (int i = 0; i < schedulers.length; i++) {
                    if (schedulers[i] == true) {
                        cleanScheduler(i, false);
                    }
                }
                for (int j = 0; j < options.length; j++) {
                    if (options[j] == true) {
                        cleanOption(j, false);
                    }
                }
                break;
        }
    }

    void activateRunButton() {
        switch (tab) {
            case 0:
                for (int i = 0; i < scripts.length; i++) {
                    if (scripts[i] == true) {
                        new ScriptTask(BlackHorseWindow.scripts.get(i)).call();
                    }
                };
                break;
            case 1:
                if (isHourSelected()) {
                    new HourTask(BlackHorseWindow.hours.get(this.hour)).run();
                };
                break;
            case 2:
                cancelOldTasks();
                scheduleNewTasks();
                break;
        }
    }

    void activateCloseButton() {
        addAppToTray();
    }

    private void checkEven() {
        for (int i = 0; i < schedulers.length; i++) {
            if (schedulers[i] == false && isHourEven(BlackHorseWindow.hours.get(i))) {
                setScheduler(i, true);
            }
            setChanged();
            notifyObservers(new int[]{2, i});
        }
    }

    private void checkOdd() {
        for (int i = 0; i < schedulers.length; i++) {
            if (schedulers[i] == false && isHourOdd(BlackHorseWindow.hours.get(i))) {
                setScheduler(i, true);
            }
            setChanged();
            notifyObservers(new int[]{2, i});
        }
    }

    private void cancelOldTasks() {
        if (isTimerActivated() == true) {
            timer.cancel();
            timer.purge();
            logger.info("All tasks are cancelled");
        }
    }

    private void scheduleNewTasks() {
        scheduleTasks(orderTasks());
    }

    private void scheduleTasks(Map<Date, HourTask> orderedTasks) {
        timer = new Timer(ProgramVersion.getProgramName() + "Timer");
        for (Date date : orderedTasks.keySet()) {
            timer.schedule(orderedTasks.get(date), date);
            logger.info("A task '" + orderedTasks.get(date).getAlias() + "' is successfully added to a scheduler");
            logger.debug("A task '" + orderedTasks.get(date).getAlias() + "' will start on '" + date.toString() + "'");
        };
    }

    private Map<Date, HourTask> orderTasks() {
        Map<Date, HourTask> result = new TreeMap<>();
        for (int i = 0; i < schedulers.length; i++) {
            if (schedulers[i] == true) {
                HourTask task = new HourTask(BlackHorseWindow.hours.get(i));
                Date date = setScheduledTime(task);
                result.put(date, task);
            }
        }
        return result;
    }

    private Date setScheduledTime(HourTask task) {
        Date result = new Date();
        if (isHourElapsed(task)) {
            result.setDate(result.getDate() + 1);
        }
        result.setHours(Integer.valueOf(task.getDigits().substring(0, task.getDigits().indexOf(":"))));
        result.setMinutes(Integer.valueOf(task.getDigits().substring(task.getDigits().indexOf(":") + 1)));
        return result;
    }

    private boolean isTimerActivated() {
        if (timer == null)
            return false;
        return true;
    }

    private boolean isHourSelected() {
        return hour != -1 ? true : false;
    }

    private boolean isHourElapsed(HourTask task){
        if (Integer.valueOf(task.getDigits().substring(0, task.getDigits().indexOf(":"))) < new Date().getHours()) {
            return true;
        }
        return false;
    }

    private boolean isHourEven(Hour task){
        return Integer.valueOf(task.getDigits().substring(0, task.getDigits().indexOf(":"))) % 2 == 0 ? true : false;
    }

    private boolean isHourOdd(Hour task){
        return Integer.valueOf(task.getDigits().substring(0, task.getDigits().indexOf(":"))) % 2 == 1 ? true : false;
    }

    private void addAppToTray() {
        setChanged();
        notifyObservers(new int[]{4});
    }
}