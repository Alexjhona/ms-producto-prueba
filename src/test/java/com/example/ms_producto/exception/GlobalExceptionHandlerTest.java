
package com.example.ms_producto.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler(new ObjectMapper());

    @Test
    void manejarValidacion_debeIncluirErroresYDatosRecibidos() {
        Map<String, Object> target = Map.of(
                "nombre", "",
                "precioVenta", -1
        );

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(
                        target,
                        "productoDto"
                );

        bindingResult.addError(
                new FieldError(
                        "productoDto",
                        "nombre",
                        "Campo obligatorio"
                )
        );

        bindingResult.addError(
                new FieldError(
                        "productoDto",
                        "nombre",
                        "Mensaje repetido"
                )
        );

        bindingResult.addError(
                new ObjectError(
                        "productoDto",
                        "Datos inválidos"
                )
        );

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(
                        null,
                        bindingResult
                );

        ResponseEntity<Map<String, Object>> response =
                handler.manejarValidacion(
                        exception,
                        request("/api/productos")
                );

        assertFormatoEstandar(
                response,
                HttpStatus.BAD_REQUEST,
                "Se encontraron errores de validación",
                "/api/productos"
        );

        assertThat(response.getBody().get("datosRecibidos"))
                .isEqualTo(target);

        assertThat(errores(response))
                .containsEntry(
                        "nombre",
                        "Campo obligatorio"
                )
                .containsEntry(
                        "productoDto",
                        "Datos inválidos"
                );
    }

    @Test
    void manejarValidacion_debeUsarMensajeGenericoCuandoEsNulo() {
        Map<String, Object> target =
                Map.of("nombre", "");

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(
                        target,
                        "productoDto"
                );

        bindingResult.addError(
                new FieldError(
                        "productoDto",
                        "nombre",
                        null
                )
        );

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(
                        null,
                        bindingResult
                );

        ResponseEntity<Map<String, Object>> response =
                handler.manejarValidacion(
                        exception,
                        request("/api/productos")
                );

        assertThat(errores(response))
                .containsEntry(
                        "nombre",
                        "Valor inválido"
                );
    }

    @Test
    void manejarBodyInvalido_debeDetectarCampoJson()
            throws Exception {

        JsonMappingException mappingException =
                mock(JsonMappingException.class);

        when(mappingException.getPath())
                .thenReturn(List.of(
                        new JsonMappingException.Reference(
                                new Object(),
                                "productos"
                        ),
                        new JsonMappingException.Reference(
                                new Object(),
                                0
                        ),
                        new JsonMappingException.Reference(
                                new Object(),
                                "precioVenta"
                        )
                ));

        HttpMessageNotReadableException exception =
                mock(HttpMessageNotReadableException.class);

        when(exception.getMostSpecificCause())
                .thenReturn(mappingException);

        ContentCachingRequestWrapper request =
                cachedRequest(
                        "/api/productos",
                        "{\"precioVenta\":}",
                        null
                );

        ResponseEntity<Map<String, Object>> response =
                handler.manejarBodyInvalido(
                        exception,
                        request
                );

        assertThat(response.getBody().get("datosRecibidos"))
                .isEqualTo("{\"precioVenta\":}");

        assertThat(errores(response))
                .containsEntry(
                        "productos.[0].precioVenta",
                        "Tipo de dato inválido o estructura incorrecta"
                );
    }

    @Test
    void manejarBodyInvalido_debeParsearBodyValido()
            throws Exception {

        HttpMessageNotReadableException exception =
                mock(HttpMessageNotReadableException.class);

        when(exception.getMostSpecificCause())
                .thenReturn(
                        new IllegalArgumentException("JSON dañado")
                );

        ContentCachingRequestWrapper request =
                cachedRequest(
                        "/api/productos",
                        "{\"nombre\":\"Teclado\"}",
                        StandardCharsets.UTF_8.name()
                );

        ResponseEntity<Map<String, Object>> response =
                handler.manejarBodyInvalido(
                        exception,
                        request
                );

        assertThat(response.getBody().get("datosRecibidos"))
                .isEqualTo(
                        Map.of("nombre", "Teclado")
                );

        assertThat(errores(response))
                .containsEntry(
                        "request",
                        "El cuerpo de la solicitud no tiene un formato JSON válido"
                );
    }

    @Test
    void manejarConstraintViolation_debeExtraerNombreDelCampo() {
        ConstraintViolation<?> violation =
                mock(ConstraintViolation.class);

        Path propertyPath = mock(Path.class);

        when(propertyPath.toString())
                .thenReturn(
                        "actualizarProducto.precioVenta"
                );

        when(violation.getPropertyPath())
                .thenReturn(propertyPath);

        when(violation.getMessage())
                .thenReturn(
                        "Debe ser mayor que cero"
                );

        ConstraintViolationException exception =
                new ConstraintViolationException(
                        Set.of(violation)
                );

        ResponseEntity<Map<String, Object>> response =
                handler.manejarConstraintViolation(
                        exception,
                        request("/api/productos/1/precio")
                );

        assertThat(errores(response))
                .containsEntry(
                        "precioVenta",
                        "Debe ser mayor que cero"
                );
    }

    @Test
    void manejarParametroInvalido_debeInformarParametroFaltante() {
        MissingServletRequestParameterException exception =
                new MissingServletRequestParameterException(
                        "precioVenta",
                        "Double"
                );

        ResponseEntity<Map<String, Object>> response =
                handler.manejarParametroInvalido(
                        exception,
                        request("/api/productos/1/precio")
                );

        assertThat(errores(response))
                .containsEntry(
                        "precioVenta",
                        "Campo obligatorio"
                );
    }

    @Test
    void manejarParametroInvalido_debeInformarTipoIncorrecto() {
        MethodArgumentTypeMismatchException exception =
                mock(MethodArgumentTypeMismatchException.class);

        when(exception.getName())
                .thenReturn("id");

        ResponseEntity<Map<String, Object>> response =
                handler.manejarParametroInvalido(
                        exception,
                        request("/api/productos/abc")
                );

        assertThat(errores(response))
                .containsEntry(
                        "id",
                        "Tipo de dato inválido"
                );
    }

    @Test
    void manejarNoEncontrado_debeRetornarFormatoEstandar() {
        ResponseEntity<Map<String, Object>> response =
                handler.manejarNoEncontrado(
                        new ProductoNoEncontradoException(99L),
                        request("/api/productos/99")
                );

        assertFormatoEstandar(
                response,
                HttpStatus.NOT_FOUND,
                "No se encontró el recurso solicitado",
                "/api/productos/99"
        );
    }

    @Test
    void manejarConflicto_debeRetornarFormatoEstandar() {
        ResponseEntity<Map<String, Object>> response =
                handler.manejarConflicto(
                        new ConflictoRecursoException("Duplicado"),
                        request("/api/productos")
                );

        assertFormatoEstandar(
                response,
                HttpStatus.CONFLICT,
                "El registro ya existe o genera conflicto",
                "/api/productos"
        );
    }

    @Test
    void manejarArgumentoInvalido_debeRetornarConflicto() {
        ResponseEntity<Map<String, Object>> response =
                handler.manejarArgumentoInvalido(
                        new IllegalArgumentException(
                                "El código ya existe"
                        ),
                        request("/api/productos")
                );

        assertFormatoEstandar(
                response,
                HttpStatus.CONFLICT,
                "El registro ya existe o genera conflicto",
                "/api/productos"
        );
    }

    @Test
    void manejarArgumentoInvalido_debeRetornarBadRequest() {
        ResponseEntity<Map<String, Object>> response =
                handler.manejarArgumentoInvalido(
                        new IllegalArgumentException(
                                "Precio inválido"
                        ),
                        request("/api/productos")
                );

        assertFormatoEstandar(
                response,
                HttpStatus.BAD_REQUEST,
                "Precio inválido",
                "/api/productos"
        );
    }

    @Test
    void manejarArgumentoInvalido_debeUsarMensajeGenerico() {
        ResponseEntity<Map<String, Object>> response =
                handler.manejarArgumentoInvalido(
                        new IllegalArgumentException(
                                (String) null
                        ),
                        request("/api/productos")
                );

        assertFormatoEstandar(
                response,
                HttpStatus.BAD_REQUEST,
                "Valor inválido",
                "/api/productos"
        );
    }

    @Test
    void manejarResponseStatus_debeNormalizarNotFound() {
        ResponseEntity<Map<String, Object>> response =
                handler.manejarResponseStatus(
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Detalle interno"
                        ),
                        request("/api/productos/99")
                );

        assertFormatoEstandar(
                response,
                HttpStatus.NOT_FOUND,
                "No se encontró el recurso solicitado",
                "/api/productos/99"
        );
    }

    @Test
    void manejarResponseStatus_debeNormalizarConflict() {
        ResponseEntity<Map<String, Object>> response =
                handler.manejarResponseStatus(
                        new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Detalle interno"
                        ),
                        request("/api/productos")
                );

        assertFormatoEstandar(
                response,
                HttpStatus.CONFLICT,
                "El registro ya existe o genera conflicto",
                "/api/productos"
        );
    }

    @Test
    void manejarResponseStatus_debeConservarReason() {
        ResponseEntity<Map<String, Object>> response =
                handler.manejarResponseStatus(
                        new ResponseStatusException(
                                HttpStatus.UNPROCESSABLE_ENTITY,
                                "Producto inválido"
                        ),
                        request("/api/productos")
                );

        assertFormatoEstandar(
                response,
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Producto inválido",
                "/api/productos"
        );
    }

    @Test
    void manejarResponseStatus_debeConvertirCodigoDesconocido() {
        ResponseEntity<Map<String, Object>> response =
                handler.manejarResponseStatus(
                        new ResponseStatusException(
                                HttpStatusCode.valueOf(599),
                                "Estado desconocido"
                        ),
                        request("/api/productos")
                );

        assertFormatoEstandar(
                response,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado en el servidor",
                "/api/productos"
        );
    }

    @Test
    void manejarMetodoNoSoportado_debeRetornar405() {
        ResponseEntity<Map<String, Object>> response =
                handler.manejarMetodoNoSoportado(
                        new HttpRequestMethodNotSupportedException(
                                "PATCH"
                        ),
                        request("/api/productos")
                );

        assertFormatoEstandar(
                response,
                HttpStatus.METHOD_NOT_ALLOWED,
                "Método HTTP no permitido para este recurso",
                "/api/productos"
        );
    }

    @Test
    void manejarParametroInvalido_debeMantenerErroresVaciosParaOtroTipo() {
        ResponseEntity<Map<String, Object>> response =
                handler.manejarParametroInvalido(
                        new RuntimeException("Parámetro inválido"),
                        request("/api/productos")
                );

        assertThat(errores(response)).isEmpty();
    }

    @Test
    void manejarBodyInvalido_debeUsarRequestCuandoPathEstaVacio() {
        JsonMappingException mappingException =
                mock(JsonMappingException.class);
        when(mappingException.getPath()).thenReturn(List.of());

        HttpMessageNotReadableException exception =
                mock(HttpMessageNotReadableException.class);
        when(exception.getMostSpecificCause()).thenReturn(mappingException);

        ResponseEntity<Map<String, Object>> response =
                handler.manejarBodyInvalido(
                        exception,
                        request("/api/productos")
                );

        assertThat(errores(response))
                .containsEntry(
                        "request",
                        "Tipo de dato inválido o estructura incorrecta"
                );
    }

    @Test
    void manejarBodyInvalido_debeIgnorarReferenciaSinCampoNiIndice() {
        JsonMappingException mappingException =
                mock(JsonMappingException.class);
        when(mappingException.getPath())
                .thenReturn(List.of(
                        new JsonMappingException.Reference(
                                new Object(),
                                -1
                        )
                ));

        HttpMessageNotReadableException exception =
                mock(HttpMessageNotReadableException.class);
        when(exception.getMostSpecificCause()).thenReturn(mappingException);

        ResponseEntity<Map<String, Object>> response =
                handler.manejarBodyInvalido(
                        exception,
                        request("/api/productos")
                );

        assertThat(errores(response)).containsKey("request");
    }

    @Test
    void manejarBodyInvalido_debeRetornarMapaCuandoCacheEstaVacio() {
        HttpMessageNotReadableException exception =
                mock(HttpMessageNotReadableException.class);
        when(exception.getMostSpecificCause())
                .thenReturn(new IllegalArgumentException("JSON inválido"));

        ContentCachingRequestWrapper request =
                new ContentCachingRequestWrapper(
                        request("/api/productos")
                );

        ResponseEntity<Map<String, Object>> response =
                handler.manejarBodyInvalido(exception, request);

        assertThat(response.getBody().get("datosRecibidos"))
                .isEqualTo(Map.of());
    }

    @Test
    void manejarConstraintViolation_debeAceptarPathSimpleYMensajeVacio() {
        ConstraintViolation<?> violation =
                mock(ConstraintViolation.class);
        Path propertyPath = mock(Path.class);
        when(propertyPath.toString()).thenReturn("precioVenta");
        when(violation.getPropertyPath()).thenReturn(propertyPath);
        when(violation.getMessage()).thenReturn("   ");

        ConstraintViolationException exception =
                new ConstraintViolationException(Set.of(violation));

        ResponseEntity<Map<String, Object>> response =
                handler.manejarConstraintViolation(
                        exception,
                        request("/api/productos/1/precio")
                );

        assertThat(errores(response))
                .containsEntry("precioVenta", "Valor inválido");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Registro duplicado",
            "Conflicto de datos",
            "Unique constraint"
    })
    void manejarArgumentoInvalido_debeDetectarVariantesDeConflicto(
            String mensaje
    ) {
        ResponseEntity<Map<String, Object>> response =
                handler.manejarArgumentoInvalido(
                        new IllegalArgumentException(mensaje),
                        request("/api/productos")
                );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void manejarErrorInterno_debeRetornarFormatoEstandar() {
        ResponseEntity<Map<String, Object>> response =
                handler.manejarErrorInterno(
                        new RuntimeException(
                                "Falla inesperada"
                        ),
                        request("/api/productos")
                );

        assertFormatoEstandar(
                response,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado en el servidor",
                "/api/productos"
        );
    }

    private MockHttpServletRequest request(String uri) {
        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI(uri);

        return request;
    }

    private ContentCachingRequestWrapper cachedRequest(
            String uri,
            String body,
            String characterEncoding
    ) throws Exception {

        MockHttpServletRequest request = request(uri);

        request.setContentType("application/json");

        if (characterEncoding != null) {
            request.setCharacterEncoding(
                    characterEncoding
            );
        }

        request.setContent(
                body.getBytes(StandardCharsets.UTF_8)
        );

        ContentCachingRequestWrapper wrapper =
                new ContentCachingRequestWrapper(request);

        wrapper.getInputStream().readAllBytes();

        return wrapper;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> errores(
            ResponseEntity<Map<String, Object>> response
    ) {
        return (Map<String, String>)
                response.getBody().get("errores");
    }

    private void assertFormatoEstandar(
            ResponseEntity<Map<String, Object>> response,
            HttpStatus status,
            String mensaje,
            String ruta
    ) {
        assertThat(response.getStatusCode())
                .isEqualTo(status);

        assertThat(response.getBody())
                .containsEntry(
                        "status",
                        status.value()
                )
                .containsEntry(
                        "error",
                        status.getReasonPhrase()
                )
                .containsEntry(
                        "mensaje",
                        mensaje
                )
                .containsEntry(
                        "ruta",
                        ruta
                )
                .containsKeys(
                        "timestamp",
                        "datosRecibidos",
                        "errores"
                );
    }
}

