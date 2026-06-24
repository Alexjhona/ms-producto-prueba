package com.example.ms_producto.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
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
        dto.setImagen("mouse.png");
        dto.setPrecioVenta(100.0);
        dto.setPrecioCompra(80.0);
        dto.setMoneda("Soles");

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getCategoriaId());
        assertEquals("P001", dto.getCodigoInterno());
        assertEquals("Mouse", dto.getNombre());
        assertEquals("mouse.png", dto.getImagen());
        assertEquals(100.0, dto.getPrecioVenta());
        assertEquals(80.0, dto.getPrecioCompra());
        assertEquals("Soles", dto.getMoneda());
    }

    @Test
    void constructorVacio_debeCrearObjeto() {
        ProductoDto dto = new ProductoDto();
        assertNotNull(dto);
    }

    @Test
    void validacionJakarta_conDtoValido_noDebeRetornarErrores() {
        ProductoDto dto = dtoValido();

        Set<ConstraintViolation<ProductoDto>> errores = validator().validate(dto);

        assertThat(errores).isEmpty();
    }

    @Test
    void validacionJakarta_conCamposInvalidos_debeRetornarErrores() {
        ProductoDto dto = new ProductoDto();
        dto.setCategoriaId(0L);
        dto.setCodigoInterno(" ");
        dto.setNombre("");
        dto.setPrecioVenta(-1.0);
        dto.setPrecioCompra(null);

        Set<ConstraintViolation<ProductoDto>> errores = validator().validate(dto);

        assertThat(errores)
                .extracting(error -> error.getPropertyPath().toString())
                .contains("categoriaId", "codigoInterno", "nombre", "precioVenta", "precioCompra");
        assertThat(errores)
                .extracting(ConstraintViolation::getMessage)
                .contains("Debe ser mayor a cero", "Campo obligatorio");
    }

    private ProductoDto dtoValido() {
        ProductoDto dto = new ProductoDto();
        dto.setCategoriaId(10L);
        dto.setCodigoInterno("P001");
        dto.setNombre("Mouse");
        dto.setPrecioVenta(100.0);
        dto.setPrecioCompra(80.0);
        dto.setMoneda("Soles");
        return dto;
    }

    private Validator validator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }
}
