package com.devdaniel.MarathonAPI.repositories;

import com.devdaniel.MarathonAPI.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByCpf(String cpf);
    List<User> findByCodeContainingIgnoreCase(String code);


}