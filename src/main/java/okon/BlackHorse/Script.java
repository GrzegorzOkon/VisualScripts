package okon.BlackHorse;

import java.util.List;

public class Script {
    private final String interfaceName;
    private final String alias;
    private final String name;
    private final String path;
    private final List<String> command;

    public Script(String interfaceName, String alias, String name, String path, List<String> command) {
        this.interfaceName = interfaceName;
        this.alias = alias;
        this.name = name;
        this.path = path;
        this.command = command;
    }

    public String getInterfaceName() { return interfaceName; }

    public String getAlias() {
        return alias;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public List<String> getCommand() { return command; }
}
