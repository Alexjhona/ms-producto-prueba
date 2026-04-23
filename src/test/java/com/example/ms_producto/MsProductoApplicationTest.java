package com.example.ms_producto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MsProductoApplicationTest {

    @Test
    void constructor_debeCrearInstancia() {
        MsProductoApplication app = new MsProductoApplication();
        assertNotNull(app);
    }
}