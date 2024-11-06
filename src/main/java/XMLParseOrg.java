
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;
import org.json.JSONObject;
import org.json.XML;
import org.json.JSONArray;

@UdfDescription(name = "convert_xml_to_json_ogg_v1", description = "Converts XML to JSON OGG")
public class XMLParseOrg {

    public JSONObject processJsonArray(JSONArray colArray) throws Exception {

        JSONObject lstJson = new JSONObject();

        colArray.forEach(value -> {
            JSONObject colItem = (JSONObject) value;
            JSONObject transformedJson = new JSONObject();

            if ("XMLRECORD".equals(colItem.get("name"))) {
                JSONObject parserJson = XML.toJSONObject(colItem.get("after").toString());
                JSONObject rowObjectChild = parserJson.getJSONObject("row");

                for (String key : rowObjectChild.keySet()) {
                    Object valueChild = rowObjectChild.get(key);

                    if (valueChild instanceof JSONArray) {
                        JSONObject transformedJsonChild = new JSONObject();

                        ((JSONArray) valueChild).forEach(element -> {
                            if (element instanceof JSONObject) {
                                JSONObject nestedObject = (JSONObject) element;
                                int mValue = nestedObject.optInt("m", -1); // get the 'm' attribute if present
                                String cValue = nestedObject.optString("c", ""); // get the 'c' attribute if present
                                String contentValue = nestedObject.optString("content", ""); // get the content value

                                // Generate the new key based on the presence of m and c attributes
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

                                // Add the key-value to the transformed JSON if contentValue is not empty
                                if (!contentValue.isEmpty()) {
                                    transformedJsonChild.put(newKey, contentValue);
                                }
                            } else {
                                // Handle non-JSONObject elements by adding them with an incremental key
                                transformedJsonChild.put("m" + (transformedJsonChild.length() + 1), element);
                            }
                        });
                        transformedJson.put(key, transformedJsonChild);
                    } else {
                        transformedJson.put(key, String.valueOf(valueChild));
                    }
                }
            } else {
                lstJson.put(colItem.get("name").toString(), colItem.get("after").toString());
            }
            lstJson.put("after", transformedJson);
        });

        return lstJson;
    }

