package com.phunghung29.securitydemo.controller;

import com.phunghung29.securitydemo.dto.*;
import com.phunghung29.securitydemo.exception.CODE;
import com.phunghung29.securitydemo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class GatewayController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ResponeObject> login(@RequestBody LoginRequestDto loginRequest) {
        try {
            LoginDto loginDto = userService.login(loginRequest);
//            return ResponseEntity.ok(loginDto);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponeObject("200", CODE.LOGIN_SUCESSFFULY, Instant.now(),loginDto)
            );
        } catch (RuntimeException e) {
            Map<String, String> err = new HashMap<>();
            err.put("message", e.getMessage());
            return ResponseEntity.status(401).contentType(MediaType.APPLICATION_JSON).body(new ResponeObject("401",CODE.LOGIN_FAIL,Instant.now(),""));

        }
    }
    @PostMapping("/register")
    public ResponseEntity<ResponeObject> register(@RequestBody RegisterRequestDto registerRequestDto)
    {
        RegisterDto registerDto = userService.registerUser(registerRequestDto);
//        return ResponseEntity.ok(registerDto);
//        return userService.register(registerRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponeObject("200", CODE.REGISTER_SUCESSFFULY, Instant.now(),registerDto)
        );
    }


}
