package com.artsolo.phonecontacts;

import com.artsolo.phonecontacts.controllers.UserController;
import com.artsolo.phonecontacts.dto.UserDto;
import com.artsolo.phonecontacts.models.UserPrincipal;
import com.artsolo.phonecontacts.services.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {
    @InjectMocks
    private UserController userController;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserService userService;

    @Test
    public void testLogin_AuthenticationException_ReturnsUnauthorized() {
        UserDto userDto = new UserDto("testuser", "invalidpassword");

        AuthenticationException authenticationException = new BadCredentialsException("Invalid credentials");

        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(authenticationException);

        ResponseEntity<String> response = userController.login(userDto);

        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testRegister_SuccessfulRegistration_ReturnsSuccessMessage() {
        UserDto userDto = new UserDto("testuser", "password");

        Mockito.when(userService.createUser(userDto)).thenReturn(true);

        ResponseEntity<String> response = userController.register(userDto);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals("User registered successfully", response.getBody());
    }

    @Test
    public void testRegister_UsernameTaken_ReturnsBadRequest() {
        UserDto userDto = new UserDto("admin", "password");

        Mockito.when(userService.createUser(userDto)).thenReturn(false);

        ResponseEntity<String> response = userController.register(userDto);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertEquals("Username is already taken!", response.getBody());
    }

}
