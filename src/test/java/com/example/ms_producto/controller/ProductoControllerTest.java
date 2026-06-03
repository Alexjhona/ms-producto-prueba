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
                .andExpect(jsonPath("$.nombre").value("Mouse Gamer"));

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
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validación fallida"))
                .andExpect(jsonPath("$.mensajes.categoriaId").value("Campo obligatorio"))
                .andExpect(jsonPath("$.mensajes.codigoInterno").value("Campo obligatorio"))
                .andExpect(jsonPath("$.mensajes.nombre").value("Campo obligatorio"))
                .andExpect(jsonPath("$.mensajes.precioVenta").value("Campo obligatorio"))
                .andExpect(jsonPath("$.mensajes.precioCompra").value("Campo obligatorio"));

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
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Producto no encontrado con id: 99"));

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
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validación fallida"))
                .andExpect(jsonPath("$.mensajes.categoriaId").value("Campo obligatorio"))
                .andExpect(jsonPath("$.mensajes.codigoInterno").value("Campo obligatorio"))
                .andExpect(jsonPath("$.mensajes.nombre").value("Campo obligatorio"))
                .andExpect(jsonPath("$.mensajes.precioVenta").value("Campo obligatorio"))
                .andExpect(jsonPath("$.mensajes.precioCompra").value("Campo obligatorio"));

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
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validación fallida"))
                .andExpect(jsonPath("$.mensajes.precioVenta").value("Debe ser mayor a cero"));

        verifyNoInteractions(productoService);
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
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Producto no encontrado con id: 99"));

        verify(productoService).eliminarProducto(99L);
    }

    @Test
    void actualizarPrecio_debeRetornarNoContent() throws Exception {
        mockMvc.perform(put("/api/productos/1/precio")
                        .param("precioVenta", "150.0"))
                .andExpect(status().isNoContent());
    }
}
