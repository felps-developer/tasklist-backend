
package br.com.jtech.tasklist.service;

import br.com.jtech.tasklist.dto.AuthRequest;
import br.com.jtech.tasklist.dto.AuthResponse;
import br.com.jtech.tasklist.dto.RegisterRequest;
import br.com.jtech.tasklist.entity.UserEntity;
import br.com.jtech.tasklist.repository.UserRepository;
import br.com.jtech.tasklist.config.infra.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
* class AuthServiceTest 
* 
* @author jtech
*/
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private br.com.jtech.tasklist.service.impl.AuthServiceImpl authService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = UserEntity.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .build();
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .name("New User")
                .email("new@example.com")
                .password("password123")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        // When
        UserEntity result = authService.register(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).existsByEmail(request.getEmail());
        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .name("New User")
                .email("existing@example.com")
                .password("password123")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email já está em uso");

        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void shouldLoginSuccessfully() {
        // Given
        AuthRequest request = AuthRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(request.getEmail())).thenReturn("accessToken");
        when(jwtTokenProvider.generateRefreshToken(request.getEmail())).thenReturn("refreshToken");

        // When
        AuthResponse result = authService.login(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("accessToken");
        assertThat(result.getRefreshToken()).isEqualTo("refreshToken");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        verify(userRepository).findByEmail(request.getEmail());
        verify(passwordEncoder).matches(request.getPassword(), user.getPassword());
        verify(jwtTokenProvider).generateToken(request.getEmail());
        verify(jwtTokenProvider).generateRefreshToken(request.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        AuthRequest request = AuthRequest.builder()
                .email("notfound@example.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class)
                .hasMessage("Credenciais inválidas");

        verify(userRepository).findByEmail(request.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {
        // Given
        AuthRequest request = AuthRequest.builder()
                .email("test@example.com")
                .password("wrongPassword")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class)
                .hasMessage("Credenciais inválidas");

        verify(userRepository).findByEmail(request.getEmail());
        verify(passwordEncoder).matches(request.getPassword(), user.getPassword());
        verify(jwtTokenProvider, never()).generateToken(anyString());
    }
}

