package com.example.ms_producto.feign;

import com.example.ms_producto.dto.CategoriaDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-categoria-service", url = "${feign.categoria.url}")
public interface CategoriaClient {
    @GetMapping("/{id}")
    CategoriaDto obtenerPorId(@PathVariable("id") Long id);
}