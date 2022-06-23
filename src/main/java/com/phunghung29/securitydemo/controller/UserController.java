package com.phunghung29.securitydemo.controller;

import com.phunghung29.securitydemo.dto.*;
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

    @PostMapping("/changePass/{id}")
    public ResponseEntity<ResponeObject> changePassCustomer(@RequestBody ChangePassRequetDto changePassRequetDto, @PathVariable Long id){

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponeObject("200", CODE.UPDATE_SUCESSFFULY_PASS, Instant.now(),userService.changePass(changePassRequetDto, id))
        );
    }
    @PostMapping("/search")
    public ResponseEntity<ResponeObject> searchEmailUser(@RequestBody SearchEmaiRequestDto searchEmaiRequestDto)
    {
        List<UserDto> userDtoList = userService.searchEmail(searchEmaiRequestDto.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponeObject("200", CODE.UPDATE_SUCESSFFULY_PASS, Instant.now(),userDtoList)
        );
    }

//    @PostMapping("/searchUser")
//    public ResponseEntity<ResponeObject> searchUser(@RequestBody SearchUserRequestDto searchUserRequestDto)
//    {
//        List<UserDto> userDtoList = userService.searchUser(searchUserRequestDto);
//        return ResponseEntity.status(HttpStatus.OK).body(
//                new ResponeObject("200", CODE.UPDATE_SUCESSFFULY_PASS, Instant.now(),userDtoList)
//        );
//    }
    @PostMapping("/searchSpec")
    public ResponseEntity<ResponeObject> searchSpects(@RequestBody SearchUserRequestDto searchUserRequestDto)
    {
        List<UserDto> userDtoList = userService.findAllUserSearch(searchUserRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponeObject("200", CODE.UPDATE_SUCESSFFULY_PASS, Instant.now(),userDtoList)
        );
    }
    @PostMapping("/isActiveUser/{id}")
    public ResponseEntity<ResponeObject> updateActiveUser(@PathVariable("id") Long id)
    {
        UserDto userDto = userService.unActivatedUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponeObject("200", CODE.UPDATE_SUCESSFFULY_PASS, Instant.now(),userDto)
        );
    }

}
