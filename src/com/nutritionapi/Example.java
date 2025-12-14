package com.nutritionapi;

import java.util.Map;

/**
 * Example usage of the Nutrition Tracker API Java SDK.
 * 
 * Before running:
 * 1. Get your free API key from: https://rapidapi.com/anonymous617461746174/api/nutrition-tracker-api
 * 2. Replace YOUR_RAPIDAPI_KEY below with your actual key
 * 3. Compile: javac -d out src/com/nutritionapi/*.java
 * 4. Run: java -cp out com.nutritionapi.Example
 */
public class Example {
    
    // Replace with your RapidAPI key
    private static final String API_KEY = "YOUR_RAPIDAPI_KEY";
    
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Nutrition Tracker API - Java SDK Example");
        System.out.println("=".repeat(60));
        
        // Initialize the client
        NutritionAPI api = new NutritionAPI(API_KEY);
        
        // Example 1: Single food item
        System.out.println("\n📍 Example 1: Single Food Item");
        System.out.println("-".repeat(40));
        
        try {
            Map<String, Object> result = api.calculate("100g grilled chicken breast");
            
            System.out.println("Query: 100g grilled chicken breast\n");
            System.out.println("Key Nutrients:");
            printNutrient(result, "Energy");
            printNutrient(result, "Protein");
            printNutrient(result, "Fat");
            
        } catch (NutritionAPI.NutritionAPIException e) {
            System.err.println("API Error: " + e.getMessage() + " (Status: " + e.getStatusCode() + ")");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        // Example 2: Multi-item meal
        System.out.println("\n\n📍 Example 2: Multi-Item Meal");
        System.out.println("-".repeat(40));
        
        try {
            Map<String, Object> result = api.calculate("2 eggs, 100g oatmeal, and 1 banana");
            
            System.out.println("Query: 2 eggs, 100g oatmeal, and 1 banana\n");
            System.out.println("Combined Nutrients:");
            printNutrient(result, "Energy");
            printNutrient(result, "Protein");
            printNutrient(result, "Carbohydrates");
            printNutrient(result, "Fiber");
            
        } catch (NutritionAPI.NutritionAPIException e) {
            System.err.println("API Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        // Example 3: All nutrients
        System.out.println("\n\n📍 Example 3: All 25+ Nutrients");
        System.out.println("-".repeat(40));
        
        try {
            Map<String, Object> result = api.calculate("1 apple");
            
            System.out.println("Query: 1 apple\n");
            System.out.println("All Nutrients:");
            
            for (Map.Entry<String, Object> entry : result.entrySet()) {
                String nutrient = entry.getKey();
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) entry.getValue();
                System.out.println("  • " + nutrient + ": " + data.get("value") + " " + data.get("unit"));
            }
            
        } catch (NutritionAPI.NutritionAPIException e) {
            System.err.println("API Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Get your API key: https://rapidapi.com/anonymous617461746174/api/nutrition-tracker-api");
        System.out.println("=".repeat(60));
    }
    
    @SuppressWarnings("unchecked")
    private static void printNutrient(Map<String, Object> result, String name) {
        Object nutrient = result.get(name);
        if (nutrient instanceof Map) {
            Map<String, Object> data = (Map<String, Object>) nutrient;
            System.out.println("  • " + name + ": " + data.get("value") + " " + data.get("unit"));
        } else {
            System.out.println("  • " + name + ": N/A");
        }
    }
}
