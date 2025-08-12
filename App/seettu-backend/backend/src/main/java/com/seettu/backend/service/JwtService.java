package com.seettu.backend.service;

import com.seettu.backend.entity.User;
import com.seettu.backend.Config.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtUtils jwtUtils;

    public String createToken(User user) {
        return jwtUtils.generateToken((UserDetails) user);
    }

    public String extractUsername(String token) {
        return jwtUtils.extractUsername(token);
    }

    public boolean validateToken(String token, User user) {
        return jwtUtils.validateToken(token, (UserDetails) user);
    }
}
