package com.siemens.internship;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //Added more validation
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotBlank
    private String status;

    // Added email regex validation
    @Email(message = "Email is not valid.")
    @NotBlank
    private String email;

    //Added toString for testing/debugging purposes
    public String toString() {
        return name + " " + description + " " + status + " " + email;
    }

}