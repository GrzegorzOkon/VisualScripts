package okon.VisualScripts;

import java.util.List;
import java.util.TimerTask;

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
        for (int i = 0; i < getInterfaceNames().size(); i++) {
            String interfaceName = getInterfaceNames().get(i);
            for (int j = 0; j < VisualScriptsWindow.scripts.size(); j++) {
                if (VisualScriptsWindow.scripts.get(j).getInterfaceName().equals(interfaceName)) {
                    if (VisualScriptsWindow.scripts.get(j).getEngine().toLowerCase().equals("java")) {
                        new JarRunner().run(VisualScriptsWindow.scripts.get(j));
                    } else if (VisualScriptsWindow.scripts.get(i).getEngine().toLowerCase().equals("perl")) {
                        new PlRunner().run(VisualScriptsWindow.scripts.get(j));
                    }
                }
            }
        }
    }
}
