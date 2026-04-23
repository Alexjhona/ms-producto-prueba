package com.example.ms_producto.service.impl;

import com.example.ms_producto.dto.CategoriaDto;
import com.example.ms_producto.dto.ProductoDto;
import com.example.ms_producto.entity.Producto;
import com.example.ms_producto.feign.CategoriaClient;
import com.example.ms_producto.repository.ProductoRepository;
import com.example.ms_producto.service.ProductoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaClient categoriaClient;

    public ProductoServiceImpl(ProductoRepository productoRepository, CategoriaClient categoriaClient) {
        this.productoRepository = productoRepository;
        this.categoriaClient = categoriaClient;
    }

    @Override
    public ProductoDto crearProducto(ProductoDto productoDto) {
        // Validar categoría existe
        CategoriaDto cat = categoriaClient.obtenerPorId(productoDto.getCategoriaId());
        if (cat == null) {
            throw new IllegalArgumentException("Categoría no existe con id: " + productoDto.getCategoriaId());
        }
        // Validar código interno único
        if (productoRepository.existsByCodigoInterno(productoDto.getCodigoInterno())) {
            throw new IllegalArgumentException("Ya existe un producto con ese código interno");
        }
        Producto p = new Producto();
        p.setCategoriaId(productoDto.getCategoriaId());
        p.setCodigoInterno(productoDto.getCodigoInterno());
        p.setNombre(productoDto.getNombre());
        p.setPrecioVenta(productoDto.getPrecioVenta());
        p.setPrecioCompra(productoDto.getPrecioCompra());
        p.setMoneda(productoDto.getMoneda());

        Producto guardado = productoRepository.save(p);
        return mapToDto(guardado);
    }

    @Override
    public ProductoDto obtenerProducto(Long id) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id: " + id));
        return mapToDto(p);
    }

    @Override
    public List<ProductoDto> listarProductos() {
        return productoRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductoDto actualizarProducto(Long id, ProductoDto productoDto) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id: " + id));

        // Si cambió categoria, validar nuevamente
        if (!existente.getCategoriaId().equals(productoDto.getCategoriaId())) {
            CategoriaDto cat = categoriaClient.obtenerPorId(productoDto.getCategoriaId());
            if (cat == null) {
                throw new IllegalArgumentException("Categoría no existe con id: " + productoDto.getCategoriaId());
            }
        }
        // Si cambió código interno, validar unicidad
        if (!existente.getCodigoInterno().equals(productoDto.getCodigoInterno())
                && productoRepository.existsByCodigoInterno(productoDto.getCodigoInterno())) {
            throw new IllegalArgumentException("Ya existe otro producto con ese código interno");
        }

        existente.setCategoriaId(productoDto.getCategoriaId());
        existente.setCodigoInterno(productoDto.getCodigoInterno());
        existente.setNombre(productoDto.getNombre());
        existente.setPrecioVenta(productoDto.getPrecioVenta());
        existente.setPrecioCompra(productoDto.getPrecioCompra());
        existente.setMoneda(productoDto.getMoneda());

        Producto actualizado = productoRepository.save(existente);
        return mapToDto(actualizado);
    }
    

    @Override
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe producto con id: " + id);
        }
        productoRepository.deleteById(id);
    }

    private ProductoDto mapToDto(Producto p) {
        ProductoDto dto = new ProductoDto();
        dto.setId(p.getId());
        dto.setCategoriaId(p.getCategoriaId());
        dto.setCodigoInterno(p.getCodigoInterno());
        dto.setNombre(p.getNombre());
        dto.setPrecioVenta(p.getPrecioVenta());
        dto.setPrecioCompra(p.getPrecioCompra());
        dto.setMoneda(p.getMoneda());
        return dto;
    }
}