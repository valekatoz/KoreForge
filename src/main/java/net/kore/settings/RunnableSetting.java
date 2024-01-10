package net.kore.settings;

public class RunnableSetting extends Setting
{
    private final Runnable runnable;

    public RunnableSetting(final String name, final Runnable runnable) {
        super(name);
        this.runnable = runnable;
    }

    public void execute() {
        this.runnable.run();
    }
}
