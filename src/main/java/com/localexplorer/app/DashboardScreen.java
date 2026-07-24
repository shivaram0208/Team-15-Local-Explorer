package com.localexplorer.app;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.localexplorer.utilities.SceneManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Dashboard screen: UI wiring + weather fetch + navigation all in one file.
 *
 * >>> Paste a free OpenWeather key from https://openweathermap.org/api below
 * to get live weather. Without one, a placeholder is shown instead. <<<
 */
public class DashboardScreen {

    private static final String OPENWEATHER_API_KEY = "YOUR_OPENWEATHER_API_KEY";
    private static final String CITY = "Coimbatore";

    @FXML private Label welcomeLabel;
    @FXML private Label weatherLabel;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome back!");
        loadWeatherAsync();
    }

    private void loadWeatherAsync() {
        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                return fetchWeather();
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> weatherLabel.setText(task.getValue())));
        executor.submit(task);
    }

    private String fetchWeather() {
        if (OPENWEATHER_API_KEY.equals("YOUR_OPENWEATHER_API_KEY")) {
            return "Add your OpenWeather API key in DashboardScreen.java for live weather";
        }
        try {
            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + CITY
                    + "&appid=" + OPENWEATHER_API_KEY + "&units=metric";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            double temp = json.getAsJsonObject("main").get("temp").getAsDouble();
            String desc = json.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();
            String name = json.get("name").getAsString();

            return name + ": " + temp + "\u00B0C, " + desc;
        } catch (Exception e) {
            e.printStackTrace();
            return "Weather unavailable right now";
        }
    }

    @FXML
    private void openMap() {
        executor.shutdownNow();
        SceneManager.switchTo("/com/localexplorer/fxml/Map.fxml", "Local Explorer AI Pro - Map");
    }

    @FXML
    private void openBudget() {
        executor.shutdownNow();
        SceneManager.switchTo("/com/localexplorer/fxml/Budget.fxml", "Local Explorer AI Pro - Budget");
    }

    @FXML
    private void openEmergency() {
        executor.shutdownNow();
        SceneManager.switchTo("/com/localexplorer/fxml/Emergency.fxml", "Local Explorer AI Pro - Emergency");
    }
}
