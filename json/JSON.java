package json;
import java.util.HashMap;

public class JSON {
    public static HashMap<String, String> jsonToHashMap(String json) {
        json = json.replace("\n", "").replace("\"", "");
        HashMap<String, String> map = new HashMap<>();
        String[] pairs = json.replace("{", "").replace("}", "").trim().split(", "); // The regex here is a bit hacky,
                                                                                    // used

        for (String pair : pairs) {
            String[] keyValue = pair.split(": ");
            map.put(keyValue[0].trim(), keyValue[1].trim());
        }
        return map;
    }
}