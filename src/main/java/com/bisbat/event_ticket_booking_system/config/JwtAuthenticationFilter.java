package com.bisbat.event_ticket_booking_system.config;

import com.bisbat.event_ticket_booking_system.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. ดึงค่าจาก Header ที่ชื่อ "Authorization"
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. เช็คว่ามี Header ไหม และขึ้นต้นด้วย "Bearer " หรือเปล่า?
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // ถ้าไม่มี (เช่น เป็นการยิง API หน้า Login) ก็ปล่อยผ่านให้ด่านต่อไปจัดการ
            filterChain.doFilter(request, response);
            return;
        }

        // 3. ตัดคำว่า "Bearer " ออก (7 ตัวอักษร) เพื่อเอาเฉพาะตัวอักษร Token
        jwt = authHeader.substring(7);

        // 4. เอา Token ไปเข้าเครื่องสแกน เพื่อดึง Email (Username) ออกมา
        userEmail = jwtService.extractUsername(jwt);

        // 5. เช็คว่ามี Email อยู่ใน Token และ ในระบบยังไม่มีการลงทะเบียน Login เข้ามา (ป้องกันการทำซ้ำ)
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // ดึงข้อมูล User เต็มๆ จาก Database (ผ่าน Email)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 6. ตรวจสอบว่า Token ถูกต้องและยังไม่หมดอายุ
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // สร้าง "บัตรผ่านชั่วคราว" แจ้งว่าคนนี้ผ่านการตรวจสอบแล้ว
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // รหัสผ่านไม่ต้องใช้แล้วเพราะเรายืนยันตัวด้วย Token ไปแล้ว
                        userDetails.getAuthorities() // ใส่สิทธิ์ (Role)
                );

                // แนบรายละเอียดเพิ่มเติม (เช่น IP Address, Session)
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 7. เอาบัตรผ่านไปเสียบไว้ใน Security Context
                // (จากจุดนี้ไป Spring จะถือว่า Request นี้ทำการ Login สำเร็จแล้ว!)
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 8. ส่งไม้ต่อให้การทำงานอื่นๆ ของ Spring ทำงานต่อไป
        filterChain.doFilter(request, response);
    }
}
