package com.example.ms_producto.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;

class FeignAuthConfigTest {

    private final RequestInterceptor interceptor =
            new FeignAuthConfig().authorizationHeaderForwarder();

    @AfterEach
    void limpiarContexto() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void noDebeAgregarAuthorizationCuandoNoExisteRequestActivo() {
        RequestContextHolder.resetRequestAttributes();
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertThat(template.headers())
                .doesNotContainKey(HttpHeaders.AUTHORIZATION);
    }

    @Test
    void noDebeAgregarAuthorizationCuandoNoExisteHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(request)
        );

        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertThat(template.headers())
                .doesNotContainKey(HttpHeaders.AUTHORIZATION);
    }

    @Test
    void noDebeAgregarAuthorizationCuandoElHeaderEstaVacio() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "   ");

        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(request)
        );

        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertThat(template.headers())
                .doesNotContainKey(HttpHeaders.AUTHORIZATION);
    }

    @Test
    void debeReenviarAuthorizationCuandoExisteBearerToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Bearer token-prueba"
        );

        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(request)
        );

        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertThat(template.headers())
                .containsKey(HttpHeaders.AUTHORIZATION);

        assertThat(template.headers().get(HttpHeaders.AUTHORIZATION))
                .containsExactly("Bearer token-prueba");
    }
}
