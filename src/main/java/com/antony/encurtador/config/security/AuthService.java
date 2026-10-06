package com.antony.encurtador.config.security;

import com.antony.encurtador.exceptions.NotFoundErrorException;
import com.antony.encurtador.user.IUserRepository;
import com.antony.encurtador.user.UserEntity;
import com.antony.encurtador.user.utils.RUserDto;
import com.antony.encurtador.user.utils.RUserResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final IUserRepository iUserRepository;

    public AuthService(AuthenticationManager authenticationManager, TokenService tokenService, IUserRepository iUserRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.iUserRepository = iUserRepository;
    }

    public RUserResponseDto login(RUserDto body) throws NotFoundErrorException {
        if (!iUserRepository.existsByEmail(body.email())){
            throw new NotFoundErrorException("Email");
        }

        var usernamePassword = new UsernamePasswordAuthenticationToken(body.email(), body.password());
        Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        var usuario = (UserEntity) auth.getPrincipal();

        return new RUserResponseDto(HttpStatus.OK, tokenService.generateToken(usuario.getUsername())) ;
    }

}
