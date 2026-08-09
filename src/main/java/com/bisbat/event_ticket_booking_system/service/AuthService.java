package com.bisbat.event_ticket_booking_system.service;

import com.bisbat.event_ticket_booking_system.dto.auth.AuthResponse;
import com.bisbat.event_ticket_booking_system.dto.auth.LoginRequest;
import com.bisbat.event_ticket_booking_system.dto.auth.RegisterRequest;
import com.bisbat.event_ticket_booking_system.models.Role;
import com.bisbat.event_ticket_booking_system.models.User;
import com.bisbat.event_ticket_booking_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // 1. ระบบสมัครสมาชิก
    public AuthResponse register(RegisterRequest request) {
        // สร้าง Entity User ใหม่ (ถ้าใช้ @Builder ในคลาส User ก็ใช้ Builder ได้เลย)
        User user = new User();
        user.setFname(request.getFname());
        user.setLname(request.getLname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // เข้ารหัสผ่านก่อนเซฟ!
        user.setRole(Role.USER); // กำหนด Role พื้นฐาน

        // เซฟลง Database
        repository.save(user);

        // ออก Token ให้เลยหลังสมัครเสร็จ
        var jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken,
                "Bearer",
                user.getEmail(),
                user.getRole().name()
        );
    }

    // 2. ระบบล็อกอิน
    public AuthResponse authenticate(LoginRequest request) {
        // ให้ AuthenticationManager เช็ค Email กับ Password
        // (ถ้าพาสเวิร์ดผิด มันจะโยน Exception ออกมาเองอัตโนมัติ)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // ถ้าผ่านบรรทัดบนมาได้ แปลว่ารหัสถูกชัวร์ๆ ก็ไปดึง User จาก DB มา
        var user = repository.findByEmail(request.getEmail()).orElseThrow();

        // สั่งออก Token ใหม่
        var jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken,
                "Bearer",
                user.getEmail(),
                user.getRole().name()
        );
    }
}