    @Udf(description = "Convert an XML string to a JSON string")
    public String parserXmlToJsonOgg(@UdfParameter(value = "ogg_xml_data") final String xmlData) throws Exception {

        JSONObject json = XML.toJSONObject(xmlData);
        JSONObject rowObject = json.getJSONObject("operation");

        String checkType = rowObject.get("type").toString();
        JSONObject lstResult = new JSONObject();

        for (String key : rowObject.keySet()) {
            Object value = rowObject.get(key);

            if (!(value instanceof JSONArray)) {
                lstResult.put(key, value);
            } else {
                if ("D".equals(checkType)) {
                    JSONObject valueD = new JSONObject("{\"after\":\"\"}");
                    lstResult.put("col", valueD);
                } else {
                    JSONArray jsonArray = (JSONArray) value;
                    lstResult.put(key, processJsonArray(jsonArray));
                }
            }
        }
        return lstResult.toString();
    }
        public static void main(String[] args) throws Exception {
        long startTime = System.nanoTime();
        String xmlData = "<?xml version='1.0' encoding='UTF-8'?> <operation table='LAKEHOUSE.FBNK_ACCOUNT_V1' type='I' ts='2024-11-01 10:03:06.000000' current_ts='2024-11-01T10:03:16.210000' pos='00000000100069161442' numCols='2'> <col name='RECID' index='0'> <before missing='true'/> <after><![CDATA[222222]]></after> </col> <col name='XMLRECORD' index='1'> <before missing='true'/> <after><![CDATA[ <row id=\"222222\"> <c1>100282</c1> <c2>1001</c2> <c3>COCA-COLA</c3> <c5>COCA-COLA</c5> <c6>COCACOLUSD</c6> <c7>TR</c7> <c8>USD</c8> <c9>1</c9> <c11>14</c11> <c21>2</c21> <c23>12055363.5</c23> <c24>12055363.5</c24> <c25>11957979.24</c25> <c26>11957979.24</c26> <c27>11957979.24</c27> <c28>20170417</c28> <c29>1525.36</c29> <c30>220</c30> <c34>20170410</c34> <c35>1180.55</c35> <c36>955</c36> <c37>20170417</c37> <c38>-100000.00</c38> <c39>213</c39> <c43>20170417</c43> <c44>-80.32</c44> <c45>522</c45> <c46>20170331</c46> <c47>20170331</c47> <c48>20170331</c48> <c49>20170331</c49> <c50>20170331</c50> <c76>NO</c76> <c78>20170316</c78> <c85>1001</c85> <c93>USD</c93> <c94>1</c94> <c95>USD</c95> <c96>1</c96> <c99>LEGACY</c99> <c99 m=\"2\">T24.IBAN</c99> <c99 m=\"3\">PREV.IBAN</c99> <c100/> <c100 m=\"2\">GB61DEMO60161300010995</c100> <c108>NO</c108> <c121>20170417</c121> <c122>10500</c122> <c141>NO</c141> <c142>Y</c142> <c167>20170417</c167> <c214>1</c214> <c215>11299_OFFICER__OFS_SEAT</c215> <c216>1705140330</c216> <c217>11299_OFFICER_OFS_SEAT</c217> <c218>GB0010001</c218> <c219>1</c219> </row>]]></after> </col> </operation>";

        String xmlDataDelete = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<operation table='LAKEHOUSE.FBNK_ACCOUNT_V1' type='I' ts='2024-11-01 10:03:06.000000' current_ts='2024-11-01T10:03:16.210000' pos='00000000100069161442' numCols='2'>\n" +
                "\t<col name='RECID' index='0'>\n" +
                "\t\t<before missing='true'/>\n" +
                "\t\t<after><![CDATA[222222]]></after>\n" +
                "\t</col>\n" +
                "\t<col name='XMLRECORD' index='1'>\n" +
                "\t\t<before missing='true'/>\n" +
                "\t\t<after><![CDATA[ <row id=\"222222\"> <c1>100282</c1> <c2>1001</c2> <c3>COCA-COLA</c3> <c5>COCA-COLA</c5> <c6>COCACOLUSD</c6> <c7>TR</c7> <c8>USD</c8> <c9>1</c9> <c11>14</c11> <c21>2</c21> <c23>12055363.5</c23> <c24>12055363.5</c24> <c25>11957979.24</c25> <c26>11957979.24</c26> <c27>11957979.24</c27> <c28>20170417</c28> <c29>1525.36</c29> <c30>220</c30> <c34>20170410</c34> <c35>1180.55</c35> <c36>955</c36> <c37>20170417</c37> <c38>-100000.00</c38> <c39>213</c39> <c43>20170417</c43> <c44>-80.32</c44> <c45>522</c45> <c46>20170331</c46> <c47>20170331</c47> <c48>20170331</c48> <c49>20170331</c49> <c50>20170331</c50> <c76>NO</c76> <c78>20170316</c78> <c85>1001</c85> <c93>USD</c93> <c94>1</c94> <c95>USD</c95> <c96>1</c96> <c99>LEGACY</c99> <c99 m=\"2\" c=\"2\">T24.IBAN</c99> <c99 m=\"3\">PREV.IBAN</c99> <c100/> <c100 m=\"2\">GB61DEMO60161300010995</c100> <c108>NO</c108> <c121>20170417</c121> <c122>10500</c122> <c141>NO</c141> <c142>Y</c142> <c167>20170417</c167> <c214>1</c214> <c215>11299_OFFICER__OFS_SEAT</c215> <c216>1705140330</c216> <c217>11299_OFFICER_OFS_SEAT</c217> <c218>GB0010001</c218> <c219>1</c219> </row>]]></after>\n" +
                "\t</col>\n" +
                "</operation>\n";
        XMLParseOrg myObject = new XMLParseOrg();

        String result = myObject.parserXmlToJsonOgg(xmlDataDelete);
        System.out.println("result:" + result);

        long endTime = System.nanoTime();
        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
        System.out.println("Total execution time: " + durationInSeconds + " seconds");
        System.out.println("result:" + result);
    }
}
