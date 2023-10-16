package com.mzo.wasl.controller;

import com.mzo.wasl.model.*;
import com.mzo.wasl.payload.request.LoginRequest;
import com.mzo.wasl.payload.request.SignupRequest;
import com.mzo.wasl.payload.response.JwtResponse;
import com.mzo.wasl.payload.response.MessageResponse;
import com.mzo.wasl.repository.ProfileRepository;
import com.mzo.wasl.repository.RoleRepository;
import com.mzo.wasl.repository.UserRepository;
import com.mzo.wasl.security.jwt.JwtUtils;
import com.mzo.wasl.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles.toString()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));
        String strRole = signUpRequest.getRole();

        if (strRole == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_REGULAR)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            user.setRole(userRole);
        } else {
                switch (strRole) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        user.setRole(adminRole);

                        break;
                    case "support":
                        Role supportRole = roleRepository.findByName(ERole.ROLE_SUPPORT)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        user.setRole(supportRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_REGULAR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        user.setRole(userRole);
                }

        }
        Profile profile = new Profile();
        userRepository.save(user);
        profileRepository.save(profile);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
