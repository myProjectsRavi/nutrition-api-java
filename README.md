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

## Troubleshooting

This section covers common integration issues and how to fix them. **If you're getting a 400 error with `"text": "Required"`**, read this carefully.

---

### ❌ Error: `"field":"text","message":"Required","code":"invalid_type"`

**Symptom:** You receive this error response:
```json
{
  "success": false,
  "error": {
    "code": 400,
    "message": "Invalid request parameters",
    "details": [{ "field": "text", "message": "Required", "code": "invalid_type" }]
  }
}
```

**Root Cause:** Your request body JSON is malformed. The API expects:
```json
{"text": "100g apple"}
```

But you're sending something like:
```json
{"{"text":"100g apple"}": true}
```

This happens when the JSON string is **double-encoded** or **incorrectly serialized**.

---

### 🔧 Fix by Integration Method

#### 1. Java HttpClient (java.net.http) - Java 11+

**❌ WRONG - Double encoding the JSON:**
```java
// DON'T DO THIS - Creates {"{"text":"100g apple"}": true}
String jsonString = "{\"text\":\"100g apple\"}";
Map<String, Boolean> body = Map.of(jsonString, true);  // WRONG!
```

**❌ WRONG - Using Map.toString():**
```java
// DON'T DO THIS - Creates {text=100g apple} (not valid JSON)
Map<String, String> body = Map.of("text", "100g apple");
String payload = body.toString();  // WRONG! This is NOT JSON
```

**✅ CORRECT - Build JSON string directly:**
```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CorrectExample {
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        
        // ✅ CORRECT: Build the JSON payload as a properly formatted string
        String foodQuery = "100g apple";
        String payload = "{\"text\": \"" + foodQuery + "\"}";
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://nutrition-tracker-api.p.rapidapi.com/v1/calculate/natural"))
            .header("Content-Type", "application/json")
            .header("X-RapidAPI-Key", "YOUR_API_KEY")
            .header("X-RapidAPI-Host", "nutrition-tracker-api.p.rapidapi.com")
            .POST(HttpRequest.BodyPublishers.ofString(payload))
            .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }
}
```

---

#### 2. Spring RestTemplate

**❌ WRONG - Passing a String that looks like JSON:**
```java
// DON'T DO THIS
RestTemplate restTemplate = new RestTemplate();
String jsonString = "{\"text\":\"100g apple\"}";
Map<String, String> body = new HashMap<>();
body.put(jsonString, "true");  // WRONG!
```

**✅ CORRECT - Use a proper request object or Map:**
```java
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.HashMap;

public class SpringExample {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        
        String url = "https://nutrition-tracker-api.p.rapidapi.com/v1/calculate/natural";
        
        // ✅ CORRECT: Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-RapidAPI-Key", "YOUR_API_KEY");
        headers.set("X-RapidAPI-Host", "nutrition-tracker-api.p.rapidapi.com");
        
        // ✅ CORRECT: Create body as a Map (Spring auto-serializes to JSON)
        Map<String, String> body = new HashMap<>();
        body.put("text", "100g apple");  // Key is "text", value is the food query
        
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
            url, HttpMethod.POST, entity, String.class
        );
        
        System.out.println(response.getBody());
    }
}
```

**✅ ALTERNATIVE - Use a POJO:**
```java
// Create a request class
public class NutritionRequest {
    private String text;
    
    public NutritionRequest(String text) {
        this.text = text;
    }
    
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}

// Use it in your API call
NutritionRequest request = new NutritionRequest("100g apple");
HttpEntity<NutritionRequest> entity = new HttpEntity<>(request, headers);
```

---

#### 3. Spring WebClient (Reactive)

**✅ CORRECT - Using WebClient:**
```java
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Map;

public class WebClientExample {
    public static void main(String[] args) {
        WebClient client = WebClient.builder()
            .baseUrl("https://nutrition-tracker-api.p.rapidapi.com")
            .defaultHeader("X-RapidAPI-Key", "YOUR_API_KEY")
            .defaultHeader("X-RapidAPI-Host", "nutrition-tracker-api.p.rapidapi.com")
            .build();
        
        // ✅ CORRECT: Create body as a Map
        Map<String, String> body = Map.of("text", "100g apple");
        
        String response = client.post()
            .uri("/v1/calculate/natural")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)  // WebClient auto-serializes the Map to JSON
            .retrieve()
            .bodyToMono(String.class)
            .block();
        
        System.out.println(response);
    }
}
```

