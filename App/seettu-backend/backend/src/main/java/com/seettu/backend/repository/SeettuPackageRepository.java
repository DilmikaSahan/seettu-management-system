package com.seettu.backend.repository;

import com.seettu.backend.entity.SeettuPackage;
import com.seettu.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeettuPackageRepository extends JpaRepository<SeettuPackage, Long> {
    List<SeettuPackage> findByProviderAndIsActiveTrue(User provider);
    List<SeettuPackage> findByIsActiveTrue();
}