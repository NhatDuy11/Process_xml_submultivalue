import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
@UdfDescription(name = "submultivalue", description = "Converts XML to JSON submultivalue")

public class test {
    @Udf(description = "Convert an XML string to a JSON string")
    public String processJson(@UdfParameter(value = "subxml") final String xmlStr) {
        try {
            System.out.println("xml_Str: " + xmlStr);
            String xmlFiltered = xmlStr.replaceFirst("^\\p{C}+", "");
            JSONObject xmlJSONObj = XML.toJSONObject(xmlFiltered);
            JSONObject row = xmlJSONObj.getJSONObject("row");
            String row_str = row.toString();
            System.out.println("row: " + row);
            JSONObject jsonObject = new JSONObject(row_str);
            Map<String, Object> resultMap = new HashMap<>();
            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                String key = it.next();
                Object value = jsonObject.get(key);
                if (value instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) value;
                    if (jsonArray.length() > 1 && (jsonArray.get(0) instanceof Number || jsonArray.get(0) instanceof String)) {
                        JSONObject newObject = new JSONObject();
                        newObject.put("m1", jsonArray.get(0));
                        for (int i = 1; i < jsonArray.length(); i++) {
                            Object arrayElement = jsonArray.get(i);
                            if (arrayElement instanceof JSONObject) {
                                JSONObject element = (JSONObject) arrayElement;
                                if (element.has("m")) {
                                    int m_value = element.getInt("m");
                                    String content = element.optString("content", "");
                                    newObject.put("m" + m_value, content);
                                }
                            }
                        }
                        resultMap.put(key, newObject);
                    } else {
                        resultMap.put(key, jsonArray.length() == 0 ? "" : jsonArray);
                    }
                } else if (value instanceof JSONObject) {
                    JSONObject innerObject = (JSONObject) value;
                    if (innerObject.length() == 0) {
                        resultMap.put(key, "");
                    } else {
                        resultMap.put(key, innerObject);
                    }
                } else {
                    resultMap.put(key, value != null ? value : "");
                }
            }

            JSONObject outputJson = new JSONObject(resultMap);
            return outputJson.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        test test_udf = new test();
        String xml ="<row id=\"222222\"> <c1>100282</c1> <c2>1001</c2> <c3>COCA-COLA</c3> <c5>COCA-COLA</c5> <c6>COCACOLUSD</c6> <c7>TR</c7> <c8>USD</c8> <c9>1</c9> <c11>14</c11> <c21>2</c21> <c23>12055363.5</c23> <c24>12055363.5</c24> <c25>11957979.24</c25> <c26>11957979.24</c26> <c27>11957979.24</c27> <c28>20170417</c28> <c29>1525.36</c29> <c30>220</c30> <c34>20170410</c34> <c35>1180.55</c35> <c36>955</c36> <c37>20170417</c37> <c38>-100000.00</c38> <c39>213</c39> <c43>20170417</c43> <c44>-80.32</c44> <c45>522</c45> <c46>20170331</c46> <c47>20170331</c47> <c48>20170331</c48> <c49>20170331</c49> <c50>20170331</c50> <c76>NO</c76> <c78>20170316</c78> <c85>1001</c85> <c93>USD</c93> <c94>1</c94> <c95>USD</c95> <c96>1</c96> <c99>LEGACY</c99> <c99 m=\"2\">T24.IBAN</c99> <c99 m=\"3\">PREV.IBAN</c99> <c100/> <c100 m=\"2\">GB61DEMO60161300010995</c100> <c108>NO</c108> <c121>20170417</c121> <c122>10500</c122> <c141>NO</c141> <c142>Y</c142> <c167>20170417</c167> <c214>1</c214> <c215>11299_OFFICER__OFS_SEAT</c215> <c216>1705140330</c216> <c217>11299_OFFICER_OFS_SEAT</c217> <c218>GB0010001</c218> <c219>1</c219> </row>";
        String rs = test_udf.processJson(xml);
        System.out.println("rs" +rs);
    }



}
