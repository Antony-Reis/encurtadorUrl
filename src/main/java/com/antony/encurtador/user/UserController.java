package com.antony.encurtador.user;

import com.antony.encurtador.config.security.AuthService;
import com.antony.encurtador.exceptions.AuthErrorException;
import com.antony.encurtador.exceptions.ConflictErrorException;
import com.antony.encurtador.exceptions.NotFoundErrorException;
import com.antony.encurtador.user.utils.RUserDto;
import com.antony.encurtador.user.utils.RUserResponseDto;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@Validated
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public RUserResponseDto registerUser(@RequestBody @Valid RUserDto body) throws ConflictErrorException {
        return userService.registerUser(body);
    }

    @PostMapping("/login")
    public RUserResponseDto loginUser(@RequestBody @Valid RUserDto body) throws NotFoundErrorException {
        return authService.login(body);
    }

    @PatchMapping
    public RUserResponseDto patchUSer(@RequestBody @Valid RUserDto body) throws AuthErrorException, NotFoundErrorException {
        return userService.patchPasswordUser(body);
    }


}
