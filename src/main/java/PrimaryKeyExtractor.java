import io.confluent.ksql.function.udf.Udf;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;
import java.util.HashMap;
import java.util.Map;
@UdfDescription(name = "extractor_pk", description = "ExtractorPrimaryKey")

public class PrimaryKeyExtractor {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Udf(description = "Extract primary_key from Struct string and return it as JSON")
    public String extractPrimaryKey(@UdfParameter(value = "ExtractorprimaryKey") final String input) {
        System.out.println("input_primarykey: " + input);
        if (input == null || input.isEmpty()) {
            return null;
        }
        String[] parts = input.replace("Struct{", "").replace("}", "").split(",");
        Map<String, String> values = new HashMap<>();
        for (String part : parts) {
            String[] keyValue = part.split("=");
            if (keyValue.length == 2) {
                values.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        String primaryKey = values.get("primary_key");
        try {
            return objectMapper.writeValueAsString(Map.of("primary_key", primaryKey));
        } catch (Exception e) {
            return null;
        }
    }
}
