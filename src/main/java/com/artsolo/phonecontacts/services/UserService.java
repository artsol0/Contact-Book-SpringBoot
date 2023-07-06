package com.artsolo.phonecontacts.services;

import com.artsolo.phonecontacts.dto.UserDto;
import com.artsolo.phonecontacts.models.User;
import com.artsolo.phonecontacts.repositoryes.RoleRepository;
import com.artsolo.phonecontacts.repositoryes.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean createUser(UserDto userDto) {
        if(userRepository.findByUsername(userDto.getUsername()) != null) {
            return false;
        }
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRoles(Arrays.asList(roleRepository.findByTitle("USER")));
        userRepository.save(user);
        return true;
    }

}
