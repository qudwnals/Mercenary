package org.example.mercenary.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 비활성화 (JWT를 사용하므로 세션 방식의 보안이 필요 없음)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. CORS 설정 (프론트엔드 포트 5173 허용)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 3. 세션을 사용하지 않음 (Stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. URL별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/matches/nearby", "/api/auth/**").permitAll() // 검색과 로그인은 모두 허용
                        .anyRequest().authenticated() // 나머지는 로그인이 필요함
                );

        return http.build();
    }

    // CORS 설정: 프론트엔드와 백엔드 간의 통신 허용
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:5173")); // 리액트 주소
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}