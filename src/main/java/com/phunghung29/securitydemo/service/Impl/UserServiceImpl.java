package com.phunghung29.securitydemo.service.Impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.phunghung29.securitydemo.dto.*;
import com.phunghung29.securitydemo.entity.Role;
import com.phunghung29.securitydemo.entity.User;
import com.phunghung29.securitydemo.exception.CODE;
import com.phunghung29.securitydemo.exception.NotFoundException;
import com.phunghung29.securitydemo.repository.RoleRepository;
import com.phunghung29.securitydemo.repository.UserRepository;
import com.phunghung29.securitydemo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto findById(Long id) {
        UserDto userDto = new UserDto();
        User user = userRepository.findById(id).orElseThrow(RuntimeException::new);
        BeanUtils.copyProperties(user, userDto);
        userDto.setRoleName(user.getRole().getRoleName());
        return userDto;
    }

    @Override
    public LoginDto login(LoginRequestDto loginRequestDto) throws RuntimeException {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();
        try {
            if (authenticate(email, password)) {
                UserDetails userDetails = userDetailService.loadUserByUsername(email);
                User detectedUser = userRepository.findByEmail(email);
                Map<String, Object> payload = new HashMap<>();
                payload.put("id", detectedUser.getId());
                payload.put("email", detectedUser.getEmail());
                payload.put("role", detectedUser.getRole().getRoleName());
                String token = generateToken(payload, new org.springframework.security.core.userdetails.User(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities()));
                return new LoginDto(token);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("LOGIN_FAILURE");
        }
    }

    @Override
    public ResponseEntity<ResponeObject> register(RegisterRequestDto registerRequestDto) {

        User foundUser = userRepository.findByEmail(registerRequestDto.getEmail().trim());
        if (foundUser != null) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponeObject("fail", "User ton tai", Instant.now(), "")
            );
        }
        Role role = roleRepository.findById(registerRequestDto.getRole_id()).orElse(null);
        if (role == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponeObject("fail", "User error", Instant.now(), "")
            );
        }
        User user = new User();
        user.setEmail(registerRequestDto.getEmail());
        String encodePassword = passwordEncoder.encode(registerRequestDto.getPassword());
        user.setPassword(encodePassword);
        user.setRole(role);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponeObject("ok", "User Successfully", Instant.now(), userRepository.save(user))
        );
    }

    @Override
    public RegisterDto registerUser(RegisterRequestDto registerRequestDto) {
        User foundUser = userRepository.findByEmail(registerRequestDto.getEmail().trim());
        if (foundUser != null) {
            throw new NotFoundException(CODE.EMAIL_EXIST, "Email exist");
        }

        Role role = roleRepository.findById(registerRequestDto.getRole_id()).orElse(null);
        if (role == null) {
            throw new NotFoundException(CODE.ROLE_NULL, "Role null");
        }
        User user = new User();
        user.setEmail(registerRequestDto.getEmail());
        String testPass = registerRequestDto.getPassword();
        if (testPass == null || testPass.isEmpty()) {
//            return new RegisterDto("Password không để trống","");
            throw new NotFoundException(CODE.PASS_NOT_NULL, "Password not null");
        }
        if (testPass.length() < 8 || testPass.length() > 16) {
//            return new RegisterDto("Password này không đáp ứng chiều dài","");

            throw new NotFoundException(CODE.PASS_NOT_LENGTH, "Password not length");
        }
        if (!testPass.matches(".*(?=.*[@#$%]).*")) {
//            return new RegisterDto("Password phải chứa các kí tự  @ # $ %","");
            throw new NotFoundException(CODE.PASS_KY_ITSELF, "The password must contain characters @ # $ %");
        }
        String testEmail = registerRequestDto.getEmail();
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        if (!Pattern.matches(regexPattern, testEmail)) {
//            return new RegisterDto("Định dạng email không đúng (vd: john@doe.com)","");
            throw new NotFoundException(CODE.EMAIL_NOT_FORMAT, "Email not format");
        }

        String encodePassword = passwordEncoder.encode(registerRequestDto.getPassword());
        user.setPassword(encodePassword);
        user.setAge(registerRequestDto.getAge());
        user.setGender(registerRequestDto.getGender());
        user.setIsActivated(registerRequestDto.getIsactivited());
        user.setRole(role);

        userRepository.save(user);
        return new RegisterDto("Thành công", user.getEmail());

    }

    @Override
    public List<UserDto> findAll() {
        List<User> userList = userRepository.findAll();
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userList) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(user, userDto);
            userDto.setRoleName(user.getRole().getRoleName());
            userDtoList.add(userDto);
        }
        return userDtoList;
    }

    @Override
    public UserDto updateUser(UserUpdataDto userUpdataDto, Long id) {
        AtomicReference<UserDto> userDto = new AtomicReference<>(new UserDto());
        userRepository.findById(id)
                .map(user -> {
                    Role role = roleRepository.findById(userUpdataDto.getRole_id()).orElse(null);
                    if (role == null) {
                        return new UserUpdataDto(id, "", 2L);
                    }
                    user.setRole(role);

                    userRepository.save(user);
                    userDto.set(new UserDto(id, user.getEmail(), user.getAge(), user.getGender(), user.getIsActivated(), user.getRole().getRoleName()));

                    return userDto.get();
                }).orElseGet(() -> {
                    User user1 = new User();
                    user1.setId(id);
                    user1.setEmail(userUpdataDto.getEmail());
                    userDto.set(new UserDto(id, user1.getEmail(), user1.getAge(), user1.getGender(), user1.getIsActivated(), user1.getRole().getRoleName()));
                    userRepository.save(user1);
                    return userDto.get();
                });
        return userDto.get();
    }

    @Override
    public ChangePassDto changePass(ChangePassRequetDto changePassRequetDto, Long id) {
        AtomicReference<ChangePassDto> changePassDto = new AtomicReference<>(new ChangePassDto());
        userRepository.findById(id)
                .map(user -> {
                    String email = changePassRequetDto.getEmail();
                    if (email == null || email.isEmpty()) {
                        throw new NotFoundException(CODE.EMAIL_NOT_NULL, "Email not null");
                    }
                    User getEmail = userRepository.findById(changePassRequetDto.getId()).orElse(null);
//                    User getEmail1= userRepository.findByEmail(email);
                    if (!getEmail.getEmail().equals(email)) {
                        throw new NotFoundException(CODE.EMAIL_INCORRECT, "Email incorrect");
                    }
//                    String getPass= changePassRequetDto.getPass_ord();
                    Boolean encodePasswordOld = passwordEncoder.matches(changePassRequetDto.getPass_ord(), getEmail.getPassword());

                    if (!encodePasswordOld) {
                        throw new NotFoundException(CODE.PASS_ERROR, "Password error");
                    }
                    String encodePassword = passwordEncoder.encode(changePassRequetDto.getPass_new());

                    user.setPassword(encodePassword);

                    userRepository.save(user);
                    changePassDto.set(new ChangePassDto(id, user.getEmail(), user.getRole().getRoleName()));

                    return changePassDto.get();
                }).orElseGet(() -> {
                    User user1 = new User();
                    user1.setId(id);
                    user1.setEmail(changePassRequetDto.getEmail());
                    changePassDto.set(new ChangePassDto(id, user1.getEmail(), user1.getRole().getRoleName()));
                    userRepository.save(user1);
                    return changePassDto.get();
                });
        return changePassDto.get();
    }

    @Override
    public List<UserDto> searchEmail(String searchEmaiRequestDto) {
        List<User> userList = userRepository.findBySearchEmail(searchEmaiRequestDto.toLowerCase());
        if (userList == null || userList.isEmpty()) {
            throw new NotFoundException(CODE.EMAIL_NOT_NULL, "EMAIL NULL");
        }
        List<UserDto> userDtoList = new ArrayList<>();
        userList.forEach(item -> {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(item, userDto);
            userDto.setRoleName(item.getRole().getRoleName());
            userDtoList.add(userDto);
        });
        return userDtoList;
    }


    public boolean authenticate(String email, String password) throws Exception {
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            SecurityContextHolder.getContext().setAuthentication(auth);
            return true;
        } catch (DisabledException e) {
            throw new DisabledException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("INCORRECT_EMAIL_OR_PASSWORD", e);
        }
    }

    public static String generateToken(Map<String, Object> payload, org.springframework.security.core.userdetails.User user) {
        Properties prop = loadProperties("jwt.setting.properties");
        assert prop != null;
        String key = prop.getProperty("key");
        String accessExpired = prop.getProperty("access_expired");
        assert key != null;
        assert accessExpired != null;
        long expiredIn = Long.parseLong(accessExpired);
        Algorithm algorithm = Algorithm.HMAC256(key);

        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiredIn))
                .withClaim("user", payload)
                .sign(algorithm);
    }

    public static Properties loadProperties(String fileName) {
        try (InputStream input = User.class.getClassLoader().getResourceAsStream(fileName)) {

            Properties prop = new Properties();

            if (input == null) {
                throw new IOException();
            }
            prop.load(input);
            return prop;

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
