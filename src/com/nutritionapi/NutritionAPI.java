package com.nutritionapi;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Nutrition Tracker API Client
 * 
 * A simple wrapper for the Nutrition Tracker API on RapidAPI.
 * Get 25+ nutrients from natural language food queries.
 * 
 * Usage:
 *     NutritionAPI api = new NutritionAPI("YOUR_RAPIDAPI_KEY");
 *     Map<String, Object> result = api.calculate("100g chicken breast");
 *     System.out.println(result.get("Protein"));
 */
public class NutritionAPI {
    
    private static final String BASE_URL = "https://nutrition-tracker-api.p.rapidapi.com";
    private final String apiKey;
    private final HttpClient client;
    
    /**
     * Initialize the Nutrition API client.
     * 
     * @param apiKey Your RapidAPI key (get it from rapidapi.com)
     */
    public NutritionAPI(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "API key is required. Get yours at: https://rapidapi.com/anonymous617461746174/api/nutrition-tracker-api"
            );
        }
        this.apiKey = apiKey;
        this.client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    }
    
    /**
     * Calculate nutrition for a food query.
     * 
     * @param text Natural language food description
     *             Examples: "100g chicken breast"
     *                       "2 eggs and 1 cup rice"
     * @return Map of nutrients with values and units
     * @throws NutritionAPIException If the API returns an error
     * @throws IOException If a network error occurs
     */
    public Map<String, Object> calculate(String text) throws NutritionAPIException, IOException, InterruptedException {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Food description text is required");
        }
        
        String url = BASE_URL + "/v1/calculate/natural";
        String payload = "{\"text\": \"" + escapeJson(text.trim()) + "\"}";
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .header("X-RapidAPI-Key", apiKey)
            .header("X-RapidAPI-Host", "nutrition-tracker-api.p.rapidapi.com")
            .POST(HttpRequest.BodyPublishers.ofString(payload))
            .timeout(Duration.ofSeconds(30))
            .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        
        if (response.statusCode() != 200) {
            String errorMessage = extractJsonValue(body, "error");
            if (errorMessage == null) errorMessage = "Unknown error occurred";
            throw new NutritionAPIException(errorMessage, response.statusCode());
        }
        
        // Check for success field
        if (!body.contains("\"success\":true") && !body.contains("\"success\": true")) {
            String errorMessage = extractJsonValue(body, "error");
            throw new NutritionAPIException(errorMessage != null ? errorMessage : "Request failed");
        }
        
        // Extract totalNutrients from response
        return parseNutrients(body);
    }
    
    /**
     * Simple JSON value extraction (avoids external dependencies).
     */
    private String extractJsonValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    /**
     * Parse nutrients from JSON response.
     * Note: This is a simplified parser. For production, use Jackson or Gson.
     */
    private Map<String, Object> parseNutrients(String json) {
        Map<String, Object> nutrients = new HashMap<>();
        
        // Find totalNutrients section
        int start = json.indexOf("\"totalNutrients\"");
        if (start == -1) return nutrients;
        
        // Extract the nutrient data (simplified parsing)
        // For production use, consider using Jackson or Gson
        Pattern nutrientPattern = Pattern.compile(
            "\"([^\"]+)\"\\s*:\\s*\\{\\s*\"value\"\\s*:\\s*([\\d.]+)\\s*,\\s*\"unit\"\\s*:\\s*\"([^\"]+)\""
        );
        Matcher matcher = nutrientPattern.matcher(json.substring(start));
        
        while (matcher.find()) {
            String name = matcher.group(1);
            double value = Double.parseDouble(matcher.group(2));
            String unit = matcher.group(3);
            
            Map<String, Object> nutrientData = new HashMap<>();
            nutrientData.put("value", value);
            nutrientData.put("unit", unit);
            nutrients.put(name, nutrientData);
        }
        
        return nutrients;
    }
    
    /**
     * Escape special characters for JSON string.
     */
    private String escapeJson(String text) {
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
    
    /**
     * Exception class for API errors.
     */
    public static class NutritionAPIException extends Exception {
        private final int statusCode;
        
        public NutritionAPIException(String message) {
            super(message);
            this.statusCode = 0;
        }
        
        public NutritionAPIException(String message, int statusCode) {
            super(message);
            this.statusCode = statusCode;
        }
        
        public int getStatusCode() {
            return statusCode;
        }
    }
}
