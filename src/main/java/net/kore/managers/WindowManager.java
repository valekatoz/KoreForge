package net.kore.managers;

import net.kore.modules.Module;
import net.kore.ui.windows.HomeWindow;
import net.kore.ui.windows.ModuleWindow;
import net.kore.ui.windows.ThemeWindow;
import net.kore.ui.windows.Window;

import java.util.ArrayList;
import java.util.List;

public class WindowManager {
    public List<Window> windows = new ArrayList<Window>();

    public WindowManager() {
        this.windows.add(new HomeWindow());
        for (Module.Category category : Module.Category.values()) {
            this.windows.add(new ModuleWindow(category));
        }

        this.windows.add(new ThemeWindow());
    }

    public Window getDefaultWindow() {
        return this.windows.get(2);
    }
}
