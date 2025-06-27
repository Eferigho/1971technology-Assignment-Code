package com.agency;

import com.agency.data.dto.UserRequestDto;
import com.agency.data.dto.UserResponseDto;
import com.agency.data.entity.User;
import com.agency.repository.UserRepository;
import com.agency.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequestDto userRequestDto;
    private User user;

    @BeforeEach
    void setUp() {
        userRequestDto = new UserRequestDto();
        userRequestDto.setName("John Doe");
        userRequestDto.setEmail("john.doe@example.com");

        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
    }

    @Test
    void createUser_ShouldReturnUserResponseDto_WhenValidInput() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserResponseDto result = userService.createUser(userRequestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");

        verify(userRepository).findByEmail("john.doe@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailAlreadyExists() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // When & Then
        assertThatThrownBy(() -> userService.createUser(userRequestDto))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("User with email john.doe@example.com already exists");

        verify(userRepository).findByEmail("john.doe@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Given
        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Smith");
        user2.setEmail("jane.smith@example.com");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));

        // When
        List<UserResponseDto> result = userService.getAllUsers();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
        assertThat(result.get(1).getName()).isEqualTo("Jane Smith");

        verify(userRepository).findAll();
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        Optional<UserResponseDto> result = userService.getUserById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getName()).isEqualTo("John Doe");

        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_ShouldReturnEmpty_WhenUserNotExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<UserResponseDto> result = userService.getUserById(1L);

        // Then
        assertThat(result).isEmpty();

        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_ShouldThrowException_WhenIdIsNull() {
        // When & Then
        assertThatThrownBy(() -> userService.getUserById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User ID cannot be null");

        verify(userRepository, never()).findById(any());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenValidInput() {
        // Given
        UserRequestDto updateRequest = new UserRequestDto();
        updateRequest.setName("John Updated");
        updateRequest.setEmail("john.updated@example.com");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("John Updated");
        updatedUser.setEmail("john.updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("john.updated@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        Optional<UserResponseDto> result = userService.updateUser(1L, updateRequest);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("John Updated");
        assertThat(result.get().getEmail()).isEqualTo("john.updated@example.com");

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldReturnTrue_WhenUserExists() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = userService.deleteUser(1L);

        // Then
        assertThat(result).isTrue();

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldReturnFalse_WhenUserNotExists() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(false);

        // When
        boolean result = userService.deleteUser(1L);

        // Then
        assertThat(result).isFalse();

        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(any());
    }
}

