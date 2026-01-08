
package eventmanagementsystem;



import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestJson {
    public static void main(String[] args) {
        try {
            InputStream is = TestJson.class.getClassLoader()
                .getResourceAsStream("serviceAccountKey.json");
            
            if (is == null) {
                System.err.println("ERROR: File not found in resources!");
                return;
            }
            
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("=== JSON File Content ===");
            System.out.println(content);
            System.out.println("=== End of Content ===");
            
            // Check for required fields
            if (content.contains("\"type\"")) {
                System.out.println("\n✓ Contains 'type' field");
                // Extract type value
                int typeIndex = content.indexOf("\"type\"");
                int start = content.indexOf("\"", typeIndex + 7) + 1;
                int end = content.indexOf("\"", start);
                String typeValue = content.substring(start, end);
                System.out.println("  Type value: " + typeValue);
            } else {
                System.out.println("\n✗ Missing 'type' field!");
            }
            
            if (content.contains("\"project_id\"")) {
                System.out.println("✓ Contains 'project_id' field");
            }
            
            if (content.contains("\"private_key\"")) {
                System.out.println("✓ Contains 'private_key' field");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}