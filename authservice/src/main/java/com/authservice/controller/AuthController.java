package com.authservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.authservice.model.User;
import com.authservice.repository.UserRepository;
import com.authservice.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	@Autowired
	UserRepository userRepository;
	@Autowired
    PasswordEncoder passwordEncoder;
	@Autowired
    JwtService jwtService;

	@PostMapping("/register")
	public ResponseEntity<Void> register(@RequestBody User user) {
	    user.setPassword(passwordEncoder.encode(user.getPassword()));
	    userRepository.save(user);

	    return ResponseEntity.status(HttpStatus.CREATED).build();
	}
    @PostMapping("/login")
    public Map<String,String> login(@RequestBody User request) {

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid Credentials");
        }
        String Token=jwtService.generateToken(user.getUsername(), user.getRole());
        Map<String,String> message=new HashMap<String,String> ();
        message.put("token", Token);
        message.put("role", user.getRole().name());
        return message;
    }
}
