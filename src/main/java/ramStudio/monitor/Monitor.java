package ramStudio.monitor;

import org.json.JSONArray;
import org.json.JSONObject;
import ramStudio.extensions.CityCodes;
import ramStudio.extensions.Coordinates;
import ramStudio.extensions.StringPair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Monitor {
    private static String token = "08cd4d10-9b89-4df3-9c87-35ce100da2f7";

    //TODO: обработать все null возвращаемые типы.
    public static void main(String[] args) {
        int limit = 1;
        Scanner scanner = new Scanner(System.in);
        Coordinates coords = new Coordinates();
        var coordsPair = coords.get(CityCodes.SPB);

        try (scanner) {
            System.out.println("Enter the number of days to forecast: ");
            limit = scanner.nextInt();

            var url = buildUrl(coordsPair, limit);
            var json = getResponseJson(sendGETRequest(url, "X-Yandex-Weather-Key"));
            parseJson(json, limit);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static URL buildUrl(StringPair coordsPair, int dayLimit) throws MalformedURLException {
        String urlString = "https://api.weather.yandex.ru/v2/forecast?lat=" + coordsPair.getLat() + "&lon=" + coordsPair.getLon()
                + "&limit=" + dayLimit + "&extra=true";
        return new URL(urlString);
    }

    private static HttpURLConnection sendGETRequest(URL url, String header) throws IOException {
        if (header.isBlank())
            throw new RuntimeException("Header is empty or white space. This is not allowed");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty(header, token);

        return connection;
    }

    private static String getResponseJson(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseContent = new StringBuilder();

            String input;
            while ((input = reader.readLine()) != null) {
                responseContent.append(input);
            }
            reader.close();

            System.out.println("responseContent = " + responseContent);
            return responseContent.toString();
        } else {
            System.out.println("GET request failed. Code = " + responseCode);
        }

        return null;
    }

    private static void parseJson(String json, int dayLimit) {
        JSONObject jsonObject = new JSONObject(json);
        JSONObject fact = jsonObject.getJSONObject("fact");
        float temperature = fact.getFloat("temp");
        System.out.println("current temperature = " + temperature);

        JSONArray forecasts = jsonObject.getJSONArray("forecasts");
        float sumTemperature = 0;
        int hoursCount = 0;

        for (int i = 0; i < forecasts.length(); i++) {
            JSONObject dayForecast = forecasts.getJSONObject(i);
            JSONArray hoursArray = dayForecast.getJSONArray("hours");

            for (int j = 0; j < hoursArray.length(); j++) {
                JSONObject hourObj = hoursArray.getJSONObject(j);
                if (hourObj.has("temp")) {
                    int hourTemp = hourObj.getInt("temp");
                    sumTemperature += hourTemp;
                    hoursCount++;
                }
            }
        }

        if (hoursCount > 0) {
            float averageTemperature = sumTemperature / hoursCount;
            System.out.printf("Average temperature over %d day(s): %.2f°C%n ", dayLimit, averageTemperature);
        } else {
            System.out.println("Something went wrong while calculate average temperature");
        }
    }
}
