package com.bisbat.event_ticket_booking_system.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // --- ส่วนแกะข้อมูล (Extract) และถอดรหัส ---
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 1. ดึง Email (Subject) ออกมาจาก Token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims) // ใส่ข้อมูลเสริมเข้าไปได้
                .subject(userDetails.getUsername()) // ใส่ Email
                .issuedAt(new Date(System.currentTimeMillis())) // เวลาที่สร้าง
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // หมดอายุใน 24 ชั่วโมง
                .signWith(getSignInKey()) // เซ็นรับรองด้วยกุญแจลับ
                .compact();
    }

    // 2. สร้าง Token ใหม่ (รับข้อมูล User เข้ามา แล้วคืนค่าเป็น String ยาวๆ)
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // เช็คว่า Email ตรงกับ User ไหม และ Token หมดอายุหรือยัง
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
