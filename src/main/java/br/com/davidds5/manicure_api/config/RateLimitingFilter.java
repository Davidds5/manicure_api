package br.com.davidds5.manicure_api.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int AUTH_LIMIT_PER_MINUTE = 15;
    private static final int PUBLIC_SIGNUP_LIMIT_PER_MINUTE = 15;
    private static final int GENERAL_LIMIT_PER_MINUTE = 120;
    private static final long ONE_MINUTE_MILLIS = 60_000L;

    private final Cache<String, ConcurrentLinkedQueue<Long>> requestCounts = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

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
        boolean isAuthEndpoint = path.contains("/login") || path.contains("/auth");
        boolean isPublicSignup = (path.contains("/tenants/signup") && "POST".equalsIgnoreCase(method))
                || (path.contains("/clients") && path.contains("/public") && "POST".equalsIgnoreCase(method));

        int maxRequests;
        String bucketType;
        if (isAuthEndpoint) {
            maxRequests = AUTH_LIMIT_PER_MINUTE;
            bucketType = "AUTH";
        } else if (isPublicSignup) {
            maxRequests = PUBLIC_SIGNUP_LIMIT_PER_MINUTE;
            bucketType = "SIGNUP";
        } else {
            maxRequests = GENERAL_LIMIT_PER_MINUTE;
            bucketType = "GEN";
        }

        String key = clientIp + ":" + bucketType;
        long now = System.currentTimeMillis();

        ConcurrentLinkedQueue<Long> timestamps = requestCounts.get(key, k -> new ConcurrentLinkedQueue<>());

        synchronized (timestamps) {
            // Limpa timestamps mais velhos que 1 minuto
            while (!timestamps.isEmpty() && now - timestamps.peek() > ONE_MINUTE_MILLIS) {
                timestamps.poll();
            }

            if (timestamps.size() >= maxRequests) {
                log.warn("Rate limit excedido para o IP [{}] na rota [{}] (tipo: {}, limite: {}/min)", clientIp, path, bucketType, maxRequests);
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
        }

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
        String remote = request.getRemoteAddr();
        if (remote != null && !remote.isBlank()) {
            return remote;
        }
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isBlank()) {
            String[] parts = xfHeader.split(",");
            return parts[parts.length - 1].trim();
        }
        return "127.0.0.1";
    }

    public long getCacheEstimatedSize() {
        return requestCounts.estimatedSize();
    }
}
