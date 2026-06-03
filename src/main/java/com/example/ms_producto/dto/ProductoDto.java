package com.example.ms_producto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductoDto {

    private Long id;

    @NotNull(message = "Campo obligatorio")
    @Positive(message = "Debe ser mayor a cero")
    private Long categoriaId;

    @NotBlank(message = "Campo obligatorio")
    private String codigoInterno;

    @NotBlank(message = "Campo obligatorio")
    private String nombre;

    @NotNull(message = "Campo obligatorio")
    @Positive(message = "Debe ser mayor a cero")
    private Double precioVenta;

    @NotNull(message = "Campo obligatorio")
    @Positive(message = "Debe ser mayor a cero")
    private Double precioCompra;

    private String moneda = "Soles";
}
