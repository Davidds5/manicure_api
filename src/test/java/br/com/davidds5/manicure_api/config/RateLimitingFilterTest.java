package br.com.davidds5.manicure_api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitingFilterTest {

    private RateLimitingFilter rateLimitingFilter;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        rateLimitingFilter = new RateLimitingFilter();
    }

    @Test
    @DisplayName("Deve permitir requisições normais dentro do limite de auth")
    void shouldAllowRequestsWithinLimit() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/login");
        request.setRemoteAddr("192.168.1.50");
        MockHttpServletResponse response = new MockHttpServletResponse();

        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        assertEquals(200, response.getStatus());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve bloquear e retornar 429 quando limite de auth for excedido")
    void shouldBlockWhenLimitExceeded() throws ServletException, IOException {
        String clientIp = "192.168.1.100";

        // Faz 15 requisições permitidas
        for (int i = 0; i < 15; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/login");
            request.setRemoteAddr(clientIp);
            MockHttpServletResponse response = new MockHttpServletResponse();
            rateLimitingFilter.doFilterInternal(request, response, filterChain);
            assertEquals(200, response.getStatus());
        }

        // A 16ª deve ser bloqueada com 429
        MockHttpServletRequest blockedRequest = new MockHttpServletRequest("POST", "/login");
        blockedRequest.setRemoteAddr(clientIp);
        MockHttpServletResponse blockedResponse = new MockHttpServletResponse();

        rateLimitingFilter.doFilterInternal(blockedRequest, blockedResponse, filterChain);

        assertEquals(429, blockedResponse.getStatus());
        assertTrue(blockedResponse.getContentAsString().contains("Too Many Requests"));
        assertEquals("60", blockedResponse.getHeader("Retry-After"));
    }

    @Test
    @DisplayName("Deve extrair corretamente o IP do cliente via X-Forwarded-For atrás de proxy reverso")
    void shouldExtractRealClientIpFromXForwardedForProxyHeader() throws ServletException, IOException {
        String realClientIp = "201.55.12.34";
        String proxyIp = "10.0.0.1";

        // Simula requisições vindas através do proxy do Render com header X-Forwarded-For
        for (int i = 0; i < 15; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/login");
            request.setRemoteAddr(proxyIp);
            request.addHeader("X-Forwarded-For", realClientIp + ", " + proxyIp);
            MockHttpServletResponse response = new MockHttpServletResponse();
            rateLimitingFilter.doFilterInternal(request, response, filterChain);
            assertEquals(200, response.getStatus());
        }

        // A 16ª requisição do mesmo cliente real deve sofrer rate limit mesmo que o remoteAddr seja o proxy
        MockHttpServletRequest blockedRequest = new MockHttpServletRequest("POST", "/login");
        blockedRequest.setRemoteAddr(proxyIp);
        blockedRequest.addHeader("X-Forwarded-For", realClientIp + ", " + proxyIp);
        MockHttpServletResponse blockedResponse = new MockHttpServletResponse();

        rateLimitingFilter.doFilterInternal(blockedRequest, blockedResponse, filterChain);

        assertEquals(429, blockedResponse.getStatus());
    }

    @Test
    @DisplayName("Endpoints isentos (swagger/health) não sofrem rate limit")
    void shouldBypassExemptEndpoints() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/health");
        request.setRemoteAddr("10.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, atLeastOnce()).doFilter(request, response);
    }
}
