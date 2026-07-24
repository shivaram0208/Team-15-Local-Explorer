package com.localexplorer.app;

import com.localexplorer.dao.DatabaseConnection;
import com.localexplorer.models.Place;
import com.localexplorer.utilities.SceneManager;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Map screen: UI wiring + DB queries + recommendation scoring all in one file.
 * Talks to MySQL directly via DatabaseConnection - no separate DAO/Service layer.
 */
public class MapScreen {

    @FXML private WebView mapView;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ListView<Place> resultsList;

    // Reference point for distance scoring: Coimbatore city center.
    // Swap for real GPS/geolocation later.
    private final double userLat = 11.0168;
    private final double userLng = 76.9558;

    private WebEngine engine;

    @FXML
    public void initialize() {
        engine = mapView.getEngine();

        loadCategories();
        categoryFilter.setOnAction(e -> loadPlaces());

        // IMPORTANT: only push markers into the page after map.html has
        // actually finished loading - calling executeScript any earlier
        // throws, because the JS functions don't exist in the page yet.
        // The map.html itself handles its own render-timing fix internally
        // (via setTimeout/ResizeObserver), so no Thread.sleep is needed here -
        // sleeping on this thread would freeze the whole UI.
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                loadPlaces();
            }
        });
        engine.load(getClass().getResource("/com/localexplorer/map/map.html").toExternalForm());
    }

    /**
     * Populates the category dropdown directly from whatever categories exist
     * in the database, instead of a hardcoded list - so newly added place
     * categories show up automatically without touching this file again.
     */
    private void loadCategories() {
        categoryFilter.getItems().clear();
        categoryFilter.getItems().add("All");

        String sql = "SELECT DISTINCT category FROM places ORDER BY category";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categoryFilter.getItems().add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        categoryFilter.setValue("All");
    }

    private void loadPlaces() {
        String category = categoryFilter.getValue();
        List<Place> places = "All".equals(category) ? fetchAllPlaces() : fetchPlacesByCategory(category);

        List<Place> ranked = rankByScore(places, 20);
        resultsList.getItems().setAll(ranked);

        StringBuilder js = new StringBuilder("clearMarkers();");
        for (Place p : ranked) {
            js.append(String.format(
                    Locale.US,
                    "addMarker(%f, %f, '%s');",
                    p.getLatitude(), p.getLongitude(), escapeJs(p.getName() + " (" + p.getCategory() + ")")
            ));
        }
        // Must match the function name defined in map.html exactly.
        js.append("fitAllMarkers();");
        engine.executeScript(js.toString());
    }

    // ---- Direct DB access (no DAO/Service indirection) ----

    private List<Place> fetchAllPlaces() {
        List<Place> places = new ArrayList<>();
        String sql = "SELECT * FROM places";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                places.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return places;
    }

    private List<Place> fetchPlacesByCategory(String category) {
        List<Place> places = new ArrayList<>();
        String sql = "SELECT * FROM places WHERE category = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    places.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return places;
    }

    private Place mapRow(ResultSet rs) throws SQLException {
        return new Place(
                rs.getInt("place_id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getDouble("latitude"),
                rs.getDouble("longitude"),
                rs.getDouble("avg_rating"),
                rs.getInt("price_level"),
                rs.getBoolean("student_discount")
        );
    }

    // ---- Recommendation scoring inline (rating 50% + proximity 35% + discount 15%) ----

    private List<Place> rankByScore(List<Place> candidates, int topN) {
        return candidates.stream()
                .sorted(Comparator.comparingDouble((Place p) -> -score(p)))
                .limit(topN)
                .collect(Collectors.toList());
    }

    private double score(Place p) {
        double distanceKm = p.distanceFromKm(userLat, userLng);
        double proximityScore = 1.0 / (1.0 + distanceKm);
        double ratingScore = p.getAvgRating() / 5.0;
        double discountBonus = p.isStudentDiscount() ? 1.0 : 0.0;
        return (ratingScore * 0.5) + (proximityScore * 0.35) + (discountBonus * 0.15);
    }

    private String escapeJs(String s) {
        return s.replace("'", "\\'");
    }

    @FXML
    private void backToDashboard() {
        SceneManager.switchTo("/com/localexplorer/fxml/Dashboard.fxml", "Local Explorer AI Pro - Dashboard");
    }
}
