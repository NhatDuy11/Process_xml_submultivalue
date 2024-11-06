import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class complete {

    public static int PRETTY_PRINT_INDENT_FACTOR = 4;

    public static void main(String[] args) {
        long startTime = System.nanoTime();


        try {
            String xmlString = "<row id=\"75825\">\n" +
                    "  <c1>140140</c1>\n" +
                    "  <c2>1001</c2>\n" +
                    "  <c3>Current Account</c3>\n" +
                    "  <c5>John Hay Maddison</c5>\n" +
                    "  <c7>TR</c7>\n" +
                    "  <c8>USD</c8>\n" +
                    "  <c9>1</c9>\n" +
                    "  <c10>100.01</c10>\n" +
                    "  <c11>26</c11>\n" +
                    "  <c12>27</c12>\n" +
                    "  <c46>20170331</c46>\n" +
                    "  <c76>NO</c76>\n" +
                    "  <c78>20170321</c78>\n" +
                    "  <c85>1001</c85>\n" +
                    "  <c89>YES</c89>\n" +
                    "  <c93>USD</c93>\n" +
                    "  <c94>1</c94>\n" +
                    "  <c95>USD</c95>\n" +
                    "  <c96>1</c96>\n" +
                    "  <c99>LEGACY</c99>\n" +
                    "  <c99 m=\"2\">T24.IBAN</c99>\n" +
                    "  <c99 m=\"3\">PREV.IBAN</c99>\n" +
                    "  <c99 m=\"5\">PREV.IBANA</c99>\n" +
                    "  <c100/>\n" +
                    "  <c100 l=\"2\">GB16DEMO60161300075825</c100>\n" +
                    "  <c100 t=\"3\"/>\n" +
                    "  <c108>YES</c108>\n" +
                    "  <c142>Y</c142>\n" +
                    "  <c157>WORKING</c157>\n" +
                    "  <c158>DEBITS</c158>\n" +
                    "  <c181>AA170803M4HP</c181>\n" +
                    "  <c214>1</c214>\n" +
                    "  <c215>93133_OFFICER__OFS_SEAT_AAACT170802VCXJMX1</c215>\n" +
                    "  <c216>1705140716</c216>\n" +
                    "  <c217>93133_OFFICER_OFS_SEAT_AAACT170802VCXJMX1</c217>\n" +
                    "  <c218>GB0010001</c218>\n" +
                    "  <c219>1</c219>\n" +
                    "</row>";
            JSONObject xmlJSONObj = XML.toJSONObject(xmlString);
            JSONObject row = xmlJSONObj.getJSONObject("row");
            System.out.println("row : " + row );
            JSONObject flattened = flattenObject(row);
            System.out.println(flattened.toString(PRETTY_PRINT_INDENT_FACTOR));
            long endTime = System.nanoTime();
            double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
            System.out.println("Total execution time: " + durationInSeconds + " seconds");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static JSONObject flattenObject(JSONObject jsonObject) throws JSONException {
        JSONObject result = new JSONObject();
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (value instanceof JSONArray) {
                String flattenedArray = flattenArray((JSONArray) value);
                result.put(key, flattenedArray.isEmpty() ? "null" : flattenedArray);
            } else if (value instanceof JSONObject) {
                String flattenedObject = flattenSingleObject((JSONObject) value);
                result.put(key, flattenedObject.isEmpty() ? "null" : flattenedObject);
            } else {
                result.put(key, (value == null || value.toString().isEmpty()) ? "null" : value);
            }
        }
        return result;
    }

    public static String flattenArray(JSONArray jsonArray) throws JSONException {
        if (jsonArray.length() == 0) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            Object item = jsonArray.get(i);
            System.out.println("item" + item);
            if (item instanceof JSONObject) {
                String flattenedObject = flattenSingleObject((JSONObject) item);
                sb.append(flattenedObject.isEmpty() ? "" : flattenedObject);
            } else {
                sb.append((item == null || item.toString().isEmpty()) ? "" : item.toString());
            }
            if (i < jsonArray.length() - 1) {
                sb.append(" | ");
            }
        }
        return sb.toString();
    }

    public static String flattenSingleObject(JSONObject jsonObject) throws JSONException {
        if (jsonObject.length() == 0) {
            return "null";
        }
        if (jsonObject.has("content")) {
            Object content = jsonObject.get("content");
            return (content == null || content.toString().isEmpty()) ? "null" : content.toString();
        } else if (jsonObject.length() == 1) {
            String key = jsonObject.keys().next();
            if (key.equals("m") || !jsonObject.has("content")) {
                return " ";
            } else {
                Object value = jsonObject.get(key);
                return (value == null || value.toString().isEmpty()) ? "null" : value.toString();
            }
        } else {
            return jsonObject.toString();
        }
    }
}
