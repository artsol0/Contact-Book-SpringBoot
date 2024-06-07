package com.artsolo.phonecontacts.auth;

import com.artsolo.phonecontacts.user.User;
import com.artsolo.phonecontacts.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public String authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() ->
                    new UsernameNotFoundException("User with username `" + request.getUsername() + "` is not found"));

            return jwtService.generateToken(user);

        } catch (AuthenticationException e) {
            return null;
        }
    }

    public String register(RegisterRequest request) {
        User user = getUserFromRegisterRequest(request);
        if (!isUsernameTaken(user.getUsername())) {
            userRepository.save(user);
            return jwtService.generateToken(user);
        }
        return null;
    }

    public User getUserFromRegisterRequest(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return user;
    }

    public boolean isUsernameTaken(String username) {return userRepository.findByUsername(username).isPresent();}
}
