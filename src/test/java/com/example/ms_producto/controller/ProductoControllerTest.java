package com.example.ms_producto.controller;

import com.example.ms_producto.dto.ProductoDto;
import com.example.ms_producto.exception.ProductoNoEncontradoException;
import com.example.ms_producto.service.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductoService productoService;

    private ProductoDto crearDto() {
        ProductoDto dto = new ProductoDto();
        dto.setId(1L);
        dto.setCategoriaId(10L);
        dto.setCodigoInterno("P001");
        dto.setNombre("Mouse Gamer");
        dto.setImagen("mouse.png");
        dto.setPrecioVenta(120.0);
        dto.setPrecioCompra(90.0);
        dto.setMoneda("Soles");
        return dto;
    }

    @Test
    void crear_debeRetornarProductoCreado() throws Exception {
        ProductoDto dto = crearDto();

        when(productoService.crearProducto(any(ProductoDto.class))).thenReturn(dto);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigoInterno").value("P001"))
                .andExpect(jsonPath("$.nombre").value("Mouse Gamer"))
                .andExpect(jsonPath("$.imagen").value("mouse.png"));

        verify(productoService).crearProducto(any(ProductoDto.class));
    }

    @Test
    void crear_conCamposVacios_debeRetornarBadRequest() throws Exception {
        ProductoDto dto = new ProductoDto();
        dto.setCodigoInterno("");
        dto.setNombre("");

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.mensaje").value("Se encontraron errores de validación"))
                .andExpect(jsonPath("$.ruta").value("/api/productos"))
                .andExpect(jsonPath("$.datosRecibidos.codigoInterno").value(""))
                .andExpect(jsonPath("$.datosRecibidos.nombre").value(""))
                .andExpect(jsonPath("$.errores.categoriaId").value("Campo obligatorio"))
                .andExpect(jsonPath("$.errores.codigoInterno").value("Campo obligatorio"))
                .andExpect(jsonPath("$.errores.nombre").value("Campo obligatorio"))
                .andExpect(jsonPath("$.errores.precioVenta").value("Campo obligatorio"))
                .andExpect(jsonPath("$.errores.precioCompra").value("Campo obligatorio"));

        verifyNoInteractions(productoService);
    }

    @Test
    void obtener_debeRetornarProducto() throws Exception {
        ProductoDto dto = crearDto();

        when(productoService.obtenerProducto(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Mouse Gamer"));

        verify(productoService).obtenerProducto(1L);
    }

    @Test
    void obtener_cuandoNoExiste_debeRetornarNotFound() throws Exception {
        when(productoService.obtenerProducto(99L)).thenThrow(new ProductoNoEncontradoException(99L));

        mockMvc.perform(get("/api/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.mensaje").value("No se encontró el recurso solicitado"))
                .andExpect(jsonPath("$.ruta").value("/api/productos/99"));

        verify(productoService).obtenerProducto(99L);
    }

    @Test
    void listar_debeRetornarListaDeProductos() throws Exception {
        ProductoDto dto = crearDto();

        when(productoService.listarProductos()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].codigoInterno").value("P001"));

        verify(productoService).listarProductos();
    }

    @Test
    void actualizar_debeRetornarProductoActualizado() throws Exception {
        ProductoDto dto = crearDto();
        dto.setNombre("Mouse Actualizado");

        when(productoService.actualizarProducto(eq(1L), any(ProductoDto.class))).thenReturn(dto);

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Mouse Actualizado"));

        verify(productoService).actualizarProducto(eq(1L), any(ProductoDto.class));
    }

    @Test
    void actualizar_conCamposVacios_debeRetornarBadRequest() throws Exception {
        ProductoDto dto = new ProductoDto();
        dto.setCodigoInterno(" ");
        dto.setNombre(" ");

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.mensaje").value("Se encontraron errores de validación"))
                .andExpect(jsonPath("$.ruta").value("/api/productos/1"))
                .andExpect(jsonPath("$.datosRecibidos.codigoInterno").value(" "))
                .andExpect(jsonPath("$.datosRecibidos.nombre").value(" "))
                .andExpect(jsonPath("$.errores.categoriaId").value("Campo obligatorio"))
                .andExpect(jsonPath("$.errores.codigoInterno").value("Campo obligatorio"))
                .andExpect(jsonPath("$.errores.nombre").value("Campo obligatorio"))
                .andExpect(jsonPath("$.errores.precioVenta").value("Campo obligatorio"))
                .andExpect(jsonPath("$.errores.precioCompra").value("Campo obligatorio"));

        verifyNoInteractions(productoService);
    }

    @Test
    void actualizar_conPrecioInvalido_debeRetornarBadRequest() throws Exception {
        ProductoDto dto = crearDto();
        dto.setPrecioVenta(0.0);

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.mensaje").value("Se encontraron errores de validación"))
                .andExpect(jsonPath("$.ruta").value("/api/productos/1"))
                .andExpect(jsonPath("$.datosRecibidos.precioVenta").value(0.0))
                .andExpect(jsonPath("$.errores.precioVenta").value("Debe ser mayor a cero"));

        verifyNoInteractions(productoService);
    }

    @Test
    void actualizar_conTipoDeDatoInvalido_debeRetornarBadRequest() throws Exception {
        String request = """
                {
                  "categoriaId": 10,
                  "codigoInterno": "P001",
                  "nombre": "Mouse Gamer",
                  "precioVenta": "abc",
                  "precioCompra": 90.0,
                  "moneda": "Soles"
                }
                """;

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.mensaje").value("Se encontraron errores de validación"))
                .andExpect(jsonPath("$.ruta").value("/api/productos/1"))
                .andExpect(jsonPath("$.datosRecibidos.precioVenta").value("abc"))
                .andExpect(jsonPath("$.errores.precioVenta").value("Tipo de dato inválido o estructura incorrecta"));

        verifyNoInteractions(productoService);
    }

    @Test
    void crear_cuandoHayConflicto_debeRetornarConflict() throws Exception {
        ProductoDto dto = crearDto();
        when(productoService.crearProducto(any(ProductoDto.class)))
                .thenThrow(new IllegalArgumentException("Ya existe un producto con ese código interno"));

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.mensaje").value("El registro ya existe o genera conflicto"))
                .andExpect(jsonPath("$.ruta").value("/api/productos"))
                .andExpect(jsonPath("$.datosRecibidos").isMap())
                .andExpect(jsonPath("$.errores").isMap());

        verify(productoService).crearProducto(any(ProductoDto.class));
    }

    @Test
    void listar_cuandoOcurreErrorInesperado_debeRetornarInternalServerError() throws Exception {
        when(productoService.listarProductos()).thenThrow(new RuntimeException("Falla inesperada"));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.mensaje").value("Ocurrió un error inesperado en el servidor"))
                .andExpect(jsonPath("$.ruta").value("/api/productos"))
                .andExpect(jsonPath("$.datosRecibidos").isMap())
                .andExpect(jsonPath("$.errores").isMap());

        verify(productoService).listarProductos();
    }

    @Test
    void eliminar_debeRetornarNoContent() throws Exception {
        doNothing().when(productoService).eliminarProducto(1L);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());

        verify(productoService).eliminarProducto(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_debeRetornarNotFound() throws Exception {
        doThrow(new ProductoNoEncontradoException(99L)).when(productoService).eliminarProducto(99L);

        mockMvc.perform(delete("/api/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.mensaje").value("No se encontró el recurso solicitado"))
                .andExpect(jsonPath("$.ruta").value("/api/productos/99"));

        verify(productoService).eliminarProducto(99L);
    }

    @Test
    void actualizarPrecio_debeRetornarNoContent() throws Exception {
        mockMvc.perform(put("/api/productos/1/precio")
                        .param("precioVenta", "150.0"))
                .andExpect(status().isNoContent());
    }
}
