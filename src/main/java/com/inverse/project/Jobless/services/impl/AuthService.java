//package com.inverse.project.Jobless.services.impl;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.inverse.project.Jobless.Token.Token;
//import com.inverse.project.Jobless.Token.TokenRepository;
//import com.inverse.project.Jobless.Token.TokenType;
//import com.inverse.project.Jobless.exceptions.ResourceNotFoundException;
//import com.inverse.project.Jobless.models.Admin;
//import com.inverse.project.Jobless.repositories.AdminRepository;
//import com.inverse.project.Jobless.security.AuthenticationRequest;
//import com.inverse.project.Jobless.security.AuthenticationResponse;
//import com.inverse.project.Jobless.security.RegisterRequest;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//
//    private final AdminRepository adminRepository;
//    private final PasswordEncoder passwordEncoders;
//    private final TokenRepository tokenRepository;
//    private final JwtService jwtService;
//    private final AuthenticationManager authenticationManager;
//    public AuthenticationResponse register(RegisterRequest request){
//
//        var admin = Admin.builder()
//                .name(request.getName())
//                .email(request.getEmail())
//                .password(passwordEncoders.encode(request.getPassword()))
//                .role("ADMIN")
//                .build();
//
//        adminRepository.save(admin);
//
//        var jwtToken = jwtService.generateToken(admin);
//        var refreshTokne = jwtService.generateRefreshToken(admin);
//        revokeAllUserTokens(admin);
//        saveUserToken(admin, jwtToken);
//        return AuthenticationResponse.builder()
//                .accessToken(jwtToken)
//                .refreshToken(refreshTokne)
//                .build();
//    }
//
//    private void revokeAllUserTokens(Admin admin) {
//        var validUserTokens = tokenRepository.findAllValidTokenByUser(admin.getId());
//        if(validUserTokens.isEmpty()) return;
//        validUserTokens.forEach(token -> {
//            token.setExpired(true);
//            token.setRevoked(true);
//        });
//
//        tokenRepository.saveAll(validUserTokens);
//    }
//
//    private void saveUserToken(Admin admin, String jwtToken) {
//        var token = Token.builder()
//                .admin(admin)
//                .token(jwtToken)
//                .tokenType(TokenType.BEARER)
//                .expired(false)
//                .revoked(false)
//                .build();
//        tokenRepository.save(token);
//    }
//
//    public AuthenticationResponse authenticate(AuthenticationRequest request){
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getEmail(),
//                        request.getPassword()
//                )
//        );
//
//        var admin = adminRepository.findByEmail(request.getEmail())
//                .orElseThrow();
//        var jwtToken = jwtService.generateToken(admin);
//        var refreshTokne = jwtService.generateRefreshToken(admin);
//        return AuthenticationResponse.builder()
//                .accessToken(jwtToken)
//                .refreshToken(refreshTokne)
//                .build();
//    }
//
//    public void refreshToken(
//            HttpServletRequest request,
//            HttpServletResponse response) throws IOException {
//        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//        final String refreshToken;
//        final String userEmail;
//        if(authHeader == null || !authHeader.startsWith("Bearer ")){
//            return;
//        }
//        refreshToken = authHeader.substring(7);
//        userEmail = jwtService.extractUsername(refreshToken); // extract user email from refresh token
//        if(userEmail != null){
//            var user = this.adminRepository.findByEmail(userEmail)
//                    .orElseThrow(
//                            () -> new ResourceNotFoundException("email not found")
//                    );
//            if(jwtService.isTokenValid(refreshToken, user)){
//               var accessToken = jwtService.generateToken(user);
//                revokeAllUserTokens(user);
//                saveUserToken(user, accessToken);
//               var authResponse = AuthenticationResponse.builder()
//                               .accessToken(accessToken)
//                       .refreshToken(refreshToken)
//                       .build();
//               new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
//            }
//        }
//    }
//}
