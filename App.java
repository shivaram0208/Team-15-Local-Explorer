package com.localexplorer;

import com.localexplorer.utilities.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager.setPrimaryStage(primaryStage);
        // No login screen - opens straight into the Dashboard
        SceneManager.switchTo("/com/localexplorer/fxml/Dashboard.fxml", "Local Explorer AI Pro - Dashboard");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
