package com.example.ms_producto.repository;

import com.example.ms_producto.config.MySQLTestContainer;
import com.example.ms_producto.entity.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("tc")
@DisplayName("Pruebas de integracion - ProductoRepository con MySQL real")
class ProductoRepositoryIntegrationTest {

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MySQLTestContainer.INSTANCE::getJdbcUrl);
        registry.add("spring.datasource.username", MySQLTestContainer.INSTANCE::getUsername);
        registry.add("spring.datasource.password", MySQLTestContainer.INSTANCE::getPassword);
        registry.add("spring.datasource.driver-class-name", MySQLTestContainer.INSTANCE::getDriverClassName);
    }

    @Autowired
    private ProductoRepository productoRepository;

    @BeforeEach
    void setUp() {
        productoRepository.deleteAll();
    }

    @Test
    @DisplayName("Guardar producto - debe persistir y generar id")
    void guardarProducto_DebePersistirConId() {
        Producto producto = producto("P100", "Mouse Inalambrico", 10L);

        Producto guardado = productoRepository.save(producto);

        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getCodigoInterno()).isEqualTo("P100");
        assertThat(guardado.getNombre()).isEqualTo("Mouse Inalambrico");
    }

    @Test
    @DisplayName("Buscar producto por id - retorna producto existente")
    void buscarPorId_RetornaProductoExistente() {
        Producto guardado = productoRepository.save(producto("P101", "Teclado", 10L));

        assertThat(productoRepository.findById(guardado.getId()))
                .isPresent()
                .get()
                .satisfies(encontrado -> {
                    assertThat(encontrado.getCodigoInterno()).isEqualTo("P101");
                    assertThat(encontrado.getNombre()).isEqualTo("Teclado");
                });
    }

    @Test
    @DisplayName("Buscar producto por codigo interno - retorna producto existente")
    void buscarPorCodigoInterno_RetornaProductoExistente() {
        productoRepository.save(producto("P102", "Monitor", 20L));

        assertThat(productoRepository.findByCodigoInterno("P102"))
                .isPresent()
                .get()
                .satisfies(encontrado -> assertThat(encontrado.getCategoriaId()).isEqualTo(20L));
        assertThat(productoRepository.existsByCodigoInterno("P102")).isTrue();
    }

    @Test
    @DisplayName("Listar productos - retorna productos persistidos")
    void listarProductos_RetornaLista() {
        productoRepository.save(producto("P103", "Audifonos", 10L));
        productoRepository.save(producto("P104", "Webcam", 20L));

        List<Producto> productos = productoRepository.findAll();

        assertThat(productos).hasSize(2);
        assertThat(productos)
                .extracting(Producto::getCodigoInterno)
                .containsExactlyInAnyOrder("P103", "P104");
    }

    @Test
    @DisplayName("Eliminar producto - ya no debe existir")
    void eliminarProducto_NoExisteDespues() {
        Producto guardado = productoRepository.save(producto("P105", "Parlantes", 10L));

        productoRepository.deleteById(guardado.getId());

        assertThat(productoRepository.findById(guardado.getId())).isEmpty();
    }

    private Producto producto(String codigoInterno, String nombre, Long categoriaId) {
        Producto producto = new Producto();
        producto.setCategoriaId(categoriaId);
        producto.setCodigoInterno(codigoInterno);
        producto.setNombre(nombre);
        producto.setPrecioVenta(120.0);
        producto.setPrecioCompra(90.0);
        producto.setMoneda("Soles");
        return producto;
    }
}
