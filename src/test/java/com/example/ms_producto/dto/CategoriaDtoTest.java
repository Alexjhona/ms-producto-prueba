package com.example.ms_producto.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CategoriaDtoTest {

    @Test
    void gettersAndSetters_debenFuncionarCorrectamente() {
        CategoriaDto dto = new CategoriaDto();

        dto.setId(1L);
        dto.setNombre("Accesorios");
        dto.setImagen("categoria.png");

        assertEquals(1L, dto.getId());
        assertEquals("Accesorios", dto.getNombre());
        assertEquals("categoria.png", dto.getImagen());
    }

    @Test
    void constructorVacio_debeCrearObjeto() {
        CategoriaDto dto = new CategoriaDto();

        assertNotNull(dto);
    }
}