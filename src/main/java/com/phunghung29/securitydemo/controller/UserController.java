package com.phunghung29.securitydemo.controller;

import com.phunghung29.securitydemo.dto.ChangePassRequetDto;
import com.phunghung29.securitydemo.dto.ResponeObject;
import com.phunghung29.securitydemo.dto.UserDto;
import com.phunghung29.securitydemo.dto.UserUpdataDto;
import com.phunghung29.securitydemo.exception.CODE;
import com.phunghung29.securitydemo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllUser(){
        List<UserDto> userDtoList= userService.findAll();
        return ResponseEntity.ok(userDtoList);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") String id) {
        UserDto userDto = userService.findById(Long.parseLong(id));
        return ResponseEntity.ok(userDto);
    }
    @PutMapping("/{id}")
    public  ResponseEntity<ResponeObject> updateUser(@RequestBody UserUpdataDto newUser, @PathVariable Long id)
    {
        return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponeObject("200", CODE.UPDATE_SUCESSFFULY, Instant.now(),userService.updateUser(newUser, id))
        );
    }

    @PostMapping("/changepass/{id}")
    public ResponseEntity<ResponeObject> changePassCustomer(@RequestBody ChangePassRequetDto changePassRequetDto, @PathVariable Long id){

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponeObject("200", CODE.UPDATE_SUCESSFFULY, Instant.now(),userService.changePass(changePassRequetDto, id))
        );
    }




}
