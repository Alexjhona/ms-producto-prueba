package com.example.ms_producto.repository;

import com.example.ms_producto.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByCodigoInterno(String codigoInterno);
    boolean existsByCodigoInterno(String codigoInterno);
}