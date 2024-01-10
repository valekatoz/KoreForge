package net.kore.themes;

import java.awt.*;

public class Theme {
    private Color primary;
    private Color secondary;
    public String name;
    public Theme(String name, Color primaryColor, Color secondaryColor)
    {
        this.primary = primaryColor;
        this.secondary = secondaryColor;
        this.name = name;
    }

    public Theme(String name)
    {
        this.name = name;
        this.primary = new Color(50, 50, 50);
    }

    public Color getPrimary()
    {
        return primary;
    }
    public Color getSecondary()
    {
        return secondary;
    }
    public Color getSecondary(int index)
    {
        return secondary;
    }
}
