package com.example.ms_producto.integration;

import com.example.ms_producto.config.MySQLTestContainer;
import com.example.ms_producto.feign.CategoriaClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @MockitoBean
    protected CategoriaClient categoriaClient;

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MySQLTestContainer.INSTANCE::getJdbcUrl);
        registry.add("spring.datasource.username", MySQLTestContainer.INSTANCE::getUsername);
        registry.add("spring.datasource.password", MySQLTestContainer.INSTANCE::getPassword);
        registry.add("spring.datasource.driver-class-name", MySQLTestContainer.INSTANCE::getDriverClassName);
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
}
