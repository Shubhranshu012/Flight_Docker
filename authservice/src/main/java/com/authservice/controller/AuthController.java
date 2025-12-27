package com.authservice.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import com.authservice.dto.UpdatePasswordRequest;
import com.authservice.model.EROLE;
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
		if(user.getRole()== EROLE.ADMIN) {
			throw new RuntimeException("Admin Register Not Allowed");
		}
		Optional<User> users=userRepository.findByEmail(user.getEmail());
		if(!users.isEmpty()) {
			throw new RuntimeException("Email Already Exists");
		}
	    user.setPassword(passwordEncoder.encode(user.getPassword()));
	    LocalDateTime newDate = LocalDateTime.now();
        user.setLastDate(newDate);
	    userRepository.save(user);

	    return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
    @PostMapping("/login")
    public Map<String,String> login(@RequestBody User request) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("Email Not Found"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid Credentials");
        }
        String Token=jwtService.generateToken(user.getEmail(), user.getRole());
        Map<String,String> message=new HashMap<String,String> ();
        message.put("token", Token);
        message.put("role", user.getRole().name());
        message.put("lastDate", user.getLastDate() != null ? user.getLastDate().toString() : "");
        return message;
    }
    
    @PostMapping("/change")
    public Map<String, String> updatePassword(@RequestBody UpdatePasswordRequest request) {
    		
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("Email Not Found"));
        System.out.println(request);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        LocalDateTime newDate = LocalDateTime.now();
        user.setLastDate(newDate);
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password updated successfully");

        return response;
    }

    
    
    
}
