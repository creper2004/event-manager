package ru.gorohov.eventmanager.secured.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.gorohov.eventmanager.secured.handlers.CustomAccessDeniedHandler;
import ru.gorohov.eventmanager.secured.handlers.CustomEntryPoint;
import ru.gorohov.eventmanager.secured.jwt.JwtManager;
import ru.gorohov.eventmanager.secured.service.UserService;

@EnableWebSecurity
@Configuration
public class SecurityConfig {


    private final UserService userService;

    private final JwtManager jwtManager;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CustomEntryPoint customEntryPoint;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;


    @Autowired
    public SecurityConfig(UserService userService, JwtManager jwtManager, JwtAuthenticationFilter jwtAuthenticationFilter, CustomEntryPoint customEntryPoint, CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.userService = userService;
        this.jwtManager = jwtManager;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customEntryPoint = customEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return daoAuthenticationProvider;
    }

    //


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.debug(true).ignoring()
                .requestMatchers("/css/**",
                        "/js/**",
                        "/img/**",
                        "/lib/**",
                        "/favicon.ico",
                        "/swagger-ui/**",
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/v3/api-docs/swagger-config",
                        "/event-manager-openapi.yaml"
                );
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(ses -> ses.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(customEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                                .requestMatchers(HttpMethod.POST,  "/users/auth").permitAll()
                                .requestMatchers(HttpMethod.GET, "/users/{id}").hasAuthority("ADMIN")

                                .requestMatchers(HttpMethod.GET,  "/locations").hasAnyAuthority("USER", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/locations").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/locations/{id}").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/locations/{id}").hasAnyAuthority("USER", "ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/location/{id}").hasAuthority("ADMIN")

                                .requestMatchers(HttpMethod.POST, "/events").hasAuthority("USER")
                                .requestMatchers(HttpMethod.DELETE, "/events/{id}").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.GET, "/events/{id}").hasAnyAuthority("USER", "ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/events/{id}").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.POST, "/events/search").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.POST,  "/events/registrations/{eventId}").hasAuthority("USER")

                                .anyRequest().authenticated()

                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();


    }

}
