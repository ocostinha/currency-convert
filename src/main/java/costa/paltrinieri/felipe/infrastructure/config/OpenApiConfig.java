package costa.paltrinieri.felipe.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                      .title("Purchase Transaction API")
                      .description("API for managing purchase transactions with currency conversion")
                      .version("1.0.0"));
    }

}
