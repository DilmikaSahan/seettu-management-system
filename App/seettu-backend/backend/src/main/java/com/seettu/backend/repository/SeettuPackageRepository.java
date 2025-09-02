package com.seettu.backend.repository;

import com.seettu.backend.entity.SeettuPackage;
import com.seettu.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeettuPackageRepository extends JpaRepository<SeettuPackage, Long> {
    List<SeettuPackage> findByProviderAndIsActiveTrue(User provider);
    List<SeettuPackage> findByIsActiveTrue();
    
    // For cascade deletion
    @Modifying
    @Query("DELETE FROM SeettuPackage sp WHERE sp.provider.id = :providerId")
    void deleteByProviderId(@Param("providerId") Long providerId);
}