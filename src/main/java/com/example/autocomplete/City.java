package com.example.autocomplete;

// Empty city class for representation of object that we are going to return
public class City {
    // fields
    String name;
    Double latitude;
    Double longitude;
    Double score;
    
    //constructor
    public City(String name, Double latitude, Double longitude, Double score) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.score = score;
    }
    
    //default constructor
    public City(){
        
    }

    //Getters and setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public Double getScore() {
        return score;
    }
    public void setScore(Double score) {
        this.score = score;
    }

    
}
