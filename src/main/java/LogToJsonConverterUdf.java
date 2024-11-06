
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;
import org.json.JSONObject;

@UdfDescription(name = "log_to_json_v1", description = "Convert log text to JSON")
public class LogToJsonConverterUdf {
    public static int PRETTY_PRINT_INDENT_FACTOR = 4;
    @Udf(description = "Convert log string to JSON string")
    public String logToJson(@UdfParameter(value = "String_log") final String log) {
        if (log == null || log.isEmpty()) {
            return null;
        }

        JSONObject jsonObject = new JSONObject();
        String[] keyValuePairs = log.split(" ");
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split("=", 2);
            String key = keyValue[0].trim(); // Trim key
            String value = keyValue[1].trim().replaceAll("^\"|\"$", "");
            if (value.matches("\\d+")) {
                jsonObject.put(key, Long.parseLong(value));
            } else {
                jsonObject.put(key, value);
            }
        }

        return jsonObject.toString(PRETTY_PRINT_INDENT_FACTOR);
    }

//    public static void main(String[] args) {
//        LogToJsonConverterUdf converter = new LogToJsonConverterUdf();
//
//        String log = "date=2019-05-13 time=11:45:03 logid=\"0211008192\" type=\"utm\" subtype=\"virus\" eventtype=\"infected\" level=\"warning\" vd=\"vdom1\" eventtime=1557773103767393505 msg=\"File action=\"blocked\" service=\"HTTP\" sessionid=359260 srcip=10.1.100.11 dstip=172.16.200.55 srcport=60446 dstport=80 srcintf=\"port12\" srcintfrole=\"undefined\" dstintf=\"port11\" dstintfrole=\"undefined\" policyid=4 proto=6 direction=\"incoming\" filename=\"HP.exe\" quarskip=\"File-was-not-quarantined.\" virus=\"EICAR_TEST_FILE\" dtype=\"Virus\" virusid=2172 url=\"http://172.16.200.55/virus/eicar.com\" profile=\"g-default\" agent=\"curl/7.47.0\" analyticscksum=\"275a021bbfb6489e54d471899f7db9d1663fc695ec2fe2a2c4538aabf651fd0f\" analyticssubmit=\"false\" crscore=50 craction=2 crlevel=\"critical\"\"\n";
//
//        String jsonResult = converter.logToJson(log);
//
//        System.out.println(jsonResult);
//    }

}
