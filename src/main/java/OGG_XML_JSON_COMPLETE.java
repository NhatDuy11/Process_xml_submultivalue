import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.util.Iterator;

@UdfDescription(name = "convert_xml_to_json_v2", description = "Converts XML to JSON submultivalue")

public class OGG_XML_JSON_COMPLETE {

    @Udf(description = "Convert an XML string to a JSON string")
    public JSONObject parseXMLStringJsonV2(@UdfParameter(value = "subxml") final String xmlData) throws Exception {

        String xmlFiltered = xmlData.replaceFirst("^\\p{C}+", "");

        JSONObject json = XML.toJSONObject(xmlFiltered);
        JSONObject rowObject = json.getJSONObject("row");
        rowObject.remove("xml:space");

        Iterator<String> keys = rowObject.keys();

        JSONObject resultJson = new JSONObject();

        keys.forEachRemaining(key -> {
            Object value = rowObject.get(key);
            if (value instanceof JSONArray) {

                JSONObject transformedJson = new JSONObject();

                ((JSONArray) value).forEach(element -> {
                    if (element instanceof JSONObject) {
                        // If element is JSONObject
                        JSONObject nestedObject = (JSONObject) element;
                        int mValue = nestedObject.optInt("m", -1); // get value m
                        String cValue = nestedObject.optString("c", ""); // get value c
                        String contentValue = nestedObject.optString("content", ""); // get value content

                        // Determine the key format
                        String newKey;
                        if (mValue != -1 && !cValue.isEmpty()) {
                            // If both m and c are present, use mX_cY format
                            newKey = "m" + mValue + "_c" + cValue;
                        } else if (mValue != -1) {
                            // If only m is present, use mX format
                            newKey = "m" + mValue;
                        } else {
                            // If neither m nor c is present, skip this entry
                            return;
                        }

                        // Add only the determined key-value pair to transformedJson
                        transformedJson.put(newKey, contentValue.isEmpty() ? nestedObject.toString() : contentValue);

                    } else {
                        // Add regular non-JSONObject array elements as m1, m2, etc.
                        transformedJson.put("m" + (transformedJson.length() + 1), element);
                    }
                });
                resultJson.put(key, transformedJson);
            } else {
                resultJson.put(key, String.valueOf(value));
            }
        });
        return resultJson;
    }

    public static void main(String[] args) throws Exception {
        long startTime = System.nanoTime();
        String xmlData = "<row id='PDLD0833940960' xml:space='preserve'>\n" +
                "    <c2>100282</c2>\n" +
                "    <c3>USD</c3>\n" +
                "    <c4>1</c4>\n" +
                "    <c5>TR</c5>\n" +
                "    <c6>00</c6>\n" +
                "    <c7>14</c7>\n" +
                "    <c8>21057</c8>\n" +
                "    <c9>E</c9>\n" +
                "    <c10>36722</c10>\n" +
                "    <c11>10</c11>\n" +
                "    <c13>0.50</c13>\n" +
                "    <c14>GB0010001</c14>\n" +
                "    <c15>8110.03</c15>\n" +
                "    <c16>9700.01</c16>\n" +
                "    <c17>-50000</c17>\n" +
                "    <c18>50502.18</c18>\n" +
                "    <c19>50502.18</c19>\n" +
                "    <c20>0</c20>\n" +
                "    <c21>IN</c21>\n" +
                "    <c21 m='3'>PE</c21>\n" +
                "    <c21 m='4'>PS</c21>\n" +
                "    <c21 m='5' c='5'>PS</c21>\n" +
                "    <c22>214.82</c22>\n" +
                "    <c22 m='2' c='2'>50000</c22>\n" +
                "    <c22 m='3' c='3'>273.76</c22>\n" +
                "    <c22 m='4' c='4'>13.6</c22>\n" +
                "    <c23>20090105</c23>\n" +
                "</row>";

        OGG_XML_JSON_COMPLETE myObj = new OGG_XML_JSON_COMPLETE();
        JSONObject result = myObj.parseXMLStringJsonV2(xmlData);
        long endTime = System.nanoTime();
        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
        System.out.println("Total execution time: " + durationInSeconds + " seconds");
        System.out.println("result:" + result);
    }
}
