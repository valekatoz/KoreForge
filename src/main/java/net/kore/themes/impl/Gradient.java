package net.kore.themes.impl;

import net.kore.themes.Theme;
import net.kore.Kore;

import java.awt.*;

public class Gradient extends Theme {
    public Gradient()
    {
        super("Gradient");
    }

    @Override
    public Color getSecondary()
    {
        final float location = (float)((Math.cos((System.currentTimeMillis() * Kore.clickGui.shiftSpeed.getValue()) / 1000.0) + 1.0) * 0.5);
        if (!Kore.clickGui.hsb.isEnabled()) {
            return new Color((int)(Kore.clickGui.redShift1.getValue() + (Kore.clickGui.redShift2.getValue() - Kore.clickGui.redShift1.getValue()) * location), (int)(Kore.clickGui.greenShift1.getValue() + (Kore.clickGui.greenShift2.getValue() - Kore.clickGui.greenShift1.getValue()) * location), (int)(Kore.clickGui.blueShift1.getValue() + (Kore.clickGui.blueShift2.getValue() - Kore.clickGui.blueShift1.getValue()) * location));
        }
        final float[] c1 = Color.RGBtoHSB((int)Kore.clickGui.redShift1.getValue(), (int)Kore.clickGui.greenShift1.getValue(), (int)Kore.clickGui.blueShift1.getValue(), null);
        final float[] c2 = Color.RGBtoHSB((int)Kore.clickGui.redShift2.getValue(), (int)Kore.clickGui.greenShift2.getValue(), (int)Kore.clickGui.blueShift2.getValue(), null);
        return Color.getHSBColor(c1[0] + (c2[0] - c1[0]) * location, c1[1] + (c2[1] - c1[1]) * location, c1[2] + (c2[2] - c1[2]) * location);
    }

    @Override
    public Color getSecondary(int index)
    {
        final float location = (float)((Math.cos((index * 100 + System.currentTimeMillis() * Kore.clickGui.shiftSpeed.getValue()) / 1000.0) + 1.0) * 0.5);
        if (!Kore.clickGui.hsb.isEnabled()) {
            return new Color((int)(Kore.clickGui.redShift1.getValue() + (Kore.clickGui.redShift2.getValue() - Kore.clickGui.redShift1.getValue()) * location), (int)(Kore.clickGui.greenShift1.getValue() + (Kore.clickGui.greenShift2.getValue() - Kore.clickGui.greenShift1.getValue()) * location), (int)(Kore.clickGui.blueShift1.getValue() + (Kore.clickGui.blueShift2.getValue() - Kore.clickGui.blueShift1.getValue()) * location));
        }
        final float[] c1 = Color.RGBtoHSB((int)Kore.clickGui.redShift1.getValue(), (int)Kore.clickGui.greenShift1.getValue(), (int)Kore.clickGui.blueShift1.getValue(), null);
        final float[] c2 = Color.RGBtoHSB((int)Kore.clickGui.redShift2.getValue(), (int)Kore.clickGui.greenShift2.getValue(), (int)Kore.clickGui.blueShift2.getValue(), null);
        return Color.getHSBColor(c1[0] + (c2[0] - c1[0]) * location, c1[1] + (c2[1] - c1[1]) * location, c1[2] + (c2[2] - c1[2]) * location);
    }
}
