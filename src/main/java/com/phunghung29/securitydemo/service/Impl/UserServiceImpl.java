package com.phunghung29.securitydemo.service.Impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.phunghung29.securitydemo.dto.*;
import com.phunghung29.securitydemo.entity.Role;
import com.phunghung29.securitydemo.entity.User;
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
        User user= userRepository.findById(id).orElseThrow(RuntimeException::new);
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

        User foundUser= userRepository.findByEmail(registerRequestDto.getEmail().trim());
        if(foundUser != null){
            return  ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponeObject("fail","User ton tai", "")
            );
        }
        Role role =roleRepository.findById(registerRequestDto.getRole_id()).orElse(null);
        if (role ==null)
        {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponeObject("fail","User error", "")
            );
        }
        User user= new User();
        user.setEmail(registerRequestDto.getEmail());
        String encodePassword= passwordEncoder.encode(registerRequestDto.getPassword());
        user.setPassword(encodePassword);
        user.setRole(role);
        return  ResponseEntity.status(HttpStatus.OK).body(
                new ResponeObject("ok","User Successfully", userRepository.save(user))
        );
    }

    @Override
    public RegisterDto registerUser(RegisterRequestDto registerRequestDto) {
        User foundUser= userRepository.findByEmail(registerRequestDto.getEmail().trim());
        if(foundUser != null)
        {
            return new RegisterDto("Email t???n  t???i", "") ;
        }
        Role role =roleRepository.findById(registerRequestDto.getRole_id()).orElse(null);
        if (role ==null)
        {
            return new RegisterDto("Role kh??ng t???n t???i", "") ;
        }

        User user= new User();

        user.setEmail(registerRequestDto.getEmail());

        String testPass= registerRequestDto.getPassword();
        if(testPass== null || testPass.isEmpty())
        {
            return new RegisterDto("Password kh??ng ????? tr???ng","");
        }
        if(testPass.length() < 8 || testPass.length() > 16)
        {
            return new RegisterDto("Password n??y kh??ng ????p ???ng chi???u d??i","");
        }
        if(!testPass.matches(".*(?=.*[@#$%]).*"))
        {
            return new RegisterDto("Password ph???i ch???a c??c k?? t???  @ # $ %","");
        }
        String testEmail= registerRequestDto.getEmail();
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        if(!Pattern.matches(regexPattern, testEmail))
        {
            return new RegisterDto("?????nh d???ng email kh??ng ????ng (vd: john@doe.com)","");
        }
//        else {
//            if (testPass.length()>8 && testPass.matches("/^[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]*$/"))
//            {
//                return new RegisterDto("Password ph???i d??i h??n 8 v?? ph???i c?? k?? t??? ?????c bi???t","");
//            }
//        }
        String encodePassword= passwordEncoder.encode(registerRequestDto.getPassword());
        user.setPassword(encodePassword);
        user.setRole(role);
        userRepository.save(user);
        return new RegisterDto("Th??nh c??ng",user.getEmail());

    }

    @Override
    public List<UserDto> findAll() {
        List<User> userList= userRepository.findAll();
        List<UserDto> userDtoList= new ArrayList<>();
        for (User user: userList)
        {
            UserDto userDto= new UserDto();
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
                .map(user->{
                    Role role= roleRepository.findById(userUpdataDto.getRole_id()).orElse(null);
                    if(role==null)
                    {
                        return new UserUpdataDto(id,"", 2L);
                    }
                    user.setRole(role);

                    userRepository.save(user);
                     userDto.set(new UserDto(id, user.getEmail(), user.getRole().getRoleName()));

                    return userDto.get();
                }).orElseGet(()->{
                    User user1= new User();
                    user1.setId(id);
                    user1.setEmail(userUpdataDto.getEmail());
                     userDto.set(new UserDto(id, user1.getEmail(), user1.getRole().getRoleName()));
                     userRepository.save(user1);
                     return userDto.get();
                });
        return userDto.get();
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
