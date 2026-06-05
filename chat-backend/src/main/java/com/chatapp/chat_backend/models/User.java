package com.chatapp.chat_backend.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet; import java.util.Set;


@Entity
@Table(name = "users") // This tells Spring to create a table named 'users'
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

   @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    @Column(name = "is_online")
    private boolean isOnline = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore // Crucial: Prevents infinite looping when converting data to JSON
    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
    private Set<ChatRoom> chatRooms = new HashSet<>();

    public Set<ChatRoom> getChatRooms() { return chatRooms; }
    public void setChatRooms(Set<ChatRoom> chatRooms) { this.chatRooms = chatRooms; }

    // Default Constructor (Required by JPA)
    public User() {}

    // Custom Constructor
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean online) { isOnline = online; }
}