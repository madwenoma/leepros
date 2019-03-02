package nio.reactor;

public abstract class EventHandler {

    private InputSource source;

    public abstract void handle(Event event);

    public InputSource getSource() {
        return source;
    }

    public void setSource(InputSource source) {
        this.source = source;
    }
}

