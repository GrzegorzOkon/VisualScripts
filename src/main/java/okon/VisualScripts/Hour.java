package okon.VisualScripts;

import java.util.List;

public class Hour {
    private final String alias;
    private final String digits;
    private final List<String> interfaceNames;

    public Hour(String alias, String digits, List<String> interfaceNames) {
        this.alias = alias;
        this.digits = digits;
        this.interfaceNames = interfaceNames;
    }

    public String getAlias() {
        return alias;
    }

    public String getDigits() {
        return digits;
    }

    public List<String> getInterfaceNames() {
        return interfaceNames;
    }
}
