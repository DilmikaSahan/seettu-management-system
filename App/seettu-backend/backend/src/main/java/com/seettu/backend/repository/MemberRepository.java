package com.seettu.backend.repository;

import com.seettu.backend.entity.Member;
import com.seettu.backend.entity.SeettuGroup;
import com.seettu.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByGroup(SeettuGroup group);
    List<Member> findByUser(User user);
    Optional<Member> findByGroupAndOrderNumber(SeettuGroup group, Integer orderNumber);
    Optional<Member> findByGroupAndUser(SeettuGroup group, User user);
    boolean existsByGroupAndOrderNumber(SeettuGroup group, Integer orderNumber);
}