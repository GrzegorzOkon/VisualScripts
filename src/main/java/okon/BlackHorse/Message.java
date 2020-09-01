package okon.BlackHorse;

public class Message {
    private final String name;
    private final String result;

    public Message(String name, String result) {
        this.name = name;
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public String getResult() {
        return result;
    }
}
