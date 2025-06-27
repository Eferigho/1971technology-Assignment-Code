package com.agency;

import com.agency.data.dto.UserRequestDto;
import com.agency.data.dto.UserResponseDto;
import com.agency.data.entity.User;
import com.agency.repository.UserRepository;
import com.agency.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UserRequestDto userRequestDto;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        userRequestDto = new UserRequestDto();
        userRequestDto.setName("Integration Test User");
        userRequestDto.setEmail("integration@test.com");
    }

    @Test
    void createUser_ShouldPersistUserInDatabase() {
        // When
        UserResponseDto result = userService.createUser(userRequestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Integration Test User");
        assertThat(result.getEmail()).isEqualTo("integration@test.com");

        // Verify in database
        Optional<User> savedUser = userRepository.findById(result.getId());
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getName()).isEqualTo("Integration Test User");
    }

    @Test
    void createUser_ShouldThrowException_WhenDuplicateEmail() {
        // Given
        userService.createUser(userRequestDto);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(userRequestDto))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void getAllUsers_ShouldReturnAllPersistedUsers() {
        // Given
        UserRequestDto user1 = new UserRequestDto();
        user1.setName("User 1");
        user1.setEmail("user1@test.com");

        UserRequestDto user2 = new UserRequestDto();
        user2.setName("User 2");
        user2.setEmail("user2@test.com");

        userService.createUser(user1);
        userService.createUser(user2);

        // When
        List<UserResponseDto> result = userService.getAllUsers();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(UserResponseDto::getName)
                .containsExactlyInAnyOrder("User 1", "User 2");
    }

    @Test
    void updateUser_ShouldPersistChangesInDatabase() {
        // Given
        UserResponseDto createdUser = userService.createUser(userRequestDto);

        UserRequestDto updateRequest = new UserRequestDto();
        updateRequest.setName("Updated Name");
        updateRequest.setEmail("updated@test.com");

        // When
        Optional<UserResponseDto> result = userService.updateUser(createdUser.getId(), updateRequest);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Updated Name");
        assertThat(result.get().getEmail()).isEqualTo("updated@test.com");

        // Verify in database
        Optional<User> updatedUser = userRepository.findById(createdUser.getId());
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.get().getEmail()).isEqualTo("updated@test.com");
    }

    @Test
    void deleteUser_ShouldRemoveFromDatabase() {
        // Given
        UserResponseDto createdUser = userService.createUser(userRequestDto);

        // When
        boolean result = userService.deleteUser(createdUser.getId());

        // Then
        assertThat(result).isTrue();

        // Verify removal from database
        Optional<User> deletedUser = userRepository.findById(createdUser.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    void fullUserLifecycle_ShouldWorkEndToEnd() {
        // Create
        UserResponseDto created = userService.createUser(userRequestDto);
        assertThat(created.getId()).isNotNull();

        // Read
        Optional<UserResponseDto> found = userService.getUserById(created.getId());
        assertThat(found).isPresent();

        // Update
        UserRequestDto updateRequest = new UserRequestDto();
        updateRequest.setName("Updated Integration User");
        updateRequest.setEmail("updated.integration@test.com");

        Optional<UserResponseDto> updated = userService.updateUser(created.getId(), updateRequest);
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("Updated Integration User");

        // Delete
        boolean deleted = userService.deleteUser(created.getId());
        assertThat(deleted).isTrue();

        // Verify deletion
        Optional<UserResponseDto> notFound = userService.getUserById(created.getId());
        assertThat(notFound).isEmpty();
    }
}

