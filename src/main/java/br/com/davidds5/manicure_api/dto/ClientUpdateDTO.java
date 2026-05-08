package br.com.davidds5.manicure_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class ClientUpdateDTO {

    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String name;

    @Email(message = "Email inválido")
    private String email;

    @Size(min = 10, max = 15, message = "Telefone deve ter entre 10 e 15 caracteres")
    private String phone;

    public ClientUpdateDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}