package voynap.InterviewTask.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import voynap.InterviewTask.services.CustomersDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomersDetailsService customersDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    private final JWTFilter jwtFilter;


    @Autowired
    public SecurityConfig(CustomersDetailsService customersDetailsService, PasswordEncoder passwordEncoder, ObjectMapper objectMapper, JWTFilter jwtFilter) {
        this.customersDetailsService = customersDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
        this.jwtFilter = jwtFilter;
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customersDetailsService).passwordEncoder(passwordEncoder);
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JWTFilter jwtFilter) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests(authorize -> authorize
                        .requestMatchers(req -> HttpMethod.POST.matches(req.getMethod()) && "/auth/registration".equals(req.getRequestURI())).permitAll()
                        .requestMatchers(req -> HttpMethod.POST.matches(req.getMethod()) && "/auth/login".equals(req.getRequestURI())).permitAll()
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout.permitAll())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}




