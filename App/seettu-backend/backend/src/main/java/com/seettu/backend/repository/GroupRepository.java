package com.seettu.backend.repository;
import com.seettu.backend.entity.GroupStatus;
import com.seettu.backend.entity.SeettuGroup;
import com.seettu.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRepository extends JpaRepository<SeettuGroup, Long> {
    List<SeettuGroup> findByProvider(User provider);
    List<SeettuGroup> findByProviderOrderByStartDateDesc(User provider);
    List<SeettuGroup> findByProviderAndStatusOrderByStartDateDesc(User provider, GroupStatus status);
    List<SeettuGroup> findByIsActiveTrue();
    
    // For cascade deletion checks
    @Query("SELECT g FROM SeettuGroup g WHERE g.provider.id = :providerId")
    List<SeettuGroup> findByProviderId(@Param("providerId") Long providerId);
}
