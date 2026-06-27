
package com.example.ms_producto.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RequestBodyCachingFilterTest {

    private final RequestBodyCachingFilter filter =
            new RequestBodyCachingFilter();

    @Test
    void debeConservarRequestCuandoYaEstaEnvuelto() throws Exception {
        ContentCachingRequestWrapper request =
                new ContentCachingRequestWrapper(
                        new MockHttpServletRequest()
                );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    void debeEnvolverRequestCuandoTodaviaNoEstaEnvuelto()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain chain = mock(FilterChain.class);

        ArgumentCaptor<ServletRequest> captor =
                ArgumentCaptor.forClass(ServletRequest.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(
                captor.capture(),
                same(response)
        );

        assertThat(captor.getValue())
                .isInstanceOf(ContentCachingRequestWrapper.class);
    }
}

