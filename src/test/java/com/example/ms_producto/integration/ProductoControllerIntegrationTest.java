package com.example.ms_producto.integration;

import com.example.ms_producto.dto.CategoriaDto;
import com.example.ms_producto.dto.ProductoDto;
import com.example.ms_producto.entity.Producto;
import com.example.ms_producto.repository.ProductoRepository;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Pruebas de integracion - ProductoController")
class ProductoControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ProductoRepository productoRepository;

    @BeforeEach
    void setUp() {
        productoRepository.deleteAll();
        when(categoriaClient.obtenerPorId(10L)).thenReturn(categoria(10L, "Accesorios"));
        when(categoriaClient.obtenerPorId(20L)).thenReturn(categoria(20L, "Perifericos"));
    }

    @Test
    @DisplayName("POST /api/productos crea producto y lo persiste")
    void crearProducto_RetornaOk() throws Exception {
        ProductoDto request = productoDto("P001", "Mouse Gamer", 10L);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.categoriaId").value(10L))
                .andExpect(jsonPath("$.codigoInterno").value("P001"))
                .andExpect(jsonPath("$.nombre").value("Mouse Gamer"))
                .andExpect(jsonPath("$.precioVenta").value(120.0))
                .andExpect(jsonPath("$.precioCompra").value(90.0))
                .andExpect(jsonPath("$.moneda").value("Soles"));
    }

    @Test
    @DisplayName("GET /api/productos lista productos persistidos")
    void listarProductos_RetornaLista() throws Exception {
        guardarProducto("P002", "Teclado Mecanico", 10L);
        guardarProducto("P003", "Monitor LED", 20L);

        mockMvc.perform(get("/api/productos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].codigoInterno").value("P002"))
                .andExpect(jsonPath("$[1].codigoInterno").value("P003"));
    }

    @Test
    @DisplayName("GET /api/productos/{id} obtiene producto existente")
    void obtenerProducto_RetornaProducto() throws Exception {
        Producto producto = guardarProducto("P004", "Audifonos", 10L);

        mockMvc.perform(get("/api/productos/{id}", producto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(producto.getId()))
                .andExpect(jsonPath("$.codigoInterno").value("P004"))
                .andExpect(jsonPath("$.nombre").value("Audifonos"));
    }

    @Test
    @DisplayName("PUT /api/productos/{id} actualiza producto existente")
    void actualizarProducto_RetornaProductoActualizado() throws Exception {
        Producto producto = guardarProducto("P005", "Nombre Inicial", 10L);
        ProductoDto request = productoDto("P005-A", "Nombre Actualizado", 20L);
        request.setPrecioVenta(180.0);
        request.setPrecioCompra(130.0);

        mockMvc.perform(put("/api/productos/{id}", producto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(producto.getId()))
                .andExpect(jsonPath("$.categoriaId").value(20L))
                .andExpect(jsonPath("$.codigoInterno").value("P005-A"))
                .andExpect(jsonPath("$.nombre").value("Nombre Actualizado"))
                .andExpect(jsonPath("$.precioVenta").value(180.0))
                .andExpect(jsonPath("$.precioCompra").value(130.0));
    }

    @Test
    @DisplayName("DELETE /api/productos/{id} elimina producto existente")
    void eliminarProducto_RetornaNoContent() throws Exception {
        Producto producto = guardarProducto("P006", "Webcam", 10L);

        mockMvc.perform(delete("/api/productos/{id}", producto.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/productos/{id} propaga error cuando no existe")
    void obtenerProducto_CuandoNoExiste_PropagaError() {
        ServletException exception = assertThrows(ServletException.class, () ->
                mockMvc.perform(get("/api/productos/{id}", 99999L)
                        .accept(MediaType.APPLICATION_JSON)));

        assertThat(exception.getCause())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Producto no encontrado con id: 99999");
    }

    private ProductoDto productoDto(String codigoInterno, String nombre, Long categoriaId) {
        ProductoDto dto = new ProductoDto();
        dto.setCategoriaId(categoriaId);
        dto.setCodigoInterno(codigoInterno);
        dto.setNombre(nombre);
        dto.setPrecioVenta(120.0);
        dto.setPrecioCompra(90.0);
        dto.setMoneda("Soles");
        return dto;
    }

    private Producto guardarProducto(String codigoInterno, String nombre, Long categoriaId) {
        Producto producto = new Producto();
        producto.setCategoriaId(categoriaId);
        producto.setCodigoInterno(codigoInterno);
        producto.setNombre(nombre);
        producto.setPrecioVenta(120.0);
        producto.setPrecioCompra(90.0);
        producto.setMoneda("Soles");
        return productoRepository.save(producto);
    }

    private CategoriaDto categoria(Long id, String nombre) {
        CategoriaDto dto = new CategoriaDto();
        dto.setId(id);
        dto.setNombre(nombre);
        return dto;
    }
}
