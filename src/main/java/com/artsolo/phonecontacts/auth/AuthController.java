package com.artsolo.phonecontacts.auth;

import com.artsolo.phonecontacts.responses.DataResponse;
import com.artsolo.phonecontacts.responses.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        String token = authService.register(request);

        if (!token.isEmpty()) {
            return ResponseEntity.ok().body(new DataResponse<>(true, HttpStatus.OK.value(), token));
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse(
                false,
                HttpStatus.CONTINUE.value(),
                "Username is already taken")
        );
    }

    @GetMapping("/login")
    public ResponseEntity<?> auth(@RequestBody @Valid AuthenticationRequest request) {
        String token = authService.authenticate(request);

        if (!token.isEmpty()) {
            return ResponseEntity.ok().body(new DataResponse<>(true, HttpStatus.OK.value(), token));
        }

        return ResponseEntity.badRequest().body(new MessageResponse(
                false,
                HttpStatus.BAD_REQUEST.value(),
                "Invalid data credentials")
        );
    }
}
