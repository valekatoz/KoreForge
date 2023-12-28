package katoz.kore.utils.hud;

import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.hud.TextHud;
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack;
import cc.polyfrost.oneconfig.renderer.TextRenderer;

import java.util.List;

public abstract class WatermarkHud extends TextHud {
    /**
     * @param enabled      If the render is enabled
     * @param x            X-coordinate of render on a 1080p display
     * @param y            Y-coordinate of render on a 1080p display
     * @param scale        Scale of the render
     * @param background   If the HUD should have a background
     * @param rounded      If the corner is rounded or not
     * @param cornerRadius Radius of the corner
     * @param paddingX     X-Padding of the HUD
     * @param paddingY     Y-Padding of the HUD
     * @param bgColor      Background color
     * @param border       If the render has a border or not
     * @param borderSize   Thickness of the border
     * @param borderColor  The color of the border
     */
    public WatermarkHud(String title, boolean enabled, float x, float y, float scale, boolean background, boolean rounded, float cornerRadius, float paddingX, float paddingY, OneColor bgColor, boolean border, float borderSize, OneColor borderColor) {
        super(enabled, x, y, scale, background, rounded, cornerRadius, paddingX, paddingY, bgColor, border, borderSize, borderColor);
        this.title = title;
    }

    public WatermarkHud(String title, boolean enabled, int x, int y) {
        this(title, enabled, x, y, 1f, true, false, 2, 5, 5, new OneColor(0, 0, 0, 120), false, 2, new OneColor(0, 0, 0));
    }

    public WatermarkHud(String title, boolean enabled) {
        this(title, enabled, 0, 0);
    }

    /**
     * This function is called every tick
     *
     * @return The new text
     */
    protected abstract String getText(boolean example);

    /**
     * This function is called every frame
     *
     * @return The new text, null to use the cached value
     */
    protected String getTextFrequent(boolean example) {
        return null;
    }

    @Override
    protected void getLines(List<String> lines, boolean example) {
        lines.add(getCompleteText(getText(example)));
    }

    @Override
    protected void getLinesFrequent(List<String> lines, boolean example) {
        String text = getTextFrequent(example);
        if (text == null) return;
        lines.clear();
        lines.add(getCompleteText(text));
    }

    @Override
    public void draw(UMatrixStack matrices, float x, float y, float scale, boolean example) {
        float textX = x;
        if (brackets) {
            drawLine("[", textX, y, bracketsColor, scale);
            textX += getLineWidth("[", scale);
        }
        drawLine(lines.get(0), textX, y, scale);
        if (brackets) {
            textX += getLineWidth(lines.get(0), scale);
            drawLine("]", textX, y, bracketsColor, scale);
        }
    }

    protected void drawLine(String line, float x, float y, OneColor color, float scale) {
        TextRenderer.drawScaledString(line, x, y, color.getRGB(), TextRenderer.TextType.toType(textType), scale);
    }

    protected final String getCompleteText(String text) {
        boolean showTitle = !title.trim().isEmpty();
        StringBuilder builder = new StringBuilder();

        if (showTitle && titleLocation == 0 && getText(true).isEmpty()) {
            builder.append(title).append("");
        } else if (showTitle && titleLocation == 0) {
            builder.append(title).append(" ");
        }

        builder.append(text);

        if (showTitle && titleLocation == 1 && getText(true).isEmpty()) {
            builder.append("").append(title);
        } else if (showTitle && titleLocation == 1) {
            builder.append(" ").append(title);
        }

        return builder.toString();
    }

    @Override
    protected float getWidth(float scale, boolean example) {
        if (lines == null) return 0f;
        if (!brackets) return getLineWidth(lines.get(0), scale);
        return getLineWidth("[" + lines.get(0) + "]", scale);
    }
    protected boolean brackets = false;
    protected OneColor bracketsColor = new OneColor(0xFFFFFFFF);
    protected String title;
    protected int titleLocation = 0;
}