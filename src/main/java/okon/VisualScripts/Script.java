package okon.VisualScripts;

public class Script {
    private final String interfaceName;
    private final String alias;
    private final String name;
    private final String path;
    private final String engine;

    public Script(String interfaceName, String alias, String name, String path, String engine) {
        this.interfaceName = interfaceName;
        this.alias = alias;
        this.name = name;
        this.path = path;
        this.engine = engine;
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

    public String getEngine() {
        return engine;
    }
}
