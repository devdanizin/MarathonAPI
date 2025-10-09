package com.devdaniel.MarathonAPI.models;

import com.devdaniel.MarathonAPI.enums.GenEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos.")
    @Column(unique = true, length = 11, nullable = false)
    private String cpf;

    @NotNull(message = "A data de nascimento é obrigatória.")
    @Past(message = "A data de nascimento deve ser anterior à data atual.")
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @NotNull(message = "O gênero é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(length = 1, nullable = false)
    private GenEnum gender;

    @Column(length = 1)
    @Pattern(regexp = "^[A-Za-z]$", message = "A camisa deve ter um tamanho.")
    private Character shirt;

    @NotBlank(message = "Cidade não pode ser vazio")
    @Size(max = 50, message = "Cidade deve ter no máximo 50 caracteres")
    @Column(nullable = false)
    private String city;
}
