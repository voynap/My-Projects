package voynap.InterviewTask.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import voynap.InterviewTask.security.JWTUtil;
import voynap.InterviewTask.services.CustomersDetailsService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@Component
public class JWTFilter extends OncePerRequestFilter {


    private final JWTUtil jwtUtil;

    private final CustomersDetailsService customersDetailsService;
    @Autowired
    public JWTFilter(JWTUtil jwtUtil, CustomersDetailsService customersDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customersDetailsService = customersDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && !header.isBlank() && header.startsWith("Bearer ")) {
            String jwt = header.substring(7);
            if (!jwt.isBlank()) {
                String username = jwtUtil.validateTokenAndRetrieveClaim(jwt);
                String role = jwtUtil.extractRoleFromToken(jwt);
                UserDetails userDetails = customersDetailsService.loadUserByUsername(username);

                Collection<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(role));
                System.out.println("username " + username);
                System.out.println("role " + role);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), authorities);

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }


}
