package com.seettu.backend.repository;
import com.seettu.backend.entity.Payment;
import com.seettu.backend.entity.SeettuGroup;
import com.seettu.backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByMemberId(Long memberId);
    List<Payment> findByGroup(SeettuGroup group);
    List<Payment> findByGroupOrderByMonthNumber(SeettuGroup group);
    Optional<Payment> findByMemberAndMonthNumber(Member member, Integer monthNumber);
    List<Payment> findByMember(Member member);
}
