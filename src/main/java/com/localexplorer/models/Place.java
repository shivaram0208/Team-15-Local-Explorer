package com.localexplorer.models;

public class Place {
    private int placeId;
    private String name;
    private String category;
    private double latitude;
    private double longitude;
    private double avgRating;
    private int priceLevel;
    private boolean studentDiscount;

    public Place() {}

    public Place(int placeId, String name, String category, double latitude, double longitude,
                 double avgRating, int priceLevel, boolean studentDiscount) {
        this.placeId = placeId;
        this.name = name;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
        this.avgRating = avgRating;
        this.priceLevel = priceLevel;
        this.studentDiscount = studentDiscount;
    }

    public int getPlaceId() { return placeId; }
    public void setPlaceId(int placeId) { this.placeId = placeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }

    public int getPriceLevel() { return priceLevel; }
    public void setPriceLevel(int priceLevel) { this.priceLevel = priceLevel; }

    public boolean isStudentDiscount() { return studentDiscount; }
    public void setStudentDiscount(boolean studentDiscount) { this.studentDiscount = studentDiscount; }

    /**
     * Haversine distance in kilometers from a given lat/lng to this place.
     */
    public double distanceFromKm(double fromLat, double fromLng) {
        final int R = 6371;
        double dLat = Math.toRadians(latitude - fromLat);
        double dLng = Math.toRadians(longitude - fromLng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(fromLat)) * Math.cos(Math.toRadians(latitude))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    public String toString() {
        return name + " (" + category + ") - ★" + avgRating;
    }
}
