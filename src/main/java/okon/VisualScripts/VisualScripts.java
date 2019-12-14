package okon.VisualScripts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Observable;
import java.util.Timer;

public class VisualScripts extends Observable {
    private static final Logger logger = LogManager.getLogger(VisualScripts.class);
    private final boolean[] scripts = new boolean[VisualScriptsWindow.scripts.size()];
    private final boolean[] schedulers = new boolean[VisualScriptsWindow.hours.size()];
    private final boolean[] options = new boolean[2];
    private int tab = 0;
    private int hour = -1;

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
                        new ScriptConsumerThread().run(VisualScriptsWindow.scripts.get(i));
                    }
                };
                break;
            case 1:
                if (isHourSelected()) {
                    new HourTask(VisualScriptsWindow.hours.get(this.hour)).run();
                };
                break;
            case 2:
                Timer timer = new Timer();
                for (int i = 0; i < schedulers.length; i++) {
                    if (schedulers[i] == true) {
                        HourTask task = new HourTask(VisualScriptsWindow.hours.get(i));
                        Date date = setScheduledTime(task);
                        timer.schedule(task, date);
                        logger.info("Task '" + task.getAlias() + "' is succesfully added to a scheduler.");
                        logger.debug("Task '" + task.getAlias() + "' will start on '" + date.toString() + "'.");
                    }
                };
                break;
        }
    }

    private void checkEven() {
        for (int i = 0; i < schedulers.length; i++) {
            if (schedulers[i] == false && isHourEven(VisualScriptsWindow.hours.get(i))) {
                setScheduler(i, true);
            }
            setChanged();
            notifyObservers(new int[]{2, i});
        }
    }

    private void checkOdd() {
        for (int i = 0; i < schedulers.length; i++) {
            if (schedulers[i] == false && isHourOdd(VisualScriptsWindow.hours.get(i))) {
                setScheduler(i, true);
            }
            setChanged();
            notifyObservers(new int[]{2, i});
        }
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

    private boolean isHourSelected () {
        return hour != -1 ? true : false;
    }

    private boolean isHourElapsed (HourTask task){
        if (Integer.valueOf(task.getDigits().substring(0, task.getDigits().indexOf(":"))) < new Date().getHours()) {
            return true;
        }
        return false;
    }

    private boolean isHourEven (Hour task){
        return Integer.valueOf(task.getDigits().substring(0, task.getDigits().indexOf(":"))) % 2 == 0 ? true : false;
    }

    private boolean isHourOdd (Hour task){
        return Integer.valueOf(task.getDigits().substring(0, task.getDigits().indexOf(":"))) % 2 == 1 ? true : false;
    }
}