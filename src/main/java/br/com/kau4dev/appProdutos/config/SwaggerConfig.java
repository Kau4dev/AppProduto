package br.com.kau4dev.appProdutos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerConfig.class);

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Produtos")
                        .version("1.0")
                        .description("Documentação da API de gerenciamento de produtos"));
    }

    @PostConstruct
    public void logSwaggerUrl() {
        String swaggerUrl = "http://localhost:" + serverPort + "/swagger-ui/index.html";
        logger.info("Swagger UI disponível em: {}", swaggerUrl);
    }
}