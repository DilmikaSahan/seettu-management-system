package com.seettu.backend.repository;

import com.seettu.backend.entity.Member;
import com.seettu.backend.entity.SeettuGroup;
import com.seettu.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByGroup(SeettuGroup group);
    List<Member> findByUser(User user);
    List<Member> findByUserId(Long userId);
    Optional<Member> findByGroupAndOrderNumber(SeettuGroup group, Integer orderNumber);
    Optional<Member> findByGroupAndUser(SeettuGroup group, User user);
    boolean existsByGroupAndOrderNumber(SeettuGroup group, Integer orderNumber);
    
    // For automated reminder system
    @Query("SELECT COUNT(m) FROM Member m WHERE m.group.id = :groupId")
    long countByGroupId(@Param("groupId") Long groupId);
    
    // For cascade deletion
    @Modifying
    @Query("DELETE FROM Member m WHERE m.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}