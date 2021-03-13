package com.stackroute.userservice.authentication.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Entity class mapped to RegisteredUser Table
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class RegisteredUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    @NonNull
    private String email;
    @NonNull
    private String password;
}
