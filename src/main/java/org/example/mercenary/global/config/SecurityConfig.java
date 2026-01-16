package org.example.mercenary.global.config;

import lombok.RequiredArgsConstructor;
import org.example.mercenary.global.auth.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 비활성화 (JWT 사용 시 필수)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. CORS 설정 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 3. 세션 미사용 (Stateless 설정)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 로그인, 회원가입 관련 API는 누구나 접근 가능
                        .requestMatchers("/api/auth/**", "/login/**").permitAll()

                        // (선택 사항) 매치 목록 조회(GET)도 로그인 없이 보여주고 싶다면 아래 줄 주석 해제
                        .requestMatchers(HttpMethod.GET, "/api/matches/**").permitAll()

                        // 나머지 모든 요청은 인증(토큰) 필요
                        .anyRequest().authenticated()
                )

                // 🔥 JWT 필터를 "ID/PW 인증 필터"보다 앞에서 실행되도록 등록

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS 설정: 프론트엔드(5173) 허용
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*")); // 프론트에서 헤더를 다 볼 수 있게 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}