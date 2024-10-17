package ru.gorohov.eventmanager.secured.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.gorohov.eventmanager.secured.jwt.JwtManager;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtManager jwtManager;
    private static final String HEADER_NAME = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        var header = request.getHeader(HEADER_NAME);

        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        var jwtToken = header.substring(7);

        if (!jwtManager.isTokenValid(jwtToken)) {
            logger.info("Token is invalid");
            filterChain.doFilter(request, response);
            return;
        }
        var username = jwtManager.getLoginFromToken(jwtToken);
        var role = jwtManager.getRoleFromToken(jwtToken);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(username, null,
                        List.of(new SimpleGrantedAuthority(role)));

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        filterChain.doFilter(request, response);

    }
}
