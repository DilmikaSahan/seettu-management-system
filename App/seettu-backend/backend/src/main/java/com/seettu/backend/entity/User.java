package com.seettu.backend.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @JsonIgnore
    private String password;
    
    @Column(unique = true)
    private String phoneNumber;
    
    @Column(unique = true)
    private String userId; // Custom ID for searching

    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "is_suspended")
    private Boolean isSuspended = false;
    
    @Column(name = "suspended_date")
    private LocalDateTime suspendedDate;
    
    @Column(name = "suspension_reason")
    private String suspensionReason;

    // Custom constructor for backward compatibility
    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdDate = LocalDateTime.now();
    }

    // Custom method for backward compatibility
    public String getFullName() {
        return name;
    }

    public void setFullName(String fullName) {
        this.name = fullName;
    }
}
