package com.antony.encurtador.user;

import com.antony.encurtador.exceptions.ConflictErrorException;
import com.antony.encurtador.user.utils.RUserDto;
import com.antony.encurtador.user.utils.RUserResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private IUserRepository iUserRepository;

    @Spy
    BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Nested
    class registerUser {
        @Test
        @DisplayName(" Should register a user with success")
        void shouldRegisterAUser() {
            //Arrange
            doReturn(null).when(iUserRepository).save(any());
            doReturn(false).when(iUserRepository).existsByEmail(any());

            RUserDto input = new RUserDto("email@email.com",
                    "password");

            RUserResponseDto responseExpectd = new RUserResponseDto(HttpStatus.CREATED,
                    "User registado");
            //Act
            var output = userService.registerUser(input);
            //Assert
            assertEquals(responseExpectd, output);
        }
        @Test
        @DisplayName("Should throw ConflictErrorException when email already exists")
        void shouldThrowConflictErrorExceptionWhenEmailAlreadExits(){
            //Arrange
            doReturn(true).when(iUserRepository).existsByEmail(any());
            RUserDto input = new RUserDto("email@email.com",
                    "password");
            //Act
            ConflictErrorException exceptionThrown = assertThrows(
                    ConflictErrorException.class,
                    () -> userService.registerUser(input)
            );
            //Assert
            assertEquals("Erro de conflito do elemento:Email", exceptionThrown.getMessage());
        }
    }
}