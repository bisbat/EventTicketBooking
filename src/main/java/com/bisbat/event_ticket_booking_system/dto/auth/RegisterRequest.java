package com.bisbat.event_ticket_booking_system.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Firstname is required")
    String fname;
    @NotBlank(message = "Lastname is required")
    String lname;
    @NotBlank(message = "Email is required")
    @Email(message = "email must be valid")
    String email;
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "password must be at least 8 characters")
    String password;

}
