package com.bisbat.event_ticket_booking_system.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 1. ปิด CSRF (เขียนย่อแบบ Method Reference คลีนขึ้น)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. ตั้งค่าสิทธิ์การเข้าถึง API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // สมัคร/ล็อกอิน เข้าได้เลย
                        .anyRequest().authenticated()                // นอกนั้นต้องมี Token
                )

                // 3. ตั้งค่าเป็น Stateless (ไม่จำ Session)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // เพิ่มบล็อกนี้เข้าไป เพื่อดัก Error ว่าถ้ายังไม่ได้ Login ให้ตอบ 401
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "ยังไม่ได้เข้าสู่ระบบ (Token ขาดหายหรือไม่ถูกต้อง)")
                        )
                )

                // 4. เอา Filter ของเราไปวางดักไว้
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
