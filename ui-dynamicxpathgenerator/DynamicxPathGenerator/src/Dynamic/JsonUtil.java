package Dynamic;


import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonUtil {

    private static final String JSON_FILE_PATH = System.getProperty("user.dir") + "\\OR_Main.json";
    private static final LinkedHashMap<String, JSONObject> orderedJsonMap = new LinkedHashMap<>();

    static {
        loadExistingJson();
    }

    // Save a new field to internal map (if not duplicate)
    public static void saveElement(String key, JSONObject value) {
        if (!isDuplicate(key, value)) {
            orderedJsonMap.put(key, value);
            System.out.println("Element saved: " + key);
        } else {
            System.out.println("Skipping duplicate entry for: " + key);
        }
    }

    // Manually trigger writing JSON to file
    public static void writeToFile() {
        saveOrderedJson(orderedJsonMap);
    }

    // Load JSON as ordered LinkedHashMap (for reuse in element functions)
    public static LinkedHashMap<String, JSONObject> loadOrderedJson() {
        LinkedHashMap<String, JSONObject> map = new LinkedHashMap<>();
        try {
            File file = new File(JSON_FILE_PATH);
            if (!file.exists() || file.length() == 0) return map;

            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            JSONObject jsonObject = new JSONObject(content);

            for (String key : jsonObject.keySet()) {
                map.put(key, jsonObject.getJSONObject(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    // Save the given LinkedHashMap to JSON in correct order
    public static void saveOrderedJson(LinkedHashMap<String, JSONObject> map) {
        try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
            JSONObject finalJson = new JSONObject();
            for (Map.Entry<String, JSONObject> entry : map.entrySet()) {
                finalJson.put(entry.getKey(), entry.getValue());
            }
            writer.write(finalJson.toString(4));
            writer.flush();
            System.out.println("JSON written in correct order.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Checks for duplicates in orderedJsonMap
    private static boolean isDuplicate(String key, JSONObject newObj) {
        if (!orderedJsonMap.containsKey(key)) return false;
        JSONObject existing = orderedJsonMap.get(key);
        return existing.similar(newObj);
    }

    // Load into static map during init
    private static void loadExistingJson() {
        try {
            File file = new File(JSON_FILE_PATH);
            if (!file.exists() || file.length() == 0) return;

            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            JSONObject jsonObject = new JSONObject(content);

            for (String key : jsonObject.keySet()) {
                orderedJsonMap.put(key, jsonObject.getJSONObject(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
