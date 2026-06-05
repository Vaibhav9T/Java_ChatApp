package com.chatapp.chat_backend.repositories;

import com.chatapp.chat_backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring automatically writes the SQL query for this method based on its name!
    Optional<User> findByUsername(String username);
}