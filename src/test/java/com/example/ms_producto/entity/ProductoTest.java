package com.example.ms_producto.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductoTest {

    @Test
    void gettersAndSetters_debenFuncionarCorrectamente() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setCategoriaId(10L);
        producto.setCodigoInterno("P001");
        producto.setNombre("Mouse");
        producto.setPrecioVenta(120.0);
        producto.setPrecioCompra(90.0);
        producto.setMoneda("Soles");

        assertEquals(1L, producto.getId());
        assertEquals(10L, producto.getCategoriaId());
        assertEquals("P001", producto.getCodigoInterno());
        assertEquals("Mouse", producto.getNombre());
        assertEquals(120.0, producto.getPrecioVenta());
        assertEquals(90.0, producto.getPrecioCompra());
        assertEquals("Soles", producto.getMoneda());
    }

    @Test
    void constructorVacio_debeCrearObjeto() {
        Producto producto = new Producto();
        assertNotNull(producto);
    }
}