---

#### 4. OkHttp

**❌ WRONG - Incorrect MediaType or body:**
```java
// DON'T DO THIS
String wrongBody = new JSONObject().put("{\"text\":\"100g apple\"}", true).toString();
```

**✅ CORRECT - Using OkHttp:**
```java
import okhttp3.*;

public class OkHttpExample {
    public static void main(String[] args) throws Exception {
        OkHttpClient client = new OkHttpClient();
        
        // ✅ CORRECT: Build proper JSON body
        String payload = "{\"text\": \"100g apple\"}";
        
        RequestBody body = RequestBody.create(
            payload,
            MediaType.parse("application/json")
        );
        
        Request request = new Request.Builder()
            .url("https://nutrition-tracker-api.p.rapidapi.com/v1/calculate/natural")
            .addHeader("X-RapidAPI-Key", "YOUR_API_KEY")
            .addHeader("X-RapidAPI-Host", "nutrition-tracker-api.p.rapidapi.com")
            .post(body)
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.body().string());
        }
    }
}
```

---

#### 5. Using Gson or Jackson for JSON Serialization

If you prefer using a JSON library (recommended for complex scenarios):

**✅ CORRECT - Using Gson:**
```java
import com.google.gson.Gson;
import java.util.Map;

public class GsonExample {
    public static void main(String[] args) {
        Gson gson = new Gson();
        
        // Create a simple Map
        Map<String, String> requestBody = Map.of("text", "100g apple");
        
        // ✅ Gson correctly converts this to: {"text":"100g apple"}
        String payload = gson.toJson(requestBody);
        
        System.out.println(payload);  // Output: {"text":"100g apple"}
        
        // Now use this payload in your HTTP request
    }
}
```

**✅ CORRECT - Using Jackson:**
```java
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class JacksonExample {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        
        // Create a simple Map
        Map<String, String> requestBody = Map.of("text", "100g apple");
        
        // ✅ Jackson correctly converts this to: {"text":"100g apple"}
        String payload = mapper.writeValueAsString(requestBody);
        
        System.out.println(payload);  // Output: {"text":"100g apple"}
        
        // Now use this payload in your HTTP request
    }
}
```

---

### 🔍 How to Debug Your Request

**Step 1: Print your payload before sending:**
```java
String payload = /* your JSON construction */;
System.out.println("DEBUG - Sending payload: " + payload);
// Should print: {"text": "100g apple"}
// NOT: {"{"text":"100g apple"}": true}
```

**Step 2: Verify JSON structure:**
Your payload MUST look exactly like this:
```json
{"text": "your food query here"}
```

**Step 3: Check for these common mistakes:**

| Mistake | What You're Sending | Why It's Wrong |
|---------|---------------------|----------------|
| Double encoding | `{"{"text":"apple"}":true}` | JSON string used as a key |
| Using Map.toString() | `{text=apple}` | Not valid JSON format |
| Missing quotes | `{text: apple}` | JSON requires quoted strings |
| Wrong Content-Type | N/A | Must be `application/json` |

---

### 📋 Checklist Before Making API Calls

- [ ] **Content-Type header** is set to `application/json`
- [ ] **X-RapidAPI-Key** header contains your valid API key
- [ ] **X-RapidAPI-Host** header is set to `nutrition-tracker-api.p.rapidapi.com`
- [ ] **Request body** is valid JSON: `{"text": "your food query"}`
- [ ] **No double encoding** - the `text` key appears only once
- [ ] **Using POST method** (not GET)

---

### 💡 Pro Tips

1. **Use this SDK!** The `NutritionAPI.java` class in this repository handles all JSON serialization correctly. Just copy it to your project.

2. **Test with curl first:**
   ```bash
   curl -X POST "https://nutrition-tracker-api.p.rapidapi.com/v1/calculate/natural" \
     -H "Content-Type: application/json" \
     -H "X-RapidAPI-Key: YOUR_API_KEY" \
     -H "X-RapidAPI-Host: nutrition-tracker-api.p.rapidapi.com" \
     -d '{"text": "100g apple"}'
   ```

3. **Use JSON libraries** like Gson or Jackson instead of manual string building for complex queries.

4. **Escape special characters** in food queries (our SDK handles this automatically):
   ```java
   // If your query contains quotes or special chars
   String query = "100g \"premium\" chicken";
   // Must be escaped to: 100g \"premium\" chicken
   ```

---

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
