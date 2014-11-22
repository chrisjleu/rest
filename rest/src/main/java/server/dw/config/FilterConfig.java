package server.dw.config;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

@Data
public class FilterConfig {

    @NotBlank
    private String name;

    @NotBlank
    private String className;
    
    private String path = "/*";

    private String enabled = Boolean.TRUE.toString();
}
