import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.URL;
public class Weather {

    public static void main() {
        String apiKey = "7236453d-0f12-440a-afae-36c9fd0d5718";
        double lat = 55.6;
        double lon = 37.9;
        int lim = 7;

        try {
            // Создаем URL запроса
            String urlString = String.format("https://api.weather.yandex.ru/v2/forecast?lat=%s&lon=%s&limit=%d", lat, lon, lim);
            URL url = new URL(urlString);

            // Открываем соединение
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Yandex-Weather-Key", apiKey);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            BufferedWriter writer = new BufferedWriter(new FileWriter("output.json"));

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                writer.write(inputLine);
            }
            in.close();
            writer.close();

            String jsonResponse = response.toString();
            System.out.println("Полный ответ JSON: " + jsonResponse);

            int tempIndex = jsonResponse.indexOf("\"temp\":") + 7;
            int endIndex = jsonResponse.indexOf(",", tempIndex);
            int currentTemperature = Integer.parseInt(jsonResponse.substring(tempIndex, endIndex));
            System.out.println("Текущая температура: " + currentTemperature + "°C");

            int totalTemperature = 0;
            int count = 0;
            int index = jsonResponse.indexOf("\"parts\":");
            while ((index = jsonResponse.indexOf("\"temp_avg\":", index)) != -1) {
                tempIndex = index + 11;
                endIndex = jsonResponse.indexOf(",", tempIndex);

                if (endIndex == -1) {
                    endIndex = jsonResponse.length();
                }
                int temperature = Integer.parseInt(jsonResponse.substring(tempIndex, endIndex));
                totalTemperature += temperature;
                count++;
                index = jsonResponse.indexOf("\"parts\":", index);
                if(index == -1) {
                    index = jsonResponse.length();
                }
            }

            if (count > 0) {
                double averageTemperature = (double) totalTemperature / count;
                System.out.println(String.format("Средняя температура на %s дней: %.1f°C", lim, averageTemperature));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
