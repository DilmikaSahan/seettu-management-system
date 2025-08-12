package com.seettu.backend.repository;
import com.seettu.backend.entity.Role;
import com.seettu.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByUserId(String userId);
    boolean existsByUserId(String userId);
    
    List<User> findByRole(Role role);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND " +
           "(LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.userId) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<User> searchUsersByRoleAndTerm(@Param("role") Role role, @Param("searchTerm") String searchTerm);
}
