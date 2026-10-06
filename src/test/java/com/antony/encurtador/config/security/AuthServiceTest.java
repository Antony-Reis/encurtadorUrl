package com.antony.encurtador.config.security;

import com.antony.encurtador.exceptions.NotFoundErrorException;
import com.antony.encurtador.user.IUserRepository;
import com.antony.encurtador.user.UserEntity;
import com.antony.encurtador.user.utils.RUserDto;
import com.antony.encurtador.user.utils.RUserResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private IUserRepository iUserRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Nested
    class login {
        @Test
        @DisplayName("Should login user with sucess")
        void ShouldloginWithSucess(){
            //Arrange
            final String token = "token gerado";
            final RUserDto input = new RUserDto("email@email.com", "password");
            final UserEntity user = new UserEntity("email@email.com", "password");
            final RUserResponseDto responseExpected = new RUserResponseDto(HttpStatus.OK, token);

            doReturn(true).when(iUserRepository).existsByEmail(any());
            Authentication auth =  mock(Authentication.class);

            doReturn(auth)
                    .when(authenticationManager)
                    .authenticate(any());

            doReturn(user).when(auth).getPrincipal();

            doReturn(token)
                    .when(tokenService)
                    .generateToken(user.getUsername());

            //Act
            RUserResponseDto response = authService.login(input);
            //Assert
            assertNotNull(response);
            assertEquals(responseExpected, response);
        }
        @Test
        @DisplayName("Should Throw Email NotFoundException")
        void ShouldThrowEmailNotFoundException(){
            //Arrange
            final RUserDto input = new RUserDto("email@email.com", "password");
            final String exceptionExpected = new NotFoundErrorException("Email").getMessage();
            doReturn(false).when(iUserRepository).existsByEmail(any());
            //Act
            NotFoundErrorException exception = assertThrows(NotFoundErrorException.class
            , () -> authService.login(input));
            //Assert
            assertEquals(exceptionExpected, exception.getMessage());
        }
    }
}