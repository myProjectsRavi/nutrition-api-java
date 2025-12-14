# Nutrition Tracker API - Java SDK

[![Java](https://img.shields.io/badge/java-11+-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)
[![RapidAPI](https://img.shields.io/badge/RapidAPI-Nutrition%20Tracker-blue)](https://rapidapi.com/anonymous617461746174/api/nutrition-tracker-api)

Simple Java wrapper for the **Nutrition Tracker API** - Get 25+ nutrients from natural language food queries.

## Features

- ✅ **25+ nutrients** in every response (including free tier)
- ✅ **Natural language input** - "100g chicken breast and 1 cup rice"
- ✅ **Hierarchical fat breakdown** - saturated, mono, poly, trans, other
- ✅ **USDA data source** - laboratory-analyzed, peer-reviewed
- ✅ **<100ms response time** - edge-cached for speed
- ✅ **No external dependencies** - uses java.net.http (Java 11+)

## Installation

Clone this repository:

```bash
git clone https://github.com/myProjectsRavi/nutrition-api-java.git
```

Copy `NutritionAPI.java` to your project's source folder.

## Quick Start

### 1. Get Your API Key

Get your free API key from [RapidAPI](https://rapidapi.com/anonymous617461746174/api/nutrition-tracker-api)

### 2. Use the SDK

```java
import com.nutritionapi.NutritionAPI;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Initialize with your RapidAPI key
        NutritionAPI api = new NutritionAPI("YOUR_RAPIDAPI_KEY");
        
        try {
            // Calculate nutrition for any food
            Map<String, Object> result = api.calculate("100g grilled chicken breast");
            
            // Access nutrients
            Map<String, Object> energy = (Map<String, Object>) result.get("Energy");
            Map<String, Object> protein = (Map<String, Object>) result.get("Protein");
            
            System.out.println("Calories: " + energy.get("value") + " " + energy.get("unit"));
            System.out.println("Protein: " + protein.get("value") + " " + protein.get("unit"));
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
```

### 3. Multi-Item Meals

```java
// Calculate nutrition for entire meals
Map<String, Object> result = api.calculate("2 eggs, 100g oatmeal, and 1 banana");

// All nutrients are aggregated automatically
for (Map.Entry<String, Object> entry : result.entrySet()) {
    String nutrient = entry.getKey();
    Map<String, Object> data = (Map<String, Object>) entry.getValue();
    System.out.println(nutrient + ": " + data.get("value") + " " + data.get("unit"));
}
```

## Compiling and Running

```bash
# Compile
javac -d out src/com/nutritionapi/*.java

# Run example
java -cp out com.nutritionapi.Example
```

## API Response

The API returns 25+ nutrients:

```json
{
    "Energy": { "value": 156, "unit": "kcal" },
    "Protein": { "value": 31.02, "unit": "g" },
    "Fat": {
        "value": 3.57,
        "unit": "g",
        "breakdown": {
            "saturated": { "value": 1.01, "unit": "g" },
            "monounsaturated": { "value": 1.24, "unit": "g" },
            "polyunsaturated": { "value": 0.77, "unit": "g" },
            "other": { "value": 0.55, "unit": "g" }
        }
    },
    "Vitamin B-6": { "value": 0.6, "unit": "mg" },
    ...
}
```

## Pricing

| Tier | Price | API Calls/Month | Items/Request |
|------|-------|-----------------|---------------|
| Free | $0 | 1,000 | 2 |
| Starter | $30 | 50,000 | 5 |
| Business | $50 | 100,000 | 10 |

**All tiers include the same 25+ nutrients!**

## Links

- 📖 [Full Documentation](https://myprojectsravi.github.io/nutrition-api-web/)
- 🚀 [Get API Key](https://rapidapi.com/anonymous617461746174/api/nutrition-tracker-api)
- 🐛 [Report Issues](https://github.com/myProjectsRavi/nutrition-api-java/issues)

## License

MIT License - see [LICENSE](LICENSE) for details.
