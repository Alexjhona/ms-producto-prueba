package com.example.ms_producto.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler(new ObjectMapper());

    @Test
    void manejarNoEncontrado_debeRetornarFormatoEstandar() {
        MockHttpServletRequest request = request("/api/productos/99");

        ResponseEntity<Map<String, Object>> response =
                handler.manejarNoEncontrado(new ProductoNoEncontradoException(99L), request);

        assertFormatoEstandar(response, HttpStatus.NOT_FOUND, "No se encontró el recurso solicitado", "/api/productos/99");
    }

    @Test
    void manejarConflicto_debeRetornarFormatoEstandar() {
        MockHttpServletRequest request = request("/api/productos");

        ResponseEntity<Map<String, Object>> response =
                handler.manejarConflicto(new ConflictoRecursoException("Duplicado"), request);

        assertFormatoEstandar(response, HttpStatus.CONFLICT, "El registro ya existe o genera conflicto", "/api/productos");
    }

    @Test
    void manejarErrorInterno_debeRetornarFormatoEstandar() {
        MockHttpServletRequest request = request("/api/productos");

        ResponseEntity<Map<String, Object>> response =
                handler.manejarErrorInterno(new RuntimeException("Falla inesperada"), request);

        assertFormatoEstandar(response, HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado en el servidor", "/api/productos");
    }

    private MockHttpServletRequest request(String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);
        return request;
    }

    private void assertFormatoEstandar(
            ResponseEntity<Map<String, Object>> response,
            HttpStatus status,
            String mensaje,
            String ruta) {
        assertThat(response.getStatusCode()).isEqualTo(status);
        assertThat(response.getBody())
                .containsEntry("status", status.value())
                .containsEntry("error", status.getReasonPhrase())
                .containsEntry("mensaje", mensaje)
                .containsEntry("ruta", ruta)
                .containsKeys("timestamp", "datosRecibidos", "errores");
    }
}
