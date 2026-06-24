package com.example.ms_producto.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OpenApiConfigTest {

    @Test
    void customOpenAPI_debeRetornarConfiguracionCorrecta() {
        OpenApiConfig config = new OpenApiConfig();

        OpenAPI openAPI = config.customOpenAPI();

        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("OPEN API MICROSERVICIO PRODUCTO", openAPI.getInfo().getTitle());
        assertEquals("0.0.1", openAPI.getInfo().getVersion());
        assertEquals("Documentacion de endpoints para registrar, consultar, actualizar, eliminar productos y administrar precios de venta.", openAPI.getInfo().getDescription());
    }
}
