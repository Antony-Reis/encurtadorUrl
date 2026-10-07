
package com.antony.encurtador.user;

import com.antony.encurtador.exceptions.AuthErrorException;
import com.antony.encurtador.exceptions.ConflictErrorException;
import com.antony.encurtador.exceptions.NotFoundErrorException;
import com.antony.encurtador.user.utils.RUserDto;
import com.antony.encurtador.user.utils.RUserResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private IUserRepository iUserRepository;

    @Spy
    BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    class registerUser {

        @Test
        @DisplayName("Should register a user with success")
        void shouldRegisterAUser() {

            // Arrange
            doReturn(false)
                    .when(iUserRepository)
                    .existsByEmail(any());

            RUserDto input = new RUserDto(
                    "email@email.com",
                    "password"
            );

            RUserResponseDto responseExpected =
                    new RUserResponseDto(
                            HttpStatus.CREATED,
                            "User registado"
                    );

            // Act
            var output = userService.registerUser(input);

            // Assert
            assertEquals(responseExpected, output);

            verify(iUserRepository).save(any(UserEntity.class));
        }

        @Test
        @DisplayName("Should throw ConflictErrorException when email already exists")
        void shouldThrowConflictErrorExceptionWhenEmailAlreadyExists() {

            // Arrange
            doReturn(true)
                    .when(iUserRepository)
                    .existsByEmail(any());

            RUserDto input = new RUserDto(
                    "email@email.com",
                    "password"
            );

            // Act
            ConflictErrorException exceptionThrown = assertThrows(
                    ConflictErrorException.class,
                    () -> userService.registerUser(input)
            );

            // Assert
            assertEquals(
                    "Erro de conflito do elemento:Email",
                    exceptionThrown.getMessage()
            );

            verify(iUserRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should encrypt the password before saving the user")
        void shouldEncryptPasswordBeforeSavingUser() {

            // Arrange
            doReturn(false)
                    .when(iUserRepository)
                    .existsByEmail(any());

            RUserDto input = new RUserDto(
                    "email@email.com",
                    "password"
            );

            ArgumentCaptor<UserEntity> userCaptor =
                    ArgumentCaptor.forClass(UserEntity.class);

            // Act
            userService.registerUser(input);

            // Assert
            verify(iUserRepository).save(userCaptor.capture());

            UserEntity savedUser = userCaptor.getValue();

            assertEquals("email@email.com", savedUser.getEmail());

            assertNotNull(savedUser.getSenha());

            assertTrue(
                    new BCryptPasswordEncoder()
                            .matches("password", savedUser.getSenha())
            );

            assertTrue(
                    !savedUser.getSenha().equals("password")
            );
        }
    }

    @Nested
    class getAuthenticatedUser {

        @Test
        @DisplayName("Should return the authenticated user")
        void shouldReturnAuthenticatedUser() {

            // Arrange
            UserEntity user = new UserEntity(
                    "email@email.com",
                    "password"
            );

            Authentication authentication =
                    mock(Authentication.class);

            doReturn(user)
                    .when(authentication)
                    .getPrincipal();

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            // Act
            UserEntity output = userService.getAuthenticatedUser();

            // Assert
            assertSame(user, output);
        }
    }

    @Nested
    class patchPasswordUser {

        @Test
        @DisplayName("Should patch password user with success")
        void shouldPatchPasswordUserWithSuccess() {

            // Arrange
            UserEntity user = new UserEntity(
                    "email@email.com",
                    "oldPassword"
            );

            Authentication authentication =
                    mock(Authentication.class);

            doReturn(user)
                    .when(authentication)
                    .getPrincipal();

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            doReturn(true)
                    .when(iUserRepository)
                    .existsByEmail("email@email.com");

            doReturn(user)
                    .when(iUserRepository)
                    .save(any(UserEntity.class));

            RUserDto input = new RUserDto(
                    "email@email.com",
                    "newPassword"
            );

            RUserResponseDto responseExpected =
                    new RUserResponseDto(
                            HttpStatus.OK,
                            "User atualizado com sucesso"
                    );

            // Act
            var output = userService.patchPasswordUser(input);

            // Assert
            assertEquals(responseExpected, output);

            verify(iUserRepository).save(user);

            assertTrue(
                    new BCryptPasswordEncoder()
                            .matches("newPassword", user.getSenha())
            );

            assertTrue(
                    !user.getSenha().equals("newPassword")
            );
        }

        @Test
        @DisplayName("Should throw NotFoundErrorException when email does not exist")
        void shouldThrowNotFoundErrorExceptionWhenEmailDoesNotExist() {

            // Arrange
            doReturn(false)
                    .when(iUserRepository)
                    .existsByEmail("email@email.com");

            RUserDto input = new RUserDto(
                    "email@email.com",
                    "newPassword"
            );

            // Act
            NotFoundErrorException exceptionThrown = assertThrows(
                    NotFoundErrorException.class,
                    () -> userService.patchPasswordUser(input)
            );

            // Assert
            assertNotNull(exceptionThrown);

            verify(iUserRepository, never())
                    .save(any());
        }

        @Test
        @DisplayName("Should throw AuthErrorException when authenticated user is different")
        void shouldThrowAuthErrorExceptionWhenAuthenticatedUserIsDifferent() {

            // Arrange
            UserEntity authenticatedUser = new UserEntity(
                    "authenticated@email.com",
                    "password"
            );

            Authentication authentication =
                    mock(Authentication.class);

            doReturn(authenticatedUser)
                    .when(authentication)
                    .getPrincipal();

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            doReturn(true)
                    .when(iUserRepository)
                    .existsByEmail("another@email.com");

            RUserDto input = new RUserDto(
                    "another@email.com",
                    "newPassword"
            );

            // Act
            AuthErrorException exceptionThrown = assertThrows(
                    AuthErrorException.class,
                    () -> userService.patchPasswordUser(input)
            );

            // Assert
            assertNotNull(exceptionThrown);

            verify(iUserRepository, never())
                    .save(any());
        }

        @Test
        @DisplayName("Should update user without changing password when password is null")
        void shouldUpdateUserWithoutChangingPasswordWhenPasswordIsNull() {

            // Arrange
            UserEntity user = new UserEntity(
                    "email@email.com",
                    "oldPassword"
            );

            String oldPassword = user.getSenha();

            Authentication authentication =
                    mock(Authentication.class);

            doReturn(user)
                    .when(authentication)
                    .getPrincipal();

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            doReturn(true)
                    .when(iUserRepository)
                    .existsByEmail("email@email.com");

            doReturn(user)
                    .when(iUserRepository)
                    .save(any(UserEntity.class));

            RUserDto input = new RUserDto(
                    "email@email.com",
                    null
            );

            RUserResponseDto responseExpected =
                    new RUserResponseDto(
                            HttpStatus.OK,
                            "User atualizado com sucesso"
                    );

            // Act
            var output = userService.patchPasswordUser(input);

            // Assert
            assertEquals(responseExpected, output);

            assertEquals(oldPassword, user.getSenha());

            verify(iUserRepository).save(user);
        }
    }

    @Nested
    class loadUserByUsername {

        @Test
        @DisplayName("Should load user by email with success")
        void shouldLoadUserByEmailWithSuccess() {

            // Arrange
            UserEntity user = new UserEntity(
                    "email@email.com",
                    "password"
            );

            doReturn(Optional.of(user))
                    .when(iUserRepository)
                    .findByEmail("email@email.com");

            // Act
            UserDetails output =
                    userService.loadUserByUsername("email@email.com");

            // Assert
            assertSame(user, output);

            verify(iUserRepository)
                    .findByEmail("email@email.com");
        }

        @Test
        @DisplayName("Should throw UsernameNotFoundException when user does not exist")
        void shouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {

            // Arrange
            doReturn(Optional.empty())
                    .when(iUserRepository)
                    .findByEmail("email@email.com");

            // Act
            UsernameNotFoundException exceptionThrown = assertThrows(
                    UsernameNotFoundException.class,
                    () -> userService.loadUserByUsername("email@email.com")
            );

            // Assert
            assertEquals(
                    "Utilizador não encontrado",
                    exceptionThrown.getMessage()
            );

            verify(iUserRepository)
                    .findByEmail("email@email.com");
        }
    }
}