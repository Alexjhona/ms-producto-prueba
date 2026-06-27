package com.example.ms_producto.controller;

import com.example.ms_producto.dto.ProductoDto;
import com.example.ms_producto.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Endpoints para administrar productos, categorias asociadas y precios de venta.")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    @Operation(summary = "Registrar producto", description = "Registra un nuevo producto en el sistema y lo relaciona con una categoria existente. Puede consultar el microservicio de categorias para validar la categoria asociada antes de completar el registro.")
            @ApiResponse(responseCode = "200", description = "Producto registrado correctamente")
            @ApiResponse(responseCode = "400", description = "Datos de producto invalidos")
    public ResponseEntity<ProductoDto> crear(@Valid @RequestBody ProductoDto productoDto) {
        return ResponseEntity.ok(productoService.crearProducto(productoDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por id", description = "Obtiene el detalle de un producto registrado usando su identificador unico, incluyendo sus datos principales para gestion comercial.")
            @ApiResponse(responseCode = "200", description = "Producto encontrado")
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    public ResponseEntity<ProductoDto> obtener(@Parameter(description = "Identificador unico del recurso producto.", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerProducto(id));
    }

    @GetMapping
    @Operation(summary = "Listar productos", description = "Obtiene todos los productos registrados en el sistema junto con sus datos principales para la gestion comercial.")
    @ApiResponse(responseCode = "200", description = "Listado de productos obtenido correctamente")
    public ResponseEntity<List<ProductoDto>> listar() {
        return ResponseEntity.ok(productoService.listarProductos());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto", description = "Modifica los datos principales de un producto existente usando su identificador unico. No cambia la ruta ni crea un producto nuevo.")
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente")
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    public ResponseEntity<ProductoDto> actualizar(@Parameter(description = "Identificador unico del recurso producto.", example = "1") @PathVariable Long id, @Valid @RequestBody ProductoDto productoDto) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, productoDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto registrado mediante su identificador. Verificar previamente si el producto esta asociado a inventario, compras o ventas.")
            @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente")
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    public ResponseEntity<Void> eliminar(@Parameter(description = "Identificador unico del recurso producto.", example = "1") @PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/precio")
    @Operation(summary = "Actualizar precio de producto", description = "Actualiza el precio de venta de un producto especifico sin modificar el resto de sus datos. Este endpoint puede ser utilizado por procesos relacionados con compras o actualizacion comercial de precios.")
            @ApiResponse(responseCode = "204", description = "Precio actualizado correctamente")
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    public ResponseEntity<Void> actualizarPrecio(
            @Parameter(description = "Identificador unico del recurso producto.", example = "1") @PathVariable Long id,
            @Parameter(description = "Nuevo precio de venta que se asignara al producto.", example = "25.90") @RequestParam Double precioVenta) {
        return ResponseEntity.noContent().build();
    }
}
