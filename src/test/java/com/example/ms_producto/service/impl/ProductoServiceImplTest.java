package com.example.ms_producto.service.impl;

import com.example.ms_producto.dto.CategoriaDto;
import com.example.ms_producto.dto.ProductoDto;
import com.example.ms_producto.entity.Producto;
import com.example.ms_producto.feign.CategoriaClient;
import com.example.ms_producto.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaClient categoriaClient;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Producto producto;
    private ProductoDto productoDto;
    private CategoriaDto categoriaDto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setCategoriaId(10L);
        producto.setCodigoInterno("P001");
        producto.setNombre("Mouse Gamer");
        producto.setPrecioVenta(120.0);
        producto.setPrecioCompra(90.0);
        producto.setMoneda("Soles");

        productoDto = new ProductoDto();
        productoDto.setId(1L);
        productoDto.setCategoriaId(10L);
        productoDto.setCodigoInterno("P001");
        productoDto.setNombre("Mouse Gamer");
        productoDto.setPrecioVenta(120.0);
        productoDto.setPrecioCompra(90.0);
        productoDto.setMoneda("Soles");

        categoriaDto = new CategoriaDto();
        categoriaDto.setId(10L);
        categoriaDto.setNombre("Accesorios");
    }

    @Test
    void crearProducto_debeCrearCorrectamente() {
        when(categoriaClient.obtenerPorId(10L)).thenReturn(categoriaDto);
        when(productoRepository.existsByCodigoInterno("P001")).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        ProductoDto resultado = productoService.crearProducto(productoDto);

        assertNotNull(resultado);
        assertEquals("P001", resultado.getCodigoInterno());
        assertEquals("Mouse Gamer", resultado.getNombre());
        verify(categoriaClient).obtenerPorId(10L);
        verify(productoRepository).existsByCodigoInterno("P001");
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void crearProducto_debeLanzarExcepcionCuandoCategoriaNoExiste() {
        when(categoriaClient.obtenerPorId(10L)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> productoService.crearProducto(productoDto)
        );

        assertEquals("Categoría no existe con id: 10", ex.getMessage());
        verify(categoriaClient).obtenerPorId(10L);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void crearProducto_debeLanzarExcepcionCuandoCodigoYaExiste() {
        when(categoriaClient.obtenerPorId(10L)).thenReturn(categoriaDto);
        when(productoRepository.existsByCodigoInterno("P001")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> productoService.crearProducto(productoDto)
        );

        assertEquals("Ya existe un producto con ese código interno", ex.getMessage());
        verify(categoriaClient).obtenerPorId(10L);
        verify(productoRepository).existsByCodigoInterno("P001");
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void obtenerProducto_debeRetornarProductoCuandoExiste() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        ProductoDto resultado = productoService.obtenerProducto(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Mouse Gamer", resultado.getNombre());
        verify(productoRepository).findById(1L);
    }

    @Test
    void obtenerProducto_debeLanzarExcepcionCuandoNoExiste() {
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> productoService.obtenerProducto(1L)
        );

        assertEquals("Producto no encontrado con id: 1", ex.getMessage());
        verify(productoRepository).findById(1L);
    }

    @Test
    void listarProductos_debeRetornarLista() {
        Producto producto2 = new Producto();
        producto2.setId(2L);
        producto2.setCategoriaId(20L);
        producto2.setCodigoInterno("P002");
        producto2.setNombre("Teclado");
        producto2.setPrecioVenta(150.0);
        producto2.setPrecioCompra(100.0);
        producto2.setMoneda("Soles");

        when(productoRepository.findAll()).thenReturn(List.of(producto, producto2));

        List<ProductoDto> resultado = productoService.listarProductos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("P001", resultado.get(0).getCodigoInterno());
        assertEquals("P002", resultado.get(1).getCodigoInterno());
        verify(productoRepository).findAll();
    }

    @Test
    void actualizarProducto_debeActualizarCorrectamenteSinCambiarCategoriaNiCodigo() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        ProductoDto resultado = productoService.actualizarProducto(1L, productoDto);

        assertNotNull(resultado);
        assertEquals("Mouse Gamer", resultado.getNombre());
        verify(productoRepository).findById(1L);
        verify(productoRepository).save(any(Producto.class));
        verify(categoriaClient, never()).obtenerPorId(any(Long.class));
    }

    @Test
    void actualizarProducto_debeValidarCategoriaCuandoCambioCategoria() {
        ProductoDto nuevoDto = new ProductoDto();
        nuevoDto.setId(1L);
        nuevoDto.setCategoriaId(99L);
        nuevoDto.setCodigoInterno("P001");
        nuevoDto.setNombre("Mouse Gamer");
        nuevoDto.setPrecioVenta(120.0);
        nuevoDto.setPrecioCompra(90.0);
        nuevoDto.setMoneda("Soles");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(categoriaClient.obtenerPorId(99L)).thenReturn(categoriaDto);

        Producto productoActualizado = new Producto();
        productoActualizado.setId(1L);
        productoActualizado.setCategoriaId(99L);
        productoActualizado.setCodigoInterno("P001");
        productoActualizado.setNombre("Mouse Gamer");
        productoActualizado.setPrecioVenta(120.0);
        productoActualizado.setPrecioCompra(90.0);
        productoActualizado.setMoneda("Soles");

        when(productoRepository.save(any(Producto.class))).thenReturn(productoActualizado);

        ProductoDto resultado = productoService.actualizarProducto(1L, nuevoDto);

        assertNotNull(resultado);
        assertEquals(99L, resultado.getCategoriaId());
        verify(productoRepository).findById(1L);
        verify(categoriaClient).obtenerPorId(99L);
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void actualizarProducto_debeLanzarExcepcionCuandoProductoNoExiste() {
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> productoService.actualizarProducto(1L, productoDto)
        );

        assertEquals("Producto no encontrado con id: 1", ex.getMessage());
        verify(productoRepository).findById(1L);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void actualizarProducto_debeLanzarExcepcionCuandoNuevaCategoriaNoExiste() {
        ProductoDto nuevoDto = new ProductoDto();
        nuevoDto.setId(1L);
        nuevoDto.setCategoriaId(99L);
        nuevoDto.setCodigoInterno("P001");
        nuevoDto.setNombre("Mouse Gamer");
        nuevoDto.setPrecioVenta(120.0);
        nuevoDto.setPrecioCompra(90.0);
        nuevoDto.setMoneda("Soles");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(categoriaClient.obtenerPorId(99L)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> productoService.actualizarProducto(1L, nuevoDto)
        );

        assertEquals("Categoría no existe con id: 99", ex.getMessage());
        verify(productoRepository).findById(1L);
        verify(categoriaClient).obtenerPorId(99L);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void actualizarProducto_debeLanzarExcepcionCuandoNuevoCodigoYaExiste() {
        ProductoDto nuevoDto = new ProductoDto();
        nuevoDto.setId(1L);
        nuevoDto.setCategoriaId(10L);
        nuevoDto.setCodigoInterno("P999");
        nuevoDto.setNombre("Mouse Gamer");
        nuevoDto.setPrecioVenta(120.0);
        nuevoDto.setPrecioCompra(90.0);
        nuevoDto.setMoneda("Soles");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.existsByCodigoInterno("P999")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> productoService.actualizarProducto(1L, nuevoDto)
        );

        assertEquals("Ya existe otro producto con ese código interno", ex.getMessage());
        verify(productoRepository).findById(1L);
        verify(productoRepository).existsByCodigoInterno("P999");
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void eliminarProducto_debeEliminarCuandoExiste() {
        when(productoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(1L);

        assertDoesNotThrow(() -> productoService.eliminarProducto(1L));

        verify(productoRepository).existsById(1L);
        verify(productoRepository).deleteById(1L);
    }

    @Test
    void eliminarProducto_debeLanzarExcepcionCuandoNoExiste() {
        when(productoRepository.existsById(1L)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> productoService.eliminarProducto(1L)
        );

        assertEquals("No existe producto con id: 1", ex.getMessage());
        verify(productoRepository).existsById(1L);
        verify(productoRepository, never()).deleteById(any(Long.class));
    }
}