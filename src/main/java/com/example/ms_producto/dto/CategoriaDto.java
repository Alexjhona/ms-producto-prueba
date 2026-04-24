package com.example.ms_producto.dto;

public class CategoriaDto {
    private Long id;
    private String nombre;

    public CategoriaDto() {
        // Constructor vacío requerido para deserialización JSON.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}