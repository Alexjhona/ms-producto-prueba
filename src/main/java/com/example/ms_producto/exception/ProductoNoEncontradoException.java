package com.example.ms_producto.exception;

public class ProductoNoEncontradoException extends RecursoNoEncontradoException {

    public ProductoNoEncontradoException(Long id) {
        super("Producto no encontrado con id: " + id);
    }
}
