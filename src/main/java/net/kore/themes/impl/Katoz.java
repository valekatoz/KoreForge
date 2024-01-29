package net.kore.themes.impl;

import net.kore.themes.Theme;
import net.kore.Kore;

import java.awt.*;

public class Katoz extends Theme {
    public Katoz()
    {
        super("Katoz");
    }

    double redShift1 = 212.0;
    double redShift2 = 251.0;

    double greenShift1 = 20.0;
    double greenShift2 = 176.0;

    double blueShift1 = 90.0;
    double blueShift2 = 59.9;

    @Override
    public Color getSecondary()
    {
        final float location = (float)((Math.cos((System.currentTimeMillis() * Kore.clickGui.shiftSpeed.getValue()) / 1000.0) + 1.0) * 0.5);
        return new Color((int)(redShift1 + (redShift2 - redShift1) * location), (int)(greenShift1 + (greenShift2 - greenShift1) * location), (int)(blueShift1 + (blueShift2 - blueShift1) * location));
    }

    @Override
    public Color getSecondary(int index)
    {
        final float location = (float)((Math.cos((index * 100 + System.currentTimeMillis() * Kore.clickGui.shiftSpeed.getValue()) / 1000.0) + 1.0) * 0.5);
        return new Color((int)(redShift1 + (redShift2 - redShift1) * location), (int)(greenShift1 + (greenShift2 - greenShift1) * location), (int)(blueShift1 + (blueShift2 - blueShift1) * location));
    }
}
