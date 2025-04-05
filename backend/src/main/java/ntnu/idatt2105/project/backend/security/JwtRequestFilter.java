package ntnu.idatt2105.project.backend.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ntnu.idatt2105.project.backend.service.UserService;


/*
 * This class is responsible for filtering incoming requests to check for a valid JWT token.
 * It extends OncePerRequestFilter to ensure that the filter is executed once per request.
 * The doFilterInternal method checks the Authorization header for a Bearer token,
 * extracts the username from the token, and sets the authentication in the SecurityContext.
 * If the token is valid, it creates a UsernamePasswordAuthenticationToken and sets it in the SecurityContext.
 * The filter is applied to all incoming requests to secure the application.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userDetailsService;

    /*
     * This method is called for every incoming request.
     * It checks the Authorization header for a Bearer token,
     * extracts the username from the token, and sets the authentication in the SecurityContext.
     * If the token is valid, it creates a UsernamePasswordAuthenticationToken and sets it in the SecurityContext.
     * 
     * @param request The incoming HTTP request.
     * @param response The HTTP response.
     * @param chain The filter chain to continue processing the request.
     * @throws ServletException If an error occurs during the filter processing.
     * @throws IOException If an I/O error occurs during the filter processing.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
                
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }
}
