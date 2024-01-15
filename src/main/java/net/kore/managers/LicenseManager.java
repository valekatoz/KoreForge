package net.kore.managers;

import net.kore.Kore;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class LicenseManager {
    private boolean hasConnected = false;
    private boolean isPremium = false;
    public LicenseManager() {
        if(Kore.mc.getSession().getPlayerID() != null && this.checkLicense(Kore.mc.getSession().getPlayerID())) {
            isPremium = true;
        }
    }

    public boolean isPremium() {
        return isPremium;
    }

    public boolean hasConnected() {
        return hasConnected;
    }

    public void setConnected(boolean value) {
        this.hasConnected = value;
    }

    public void disconnect() {
        this.isPremium = false;
    }

    public boolean checkLicense(String uuid) {
        try {
            URL url = new URL("https://kore.valekatoz.com/api/checkLicense.php?key="+ Base64.getEncoder().encodeToString(uuid.getBytes()));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Kore/"+ Kore.VERSION); // custom UserAgent to skip cloudflare managed challenge
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            connection.disconnect();

            return parseJsonResponse(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean parseJsonResponse(String jsonResponse) {
        try {
            int statusIndex = jsonResponse.indexOf("\"status\":\"");

            if (statusIndex != -1) {
                int start = statusIndex + "\"status\":\"".length();
                int end = jsonResponse.indexOf("\"", start);
                String status = jsonResponse.substring(start, end);

                return "success".equalsIgnoreCase(status);
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
