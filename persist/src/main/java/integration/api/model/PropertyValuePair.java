package integration.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Can be used when querying for repository items based on their properties and respective values.
 */
@Data
@AllArgsConstructor(staticName = "of")
public class PropertyValuePair {
    
    private String propertyName;
    
    private Object propertyValue;
    
}
