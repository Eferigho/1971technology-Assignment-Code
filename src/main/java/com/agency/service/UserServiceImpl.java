package com.agency.service;

import com.agency.data.dto.UserRequestDto;
import com.agency.data.dto.UserResponseDto;
import com.agency.data.entity.User;
import com.agency.repository.UserRepository;
import com.agency.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponseDto createUser( UserRequestDto userRequestDto) {
        try {
            log.info("Creating user with email: {}", userRequestDto.getEmail());
            
            // Check if user with email already exists
            if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
                throw new DataIntegrityViolationException("User with email " + userRequestDto.getEmail() + " already exists");
            }
            
            User user = mapToEntity(userRequestDto);
            User savedUser = userRepository.save(user);
            
            log.info("User created successfully with ID: {}", savedUser.getId());
            return mapToResponseDto(savedUser);
            
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while creating user: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while creating user: {}", e.getMessage());
            throw new RuntimeException("Failed to create user due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while creating user: {}", e.getMessage());
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        try {
            log.info("Fetching all users");
            List<User> users = userRepository.findAll();
            return users.stream()
                    .map(this::mapToResponseDto)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Database error while fetching all users: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch users due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching all users: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch users", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> getUserById(Long id) {
        try {
            log.info("Fetching user with ID: {}", id);
            
            if (id == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            
            return userRepository.findById(id)
                    .map(this::mapToResponseDto);
                    
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument while fetching user: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while fetching user with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to fetch user due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching user with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to fetch user", e);
        }
    }

    @Override
    public Optional<UserResponseDto> updateUser(Long id, UserRequestDto userRequestDto) {
        try {
            log.info("Updating user with ID: {}", id);
            
            if (id == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            
            Optional<User> existingUserOpt = userRepository.findById(id);
            if (existingUserOpt.isEmpty()) {
                log.warn("User with ID {} not found for update", id);
                return Optional.empty();
            }
            
            User existingUser = existingUserOpt.get();
            
            // Check if email is being changed and if new email already exists
            if (!existingUser.getEmail().equals(userRequestDto.getEmail()) &&
                userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
                throw new DataIntegrityViolationException("User with email " + userRequestDto.getEmail() + " already exists");
            }
            
            existingUser.setName(userRequestDto.getName());
            existingUser.setEmail(userRequestDto.getEmail());
            
            User updatedUser = userRepository.save(existingUser);
            log.info("User updated successfully with ID: {}", updatedUser.getId());
            
            return Optional.of(mapToResponseDto(updatedUser));
            
        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            log.error("Error while updating user with ID {}: {}", id, e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while updating user with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to update user due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while updating user with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    public boolean deleteUser(Long id) {
        try {
            log.info("Deleting user with ID: {}", id);
            
            if (id == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            
            if (!userRepository.existsById(id)) {
                log.warn("User with ID {} not found for deletion", id);
                return false;
            }
            
            userRepository.deleteById(id);
            log.info("User deleted successfully with ID: {}", id);
            return true;
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument while deleting user: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while deleting user with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to delete user due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while deleting user with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    private User mapToEntity(UserRequestDto userRequestDto) {
        User user = new User();
        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());
        return user;
    }

    private UserResponseDto mapToResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}