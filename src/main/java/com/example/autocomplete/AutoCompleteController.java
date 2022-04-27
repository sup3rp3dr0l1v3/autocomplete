package com.example.autocomplete;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import org.apache.tomcat.util.json.JSONParser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutoCompleteController {

    // Main method, servlet and JSON loading
    @RequestMapping(value = "/suggestions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> suggestions(@RequestParam("q") String q,
            @RequestParam("latitude") @Nullable Double latitude,
            @RequestParam("longitude") @Nullable Double longitude) {
        // Declaration of default empty objects
        Map<String, Object> suggestions = new HashMap<>();
        ArrayList<City> cities = new ArrayList<>();

        try {
            // Loading the JSON from static resources
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream in = classLoader.getResourceAsStream("static/csv.json");
            JsonReader jsonReader = new JsonReader(new InputStreamReader(in));

            // Calling method to bring cities
            cities = bringCities(jsonReader, q, latitude, longitude);

        } catch (Exception e) {
            e.printStackTrace();

        }
        // inserting the cities into "suggestions"
        suggestions.put("suggestions", cities);

        // We return the user the full or empty suggestions
        return suggestions;

    }

    // Method to extract and order the cities
    private ArrayList<City> bringCities(JsonReader reader, String q, Double latitude, Double longitude)
            throws IOException {

        //Declaring empty cities arraylist
        ArrayList<City> cities = new ArrayList<City>();

        // Begin the array
        reader.beginArray();
        
        // Iterate over objects in JSON
        while (reader.hasNext()) {
            reader.beginObject();
            // Initialize empty city
            City city = new City();
            // Flag to see if this city is candidate for searching
            boolean flag = false;
            // Iterate over properties in every JSON object
            while (reader.hasNext()) {
                String name = reader.nextName();
                // Chain of If and Else If to see if we are in the property that we need
                if (name.equals("name")) {
                    String temp = reader.nextString();
                    if (temp.toLowerCase().contains(q.toLowerCase())) {
                        // if we found a city that contains the words of the query, then we flag true
                        flag = true;
                        // also we put the name of the city on the city object
                        city.name = temp;
                    }
                } 
                // We will save the values of the JSON with the correspondent field in the object but only if we got the flag
                else if (name.equals("country") && flag) {
                    city.name = city.name + ", " + reader.nextString();
                } else if (name.equals("lat") && flag) {
                    city.latitude = reader.nextDouble();
                } else if (name.equals("long") && flag) {
                    city.longitude = reader.nextDouble();
                } 
                // If we don't have the flag or a property that we need we skip the property
                else {
                    reader.skipValue();
                }
            }
            // If we got the flag we save the city into the arraylist or else we nullify it
            if (flag) {
                cities.add(city);
            } else {
                city = null;
            }
            // We tell the reader to stop searching the object and follow on with the first while
            reader.endObject();
        }

        // We calculate the score for every city
        for (City city : cities) {
            // If it matches some part of the name it automatically has 0.5
            city.score = 0.5;
            // If user provided longitude or latitude, we compare it, and sum the result into the score
            if (longitude != null || latitude != null) {
                Double subs1 = Math.abs(longitude - city.longitude);
                Double subs2 = Math.abs(latitude - city.latitude);

                city.score += (0.5 * (subs1 - subs2));
            }
        }

        //Sorting cities by score
        Collections.sort(cities, (Comparator.<City>comparingDouble(city1 -> city1.score)
                .thenComparingDouble(city2 -> city2.score)));

        // We return the cities to the previous method
        return cities;
    }

}
