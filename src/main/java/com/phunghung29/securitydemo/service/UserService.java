package com.phunghung29.securitydemo.service;

import com.phunghung29.securitydemo.dto.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    UserDto findById(Long id);

    LoginDto login(LoginRequestDto loginRequestDto) throws RuntimeException;

    ResponseEntity<ResponeObject> register(RegisterRequestDto registerRequestDto);

    RegisterDto registerUser(RegisterRequestDto registerRequestDto);

    List<UserDto> findAll();

    UserDto updateUser(UserUpdataDto userUpdataDto, Long id);
    ChangePassDto changePass(ChangePassRequetDto changePassRequetDto, Long id);



//    ResponseEntity<ResponeObject> updateUser1(UserUpdataDto userUpdataDto, Long id);


}
