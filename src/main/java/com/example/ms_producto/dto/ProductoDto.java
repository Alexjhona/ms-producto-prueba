package com.example.ms_producto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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

    public ProductoDto() {}

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public String getCodigoInterno() { return codigoInterno; }
    public void setCodigoInterno(String codigoInterno) { this.codigoInterno = codigoInterno; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(Double precioVenta) { this.precioVenta = precioVenta; }

    public Double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(Double precioCompra) { this.precioCompra = precioCompra; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
}