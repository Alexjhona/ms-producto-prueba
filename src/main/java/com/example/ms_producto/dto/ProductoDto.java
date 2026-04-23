package com.example.ms_producto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductoDto {

    private Long id;

    @NotNull
    private Long categoriaId;

    @NotBlank
    private String codigoInterno;

    @NotBlank
    private String nombre;

    @NotNull
    private Double precioVenta;

    @NotNull
    private Double precioCompra;

    private String moneda = "Soles";
}