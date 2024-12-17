import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.Iterator;

public class XMLToJsonProcessor {

    public static void main(String[] args) {
        try {
            String xml = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                    "<operation table='LAKEHOUSE.FBNK_ACCOUNT_V1' type='U' ts='2024-11-06 15:02:19.000000' current_ts='2024-11-06T15:02:38.465000' pos='00000000000045651033' numCols='2'>\n" +
                    "\t<col name='RECID' index='0'>\n" +
                    "\t\t<before><![CDATA[11018]]></before>\n" +
                    "\t\t<after><![CDATA[11018]]></after>\n" +
                    "\t</col>\n" +
                    "\t<col name='XMLRECORD' index='1'>\n" +
                    "\t\t<before missing='true'/>\n" +
                    "\t\t<after><![CDATA[<row id=\"11018\"> <c1>200001</c1> <c2>1001</c2> <c3>COCA-COLA</c3> <c5>COCA-COLA</c5> <c6>COCACOLEUR</c6> <c7>TR</c7> <c8>EUR</c8> <c9>1</c9> <c11>14</c11> <c21>2</c21> <c23>49281.29</c23> <c24>49281.29</c24> <c25>-29018.71</c25> <c26>-29018.71</c26> <c27>-29088.71</c27> <c28>20170321</c28> <c29>50000.00</c29> <c30>89</c30> <c34>20170411</c34> <c35>26000.00</c35> <c36>500</c36> <c37>20170417</c37> <c38>-78300.00</c38> <c39>210</c39> <c43>20170411</c43> <c44>-93.15</c44> <c45>533</c45> <c46>20170331</c46> <c47>20170331</c47> <c48>20170331</c48> <c49>20170331</c49> <c50>20170331</c50> <c76>NO</c76> <c78>20170316</c78> <c85>1001</c85> <c93>EUR</c93> <c94>1</c94> <c95>EUR</c95> <c96>1</c96> <c99>LEGACY</c99> <c99 m=\"2\">T24.IBAN</c99> <c99 m=\"3\">PREV.IBAN</c99> <c100/> <c100 m=\"2\">GB22DEMO60161300011018</c100> <c108>NO</c108> <c141>NO</c141> <c142>Y</c142> <c167>20170417</c167> <c214>1</c214> <c215>23303_OFFICER__OFS_SEAT</c215> <c216>1705140330</c216> <c217>23303_OFFICER_OFS_SEAT</c217> <c218>GB0010001</c218> <c219>1</c219> </row> ]]></after>\n" +
                    "\t</col>\n" +
                    "</operation>\n";            JSONObject resultJson = processXMLToJson(xml);
            System.out.println("Final JSON: " + resultJson.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONObject processXMLToJson(String xml) throws JSONException {
        JSONObject xmlJson = XML.toJSONObject(xml);
        JSONObject operation = xmlJson.getJSONObject("operation");

        String table = operation.optString("table");
        String type = operation.optString("type");
        String ts = operation.optString("ts");
        String currentTs = operation.optString("current_ts");
        String pos = operation.optString("pos");
        String numCols = operation.optString("numCols");
        String recid = extractRecid(operation);
        String afterContent = extractAfterContent(operation);
        JSONObject afterJson = XML.toJSONObject(afterContent);
        JSONObject resultJson = buildResultJson(table, type, ts, currentTs, pos, numCols, recid, afterJson);

        return resultJson;
    }

    private static String extractRecid(JSONObject operation) {
        JSONObject recidCol = operation.getJSONArray("col").getJSONObject(0);
        return recidCol.optString("after");
    }

    // Lấy nội dung 'after' từ cột thứ hai trong JSON
    private static String extractAfterContent(JSONObject operation) {
        JSONObject xmlRecordCol = operation.getJSONArray("col").getJSONObject(1);
        return xmlRecordCol.optString("after");
    }

    private static JSONObject buildResultJson(String table, String type, String ts, String currentTs, String pos,
                                              String numCols, String recid, JSONObject afterJson) throws JSONException {
        JSONObject resultJson = new JSONObject();
        resultJson.put("RECID", recid);
        resultJson.put("col", processObject(afterJson));
        resultJson.put("table", table);
        resultJson.put("type", type);
        resultJson.put("ts", ts);
        resultJson.put("current_ts", currentTs);
        resultJson.put("pos", pos);
        resultJson.put("numCols", numCols);

        if ("D".equals(type)) {
            JSONObject emptyCol = new JSONObject("{\"after\":\"\"}");
            resultJson.put("col", emptyCol);
        }

        return resultJson;
    }

    private static String processObject(JSONObject obj) throws JSONException {
        if (obj.length() == 0) {
            return "null";
        }
        Iterator<String> keys = obj.keys();
        JSONObject result = new JSONObject();

        while (keys.hasNext()) {
            String key = keys.next();
            Object value = obj.get(key);

            if (value instanceof JSONArray) {
                result.put(key, processJsonArray((JSONArray) value));
            } else if (value instanceof JSONObject) {
                result.put(key, processNestedObject((JSONObject) value));
            } else {
                result.put(key, processPrimitiveValue(value));
            }
        }

        return result.toString();
    }

    private static String processJsonArray(JSONArray jsonArray) throws JSONException {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < jsonArray.length(); i++) {
            Object item = jsonArray.get(i);

            if (item instanceof JSONObject) {
                JSONObject subObj = (JSONObject) item;
                sb.append(subObj.optString("content", ""));
            } else {
                sb.append(item.toString());
            }

            if (i != jsonArray.length() - 1) {
                sb.append("|");
            }
        }

        return sb.toString();
    }

    private static Object processNestedObject(JSONObject value) throws JSONException {
        if (value.isEmpty()) {
            return JSONObject.NULL;
        } else {
            return processObject(value);
        }
    }

    private static Object processPrimitiveValue(Object value) {
        if (value instanceof Number) {
            return value;
        } else {
            return value.toString();
        }
    }
}
