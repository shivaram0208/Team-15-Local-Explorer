package com.localexplorer.app;

import com.localexplorer.utilities.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

/**
 * Emergency screen: simple one-click actions, all logic inline.
 */
public class EmergencyScreen {

    @FXML private Label statusLabel;

    @FXML
    private void callHospital() {
        showAlert("Hospital", "Nearest: Coimbatore Medical College Hospital\nDialing 108...");
    }

    @FXML
    private void callPolice() {
        showAlert("Police", "Dialing 100...");
    }

    @FXML
    private void callFire() {
        showAlert("Fire Department", "Dialing 101...");
    }

    @FXML
    private void callBloodBank() {
        showAlert("Blood Bank", "Nearest blood bank located. Dialing helpline...");
    }

    @FXML
    private void sendLocation() {
        // In production this would read real GPS coordinates and push them
        // to saved emergency contacts via a notification/socket module.
        statusLabel.setText("Current location sent to your emergency contacts.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void backToDashboard() {
        SceneManager.switchTo("/com/localexplorer/fxml/Dashboard.fxml", "Local Explorer AI Pro - Dashboard");
    }
}
