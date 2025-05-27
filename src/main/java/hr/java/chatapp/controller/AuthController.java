package hr.java.chatapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import hr.java.chatapp.model.RefreshToken;
import hr.java.chatapp.model.UserInfo;
import hr.java.chatapp.model.dto.UserInfoDTO;
import hr.java.chatapp.model.enums.EnumRole;
import hr.java.chatapp.model.payload.request.LoginRequest;
import hr.java.chatapp.model.payload.request.RefreshTokenRequest;
import hr.java.chatapp.model.payload.request.RegistrationRequest;
import hr.java.chatapp.model.payload.response.AuthResponse;
import hr.java.chatapp.model.payload.response.JwtResponse;
import hr.java.chatapp.repository.UserInfoRepository;
import hr.java.chatapp.security.GoogleAuthService;
import hr.java.chatapp.security.JwtService;
import hr.java.chatapp.security.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private GoogleAuthService googleAuthService;
    @Autowired
    RefreshTokenService refreshTokenService;
    @Autowired
    UserInfoRepository userInfoRepository;
    @Autowired
    JwtService jwtService;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegistrationRequest registrationRequest
    ) {
        if (userInfoRepository.existsByUsername(registrationRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        if (userInfoRepository.existsByEmail(registrationRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
        Set<String> basicRoleSet = new HashSet<>();
        basicRoleSet.add(EnumRole.ROLE_USER.name());
        UserInfo user = UserInfo.builder()
                .email(registrationRequest.getEmail())
                .username(registrationRequest.getUsername())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .roles(basicRoleSet)
                .build();
        userInfoRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        Optional<UserInfo> user = userInfoRepository.findByUsername(loginRequest.getUsername());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Invalid user request!");
        }

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()));

        if (authentication.isAuthenticated()) {
            UserInfoDTO userDTO = UserInfoDTO.builder()
                    .id(user.get().getId())
                    .username(user.get().getUsername())
                    .email(user.get().getEmail())
                    .imageFileId(user.get().getImageFileId())
                    .isOnline(true)
                    .lastOnline(user.get().getLastOnline())
                    .contactIds(user.get().getContactIds())
                    .build();
            JwtResponse jwtResponse = JwtResponse.builder()
                    .accessToken(jwtService.generateToken(userDTO.getEmail()))
                    .refreshToken(refreshTokenService.createRefreshToken(user.get().getEmail()).getToken())
                    .build();
            AuthResponse authResponse = AuthResponse.builder()
                    .jwtResponse(jwtResponse)
                    .userInfo(userDTO)
                    .build();
            return ResponseEntity.ok().body(authResponse);
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(
            @RequestBody Map<String, String> request
    ) {
        String idToken = request.get("idToken");
        GoogleIdToken.Payload payload = googleAuthService.verifyToken(idToken);
        if (payload == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID refreshToken");
        }

        String userEmail = payload.getEmail();
        String userName = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");

        Set<String> basicRoleSet = new HashSet<>();
        basicRoleSet.add(EnumRole.ROLE_USER.name());

        Optional<UserInfo> existingUser = userInfoRepository.findByEmail(userEmail);

        UserInfo user;
        if (existingUser.isPresent()) {
            UserInfo oldUser = UserInfo.builder()
                    .id(existingUser.get().getId())
                    .username(existingUser.get().getUsername())
                    .email(existingUser.get().getEmail())
                    .password(existingUser.get().getPassword())
                    .roles(existingUser.get().getRoles())
                    .imageFileId(pictureUrl)
                    .build();
            user = userInfoRepository.save(oldUser);
        } else {
            UserInfo newUser = UserInfo.builder()
                    .username(userName)
                    .email(userEmail)
                    .password("")
                    .roles(basicRoleSet)
                    .imageFileId(pictureUrl)
                    .build();
            user = userInfoRepository.save(newUser);
        }

        JwtResponse jwtResponse = JwtResponse.builder()
                .accessToken(jwtService.generateToken(userEmail))
                .refreshToken(refreshTokenService.createRefreshToken(userEmail).getToken())
                .build();
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .imageFileId(user.getImageFileId())
                .build();

        return ResponseEntity.ok().body(
                AuthResponse.builder()
                        .jwtResponse(jwtResponse)
                        .userInfo(userInfoDTO)
                        .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestBody String refreshToken
    ) {
        Optional<RefreshToken> deletedRefreshToken = refreshTokenService.deleteRefreshToken(refreshToken);
        if (deletedRefreshToken.isPresent()) {
            return ResponseEntity.ok().body("You've been signed out!");
        } else
            // Either way log user out
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refreshToken");
    }

    @PostMapping("/refreshToken")
    public JwtResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return refreshTokenService.findByToken(refreshTokenRequest.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(refreshToken -> userInfoRepository.findById(refreshToken.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found!")))
                .map(userInfo -> {
                    String accessToken = jwtService.generateToken(userInfo.getEmail());
                    return JwtResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshTokenRequest.getToken())
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh Token is not in database!"));
    }

}
