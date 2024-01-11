package net.kore.utils.font;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class Fonts
{
    private static Map<String, Font> fontCache;
    public static MinecraftFontRenderer tenacity;
    public static MinecraftFontRenderer tenacityBold;
    public static MinecraftFontRenderer icon;
    public static MinecraftFontRenderer newIcons;

    public static MinecraftFontRenderer getPrimary()
    {
        return tenacity;
    }

    public static MinecraftFontRenderer getSecondary()
    {
        return tenacityBold;
    }

    private static Font getFont(final String location, final int size) {
        Font font;
        try {
            if (Fonts.fontCache.containsKey(location)) {
                font = Fonts.fontCache.get(location).deriveFont(0, (float)size);
            }
            else {
                final InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("Kore", "fonts/" + location)).getInputStream();

                font = Font.createFont(0, is);
                Fonts.fontCache.put(location, font);
                font = font.deriveFont(0, (float)size);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("arial", 0, size);
        }
        return font;
    }

    public static void downloadFont(URL downloadUrl, Path filePath) throws IOException {
        try (InputStream in = downloadUrl.openStream())
        {
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void bootstrap() {
        Fonts.tenacity = new MinecraftFontRenderer(getFont("new_font.ttf", 19), true, false);
        Fonts.tenacityBold = new MinecraftFontRenderer(getFont("tenacity-bold.ttf", 19), true, false);
        Fonts.icon = new MinecraftFontRenderer(getFont("icon.ttf", 20), true, false);
        Fonts.newIcons = new MinecraftFontRenderer(getFont("new_icons.ttf", 24), true, false);
    }

    static {
        Fonts.fontCache = new HashMap<String, Font>();
    }
}
