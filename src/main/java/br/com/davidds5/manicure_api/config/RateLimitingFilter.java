package br.com.davidds5.manicure_api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int AUTH_LIMIT_PER_MINUTE = 15;
    private static final int GENERAL_LIMIT_PER_MINUTE = 120;
    private static final long ONE_MINUTE_MILLIS = 60_000L;

    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // Endpoints estáticos, swagger, health check ignoram rate limit
        if (isExempt(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);
        boolean isAuthEndpoint = path.contains("/login") || path.contains("/auth") || (path.contains("/tenants/signup") && "POST".equalsIgnoreCase(method));
        
        int maxRequests = isAuthEndpoint ? AUTH_LIMIT_PER_MINUTE : GENERAL_LIMIT_PER_MINUTE;
        String key = clientIp + ":" + (isAuthEndpoint ? "AUTH" : "GEN");

        long now = System.currentTimeMillis();
        ConcurrentLinkedQueue<Long> timestamps = requestCounts.computeIfAbsent(key, k -> new ConcurrentLinkedQueue<>());

        // Limpa timestamps mais velhos que 1 minuto
        while (!timestamps.isEmpty() && now - timestamps.peek() > ONE_MINUTE_MILLIS) {
            timestamps.poll();
        }

        if (timestamps.size() >= maxRequests) {
            log.warn("Rate limit excedido para o IP [{}] na rota [{}]", clientIp, path);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Retry-After", "60");
            response.getWriter().write("""
                {
                    "status": 429,
                    "error": "Too Many Requests",
                    "message": "Limite de requisições excedido. Aguarde um momento antes de tentar novamente."
                }
            """);
            return;
        }

        timestamps.add(now);
        filterChain.doFilter(request, response);
    }

    private boolean isExempt(String path) {
        return path.startsWith("/swagger-ui") 
                || path.startsWith("/v3/api-docs") 
                || path.startsWith("/actuator")
                || path.equals("/health")
                || path.equals("/index.html")
                || path.equals("/");
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isBlank()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
