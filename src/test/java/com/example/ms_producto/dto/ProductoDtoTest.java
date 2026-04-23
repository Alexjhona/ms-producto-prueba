package com.example.ms_producto.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductoDtoTest {

    @Test
    void gettersAndSetters_debenFuncionarCorrectamente() {
        ProductoDto dto = new ProductoDto();
        dto.setId(1L);
        dto.setCategoriaId(10L);
        dto.setCodigoInterno("P001");
        dto.setNombre("Mouse");
        dto.setPrecioVenta(100.0);
        dto.setPrecioCompra(80.0);
        dto.setMoneda("Soles");

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getCategoriaId());
        assertEquals("P001", dto.getCodigoInterno());
        assertEquals("Mouse", dto.getNombre());
        assertEquals(100.0, dto.getPrecioVenta());
        assertEquals(80.0, dto.getPrecioCompra());
        assertEquals("Soles", dto.getMoneda());
    }

    @Test
    void constructorVacio_debeCrearObjeto() {
        ProductoDto dto = new ProductoDto();
        assertNotNull(dto);
    }
}