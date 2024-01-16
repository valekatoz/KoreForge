package net.kore.utils.api;

import net.kore.Kore;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class ServerUtils {
    public static List<String> changelog = new ArrayList<>();
    public static String logo = "https://kore.valekatoz.com/api/getLogo.php";
    public static void loadChangelog()
    {
        URL url2 = null;
        BufferedReader reader = null;

        // Tries fetching the home from server
        try {
            url2 = new URL("https://kore.valekatoz.com/api/getHome.php");

            HttpURLConnection connection = (HttpURLConnection) url2.openConnection();
            connection.setRequestProperty("User-Agent", "Kore/"+ Kore.VERSION); // custom UserAgent to skip cloudflare managed challenge
            connection.setRequestMethod("GET");

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                changelog.add(line);
            }
        } catch (IOException e) {
            if(Kore.clientSettings.debug.isEnabled()) {
                e.printStackTrace();
            }

            // If fetching the home from server failed we fetch it from local file
            try {reader = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("Kore", "home.txt")).getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    changelog.add(line);
                }

                reader.close();
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
    }
}
