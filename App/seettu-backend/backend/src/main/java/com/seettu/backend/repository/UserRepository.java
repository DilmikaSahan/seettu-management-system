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
    
    // Admin-specific queries
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") Role role);
    
    @Query("SELECT u FROM User u ORDER BY u.createdDate DESC")
    List<User> findAllOrderByCreatedDateDesc();
    
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY u.createdDate DESC")
    List<User> searchAllUsers(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.role = 'ADMIN'")
    boolean existsAdmin();
}
