package com.artsolo.phonecontacts.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user1 = User.builder().username("user214").build();
        User user2 = User.builder().username("user453").build();
        userRepository.saveAll(Arrays.asList(user1, user2));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void findUser1ByUsername() {
        Optional<User> foundedUser = userRepository.findByUsername("user214");
        assertThat(foundedUser.isPresent()).isTrue();
        assertThat(foundedUser.get().getUsername()).isEqualTo("user214");
    }

    @Test
    void findUser2ByUsername() {
        Optional<User> foundedUser = userRepository.findByUsername("user453");
        assertThat(foundedUser.isPresent()).isTrue();
        assertThat(foundedUser.get().getUsername()).isEqualTo("user453");
    }
}