package com.devdaniel.MarathonAPI.services;

import com.devdaniel.MarathonAPI.models.User;
import com.devdaniel.MarathonAPI.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(User user) {
        if (userRepository.existsByCpf(user.getCpf())) {
            throw new DataIntegrityViolationException("Já existe um usuário com este CPF.");
        }
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
    }

    public User update(UUID id, User updatedUser) {
        User existing = findById(id);

        existing.setName(updatedUser.getName());
        existing.setCpf(updatedUser.getCpf());
        existing.setBirthDate(updatedUser.getBirthDate());
        existing.setGender(updatedUser.getGender());
        existing.setShirt(updatedUser.getShirt());
        existing.setCity(updatedUser.getCity());

        return userRepository.save(existing);
    }

    public void delete(UUID id) {
        User existing = findById(id);
        userRepository.delete(existing);
    }
}