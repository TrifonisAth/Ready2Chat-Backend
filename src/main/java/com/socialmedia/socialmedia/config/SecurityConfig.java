package com.socialmedia.socialmedia.config;

import com.socialmedia.socialmedia.filter.JwtAuthenticationFilter;
import com.socialmedia.socialmedia.interceptor.QueryParamHandshakeInterceptor;
import com.socialmedia.socialmedia.services.impl.JWTService;
import com.socialmedia.socialmedia.services.impl.MyUserDetailsService;
import com.socialmedia.socialmedia.websocket.SocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableWebSocket
public class SecurityConfig implements WebSocketConfigurer, WebMvcConfigurer {
    private final MyUserDetailsService userDetailsService;
    private final JWTService jwtService;

    public SecurityConfig(MyUserDetailsService userDetailsService, JWTService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http

                // Use bean corsConfigurationSource.
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, userDetailsService), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                        "/api/login",
                        "/api/register",
                        "/api/forgotPassword",
                                "/api/reconnect",
                                "/socket"
                        )
                        .permitAll()
                        .requestMatchers("/api/verify", "/api/resend").hasRole("TEMP")
                        .requestMatchers(
                                "/api/logout",
                                "/api/friendRequest/create",
                                "/api/friendRequest/delete",
                                "/api/friendRequest/accept",
                                "/api/friend/delete",
                                "/api/message/store"
                        ).hasRole("VERIFIED"))
                .userDetailsService(userDetailsService)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
        config.setAllowedMethods(Arrays.asList("GET", "POST"));
        config.setAllowedHeaders(Arrays.asList(
                "Content-Type",
                "Authorization",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Headers",
                "Origin",
                "Accept",
                "X-Requested-With",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
//                WebSocket headers. Maybe needed for CORS?
                "Connection",
                "Host",
                "Sec-WebSocket-Version",
                "Sec-WebSocket-Key",
                "Upgrade",
                "Sec-WebSocket-Extensions"
                ));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SocketHandler(),"/socket")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new QueryParamHandshakeInterceptor(jwtService));

    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
