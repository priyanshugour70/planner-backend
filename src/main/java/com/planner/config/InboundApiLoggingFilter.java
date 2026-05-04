package com.planner.config;

import com.planner.security.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Logs every /api/** request and response status so you can see mobile traffic in the backend console.
 */
@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class InboundApiLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (!request.getRequestURI().startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long ms = System.currentTimeMillis() - start;
            String user = resolveUserLabel();
            String q = request.getQueryString();
            String path = q != null ? request.getRequestURI() + "?" + q : request.getRequestURI();
            log.info("HTTP {} {} → {} {} ({} ms)", request.getMethod(), path, response.getStatus(), user, ms);
        }
    }

    private static String resolveUserLabel() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "[anon]";
        }
        if (auth.getPrincipal() instanceof UserPrincipal up) {
            return "[userId=" + up.getId() + "]";
        }
        return "[auth=" + auth.getClass().getSimpleName() + "]";
    }
}
