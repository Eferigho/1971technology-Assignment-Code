package com.agency.service;

import com.agency.data.dto.UserRequestDto;
import com.agency.data.dto.UserResponseDto;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

public interface UserService {
    boolean deleteUser(Long id);
    UserResponseDto createUser( UserRequestDto userRequestDto);
    List<UserResponseDto> getAllUsers();
    Optional<UserResponseDto> getUserById(Long id);
    Optional<UserResponseDto> updateUser(Long id, UserRequestDto userRequestDto);
}