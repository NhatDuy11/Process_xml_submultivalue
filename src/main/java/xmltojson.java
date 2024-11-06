import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class xmltojson {
    public static void main(String[] args) {
        // Sample JSON string
        String jsonString = "{\n" +
                "\t\"c96\": 1,\n" +
                "\t\"c142\": \"Y\",\n" +
                "\t\"c11\": 26,\n" +
                "\t\"c99\": [\n" +
                "\t\t\"LEGACY\",\n" +
                "\t\t{\n" +
                "\t\t\t\"m\": 2,\n" +
                "\t\t\t\"content\": \"T24.IBAN\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"m\": 3,\n" +
                "\t\t\t\"content\": \"PREV.IBAN\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"m\": 5,\n" +
                "\t\t\t\"content\": \"PREV.IBANA\"\n" +
                "\t\t}\n" +
                "\t],\n" +
                "\t\"c10\": 100.01,\n" +
                "\t\"c76\": \"NO\",\n" +
                "\t\"c100\": [\n" +
                "\t\t\"\",\n" +
                "\t\t{\n" +
                "\t\t\t\"m\": 2,\n" +
                "\t\t\t\"content\": \"GB16DEMO60161300075825\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"m\": 3\n" +
                "\t\t}\n" +
                "\t],\n" +
                "\t\"c12\": 27,\n" +
                "\t\"c78\": 20170321,\n" +
                "\t\"c181\": \"AA170803M4HP\",\n" +
                "\t\"id\": 75825,\n" +
                "\t\"c108\": \"YES\",\n" +
                "\t\"c85\": 1001,\n" +
                "\t\"c46\": 20170331,\n" +
                "\t\"c89\": \"YES\",\n" +
                "\t\"c1\": 140140,\n" +
                "\t\"c2\": 1001,\n" +
                "\t\"c3\": \"Current Account\",\n" +
                "\t\"c5\": \"John Hay Maddison\",\n" +
                "\t\"c7\": \"TR\",\n" +
                "\t\"c8\": \"USD\",\n" +
                "\t\"c9\": 1,\n" +
                "\t\"c217\": \"93133_OFFICER_OFS_SEAT_AAACT170802VCXJMX1\",\n" +
                "\t\"c216\": 1705140716,\n" +
                "\t\"c219\": 1,\n" +
                "\t\"c218\": \"GB0010001\",\n" +
                "\t\"c93\": \"USD\",\n" +
                "\t\"c158\": \"DEBITS\",\n" +
                "\t\"c157\": \"WORKING\",\n" +
                "\t\"c95\": \"USD\",\n" +
                "\t\"c215\": \"93133_OFFICER__OFS_SEAT_AAACT170802VCXJMX1\",\n" +
                "\t\"c94\": 1,\n" +
                "\t\"c214\": 1\n" +
                "}";
        // Convert JSON string to JSONObject
        JSONObject jsonObject = new JSONObject(jsonString);
        Map<String, Object> resultMap = new HashMap<>();

        for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
            String key = it.next();
            Object value = jsonObject.get(key);

            if (value instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) value;

                if (jsonArray.length() > 1 && (jsonArray.get(0) instanceof Number || jsonArray.get(0) instanceof String)) {
                    JSONObject newObject = new JSONObject();
                    newObject.put("m1", jsonArray.get(0));

                    // Find the max value of "m"
                    int maxM = 1; // Initialize to the smallest possible m value (m1 exists as jsonArray.get(0))
                    for (int i = 1; i < jsonArray.length(); i++) {
                        Object arrayElement = jsonArray.get(i);
                        if (arrayElement instanceof JSONObject) {
                            JSONObject element = (JSONObject) arrayElement;
                            if (element.has("m")) {
                                int m_value = element.getInt("m");
                                String content = element.has("content") ? element.getString("content") : ""; // Handle empty content
                                newObject.put("m" + m_value, content);
                                // Track the max "m" value
//                                if (m_value > maxM) {
//                                    maxM = m_value;
//                                }
                            }
                        }
                    }

                    // Fill missing "m" values with empty strings
//                    for (int i = 1; i <= maxM; i++) {
//                        if (!newObject.has("m" + i)) {
//                            newObject.put("m" + i, "");
//                        }
//                    }

                    resultMap.put(key, newObject);
                } else {
                    resultMap.put(key, jsonArray);
                }
            } else {
                resultMap.put(key, value);
            }
        }

        // Output the transformed JSON
        JSONObject outputJson = new JSONObject(resultMap);
        System.out.println(outputJson.toString(4));  // Pretty print output
    }
}
