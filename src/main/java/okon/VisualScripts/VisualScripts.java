package okon.VisualScripts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Observable;
import java.util.Timer;

public class VisualScripts extends Observable {
    private static final Logger logger = LogManager.getLogger(VisualScripts.class);
    private int tab = 0;
    private boolean[] scripts = new boolean[VisualScriptsWindow.scripts.size()];
    private int hour = -1;
    private boolean[] schedulers = new boolean[VisualScriptsWindow.hours.size()];
    private boolean even = false;
    private boolean odd = false;

    void setTab(int index) { tab = index; }

    void setScript(int index, boolean newState) { scripts[index] = newState; }

    boolean getScript(int index) { return scripts[index]; }

    void setHour(int index) { this.hour = index; }

    void setScheduler(int index, boolean newState) { schedulers[index] = newState; }

    boolean getScheduler(int index) { return schedulers[index]; }

    void setEven(boolean newState) {
        even = newState;
        if (even == true) {
            checkEven();
        }
    }

    void setOdd(boolean newState) {
        odd = newState;
        if (odd == true) {
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
                };
                break;
        }
    }

    void activateRunButton() {
        switch (tab) {
            case 0:
                for (int i = 0; i < scripts.length; i++) {
                    if (scripts[i] == true) {
                        if (VisualScriptsWindow.scripts.get(i).getEngine().toLowerCase().equals("java")) {
                            new JarRunner().run(VisualScriptsWindow.scripts.get(i));
                        } else if (VisualScriptsWindow.scripts.get(i).getEngine().toLowerCase().equals("perl")) {
                            new PlRunner().run(VisualScriptsWindow.scripts.get(i));
                        }
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