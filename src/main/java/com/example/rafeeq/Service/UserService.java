package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.UserRequestDTO;
import com.example.rafeeq.DTO.UserResponseDTO;
import com.example.rafeeq.Model.User;
import com.example.rafeeq.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Get all users
    public List<UserResponseDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // Get user by ID
    public UserResponseDTO getUserById(Integer id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("User not found"));

        return convertToResponse(user);
    }

    // Add new user
    public UserResponseDTO addUser(UserRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ApiException("Email already exists");
        }

        if (userRepository.existsByPhone(dto.getPhone())) {
            throw new ApiException("Phone already exists");
        }

        User user = new User();

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(dto.getPassword());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setGender(dto.getGender());
        user.setWhatsappOptIn(dto.getWhatsappOptIn());
        user.setRole("USER");

        userRepository.save(user);

        return convertToResponse(user);
    }

    // Update user
    public UserResponseDTO updateUser(Integer id, UserRequestDTO dto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("User not found"));

        if (userRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new ApiException("Email already exists");
        }

        if (userRepository.existsByPhoneAndIdNot(dto.getPhone(), id)) {
            throw new ApiException("Phone already exists");
        }

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(dto.getPassword());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setGender(dto.getGender());
        user.setWhatsappOptIn(dto.getWhatsappOptIn());

        userRepository.save(user);

        return convertToResponse(user);
    }

    // Delete user
    public void deleteUser(Integer id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("User not found"));

        userRepository.delete(user);
    }

    // Convert User Model to Response DTO
    private UserResponseDTO convertToResponse(User user) {

        UserResponseDTO response = new UserResponseDTO();

        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setGender(user.getGender());
        response.setWhatsappOptIn(user.getWhatsappOptIn());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;
    }
}