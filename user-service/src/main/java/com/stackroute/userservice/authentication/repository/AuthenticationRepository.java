package com.stackroute.userservice.authentication.repository;

import com.stackroute.userservice.authentication.model.RegisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthenticationRepository extends JpaRepository<RegisteredUser, String> {

    Optional<RegisteredUser> getUserByEmail(String email);
}
