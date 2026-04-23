package com.example.ms_producto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "categoria_id", nullable = false)
    private Long categoriaId;

    @NotBlank
    @Column(name = "codigo_interno", nullable = false, unique = true)
    private String codigoInterno;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    @NotNull
    @Column(name = "precio_venta", nullable = false)
    private Double precioVenta;

    @NotNull
    @Column(name = "precio_compra", nullable = false)
    private Double precioCompra;

    @NotBlank
    @Column(nullable = false)
    private String moneda = "Soles";
}