package com.example.ms_producto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MsProductoApplicationTest {

	@Test
	void main_debeEjecutarseSinErrores() {
		assertDoesNotThrow(() -> MsProductoApplication.main(new String[]{}));
	}
}