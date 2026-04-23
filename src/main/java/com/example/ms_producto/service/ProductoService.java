package com.example.ms_producto.service;

import com.example.ms_producto.dto.ProductoDto;
import java.util.List;

public interface ProductoService {
    ProductoDto crearProducto(ProductoDto productoDto);
    ProductoDto obtenerProducto(Long id);
    List<ProductoDto> listarProductos();
    ProductoDto actualizarProducto(Long id, ProductoDto productoDto);
    void eliminarProducto(Long id);
}