package com.imenbank.imenbank.service;

import com.imenbank.imenbank.dto.AuthRequest;
import com.imenbank.imenbank.dto.AuthResponse;
import com.imenbank.imenbank.dto.RegisterRequest;

public interface AuthService {
    
    AuthResponse register(RegisterRequest request);
    
    AuthResponse authenticate(AuthRequest request);
    
    AuthResponse refreshToken(String refreshToken);
} 