package com.example.ms_producto.controller;

import com.example.ms_producto.dto.ProductoDto;
import com.example.ms_producto.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    public ResponseEntity<ProductoDto> crear(@RequestBody ProductoDto productoDto) {
        return ResponseEntity.ok(productoService.crearProducto(productoDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDto> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerProducto(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductoDto>> listar() {
        return ResponseEntity.ok(productoService.listarProductos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDto> actualizar(@PathVariable Long id, @RequestBody ProductoDto productoDto) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, productoDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/precio")
    public ResponseEntity<Void> actualizarPrecio(
            @PathVariable Long id,
            @RequestParam Double precioVenta) {
        // … lógica para actualizar el precio …
        return ResponseEntity.noContent().build();
    }
}