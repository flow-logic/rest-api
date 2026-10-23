package com.example.rest_api.security;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class SecurityConfig {

//    @Bean
//    public InMemoryUserDetailsManager userDetailsManager() {
//        UserDetails juan = User.builder()
//                .username("juan")
//                .password("{noop}123")
//                .build();
//        return new InMemoryUserDetailsManager(juan);
//    }
    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer
                -> configurer
                        .requestMatchers(HttpMethod.GET, "/api/students").hasRole("PROFESOR")
                        .requestMatchers(HttpMethod.GET, "/api/students/**").hasRole("PROFESOR")
                        .requestMatchers(HttpMethod.POST, "/api/students").hasRole("PROFESOR")
                        .requestMatchers(HttpMethod.PUT, "/api/students").hasRole("DIRECTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/students/**").hasRole("DIRECTOR")
                .anyRequest().permitAll()
        );
        // use HTTP Basic authentication
        http.httpBasic(Customizer.withDefaults());
// disable Cross Site Request Forgery (CSRF)
// in general, not required for stateless REST APIs that use POST, PUT, DELETE and/or PATCH
        http.csrf(csrf -> csrf.disable());
        return http.build();
    }
    
@Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permitir el origen de tu front-end o '*'
        configuration.setAllowedOrigins(List.of("http://localhost", "http://127.0.0.1", "http://localhost:5500")); // O List.of("*") si prefieres abierto
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        CorsFilter bean = new CorsFilter(source);
        FilterRegistrationBean<CorsFilter> beanRegistration = new FilterRegistrationBean<>(bean);
        // Le damos la máxima prioridad para que se ejecute antes que Spring Security
        beanRegistration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return beanRegistration;
    }
}
