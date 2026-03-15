package io.linfeng.config.swagger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Base64;

/**
 * Swagger基础认证过滤器
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class SwaggerBasicAuthFilter extends OncePerRequestFilter {

    private static final List<String> SWAGGER_PATH_PATTERNS = Arrays.asList(
            "/doc.html",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/v2/api-docs/**",
            "/webjars/**"
    );

    private final SwaggerProperties swaggerProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public SwaggerBasicAuthFilter(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!needsProtect(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isAuthorized(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        unauthorized(response);
    }

    private boolean needsProtect(HttpServletRequest request) {
        SwaggerProperties.Basic basic = swaggerProperties.getBasic();
        if (basic == null || !basic.isEnable()) {
            return false;
        }

        String uri = request.getRequestURI();
        return SWAGGER_PATH_PATTERNS.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    private boolean isAuthorized(HttpServletRequest request) {
        SwaggerProperties.Basic basic = swaggerProperties.getBasic();
        if (basic == null) {
            return false;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return false;
        }

        String token = authHeader.substring(6);
        byte[] decoded = Base64.getDecoder().decode(token);
        String credentials = new String(decoded, StandardCharsets.UTF_8);
        String[] parts = credentials.split(":", 2);
        if (parts.length != 2) {
            return false;
        }

        return Objects.equals(basic.getUsername(), parts[0]) && Objects.equals(basic.getPassword(), parts[1]);
    }

    private void unauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Swagger UI\"");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"msg\":\"Unauthorized\"}");
    }
}

