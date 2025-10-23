package com.devdaniel.MarathonAPI.controllers;

import com.devdaniel.MarathonAPI.models.User;
import com.devdaniel.MarathonAPI.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"https://saofranciscorun.com"})
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserService userService;

    @Value("${external.api.key}")
    private String apiKey;

    /**
     * Lista todos os usuários ou filtra por código (se informado)
     */
    @GetMapping("/list")
    public ResponseEntity<?> getUsers(@RequestParam(required = false) String code) {
        if (code != null && !code.trim().isEmpty()) {
            List<User> filteredUsers = userService.findByCode(code.trim());
            return ResponseEntity.ok(filteredUsers);
        }
        return ResponseEntity.ok(userService.findAll());
    }

    /**
     * Cria um novo usuário com base no CPF e CEP consultando APIs externas.
     */
    @PostMapping("/create")
    public ResponseEntity<?> createUserByCpf(@RequestBody Map<String, String> request) {
        String cpf = request.getOrDefault("cpf", "").replaceAll("\\D", "");
        String cep = request.getOrDefault("cep", "").replaceAll("\\D", "");
        String shirtStr = request.getOrDefault("shirt", "").toUpperCase();

        if (cpf.length() != 11) {
            return ResponseEntity.badRequest().body("CPF inválido. Deve conter 11 dígitos.");
        }

        List<String> validShirts = List.of("PP", "P", "M", "G", "GG", "XG");
        if (!validShirts.contains(shirtStr)) {
            return ResponseEntity.badRequest().body("Tamanho da camisa inválido. Valores válidos: " + validShirts);
        }

        String urlCpf = "https://apicpf.com/api/consulta?cpf=" + cpf;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> responseCpf = restTemplate.exchange(urlCpf, HttpMethod.GET, entity, Map.class);

            if (!responseCpf.getStatusCode().is2xxSuccessful() || responseCpf.getBody() == null) {
                return ResponseEntity.status(responseCpf.getStatusCode())
                        .body("Erro na API externa de CPF: " + responseCpf.getBody());
            }

            Map<String, Object> data = (Map<String, Object>) responseCpf.getBody().get("data");
            if (data == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Dados não encontrados para o CPF informado.");
            }

            User user = new User();

            // Preenchendo todos os campos obrigatórios com fallback caso algum dado falhe
            user.setCpf((String) data.getOrDefault("cpf", cpf));
            user.setName((String) data.getOrDefault("nome", "Nome não informado"));

            try {
                user.setBirthDate(LocalDate.parse((String) data.getOrDefault("data_nascimento", "1900-01-01")));
            } catch (Exception e) {
                user.setBirthDate(LocalDate.of(1900, 1, 1));
            }

            String genero = (String) data.getOrDefault("genero", "M");
            user.setGender("M".equalsIgnoreCase(genero) ? com.devdaniel.MarathonAPI.enums.GenEnum.M
                    : com.devdaniel.MarathonAPI.enums.GenEnum.F);

            user.setShirt(shirtStr);

            // Cidade via CEP
            if (cep.length() == 8) {
                try {
                    String urlCep = "https://viacep.com.br/ws/" + cep + "/json/";
                    Map responseCep = restTemplate.getForObject(urlCep, Map.class);
                    user.setCity(responseCep != null && responseCep.get("localidade") != null
                            ? (String) responseCep.get("localidade")
                            : "Cidade não encontrada");
                } catch (Exception e) {
                    user.setCity("Cidade não encontrada");
                }
            } else {
                user.setCity("CEP inválido");
            }

            // Salva no banco
            userService.create(user);
            return ResponseEntity.ok(user);

        } catch (Exception ex) {
            ex.printStackTrace(); // log completo do erro
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao criar usuário: " + ex.getMessage());
        }
    }

}
