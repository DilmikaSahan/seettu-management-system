package com.seettu.backend.repository;
import com.seettu.backend.entity.Payment;
import com.seettu.backend.entity.SeettuGroup;
import com.seettu.backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByMemberId(Long memberId);
    List<Payment> findByGroup(SeettuGroup group);
    List<Payment> findByGroupOrderByMonthNumber(SeettuGroup group);
    Optional<Payment> findByMemberAndMonthNumber(Member member, Integer monthNumber);
    List<Payment> findByMember(Member member);
    
    // New methods for automated payment reminders
    @Query("SELECT p FROM Payment p WHERE p.paymentDate = :dueDate AND p.isPaid = false")
    List<Payment> findUpcomingUnpaidPayments(@Param("dueDate") LocalDate dueDate);

    @Query("SELECT p FROM Payment p WHERE p.paymentDate < :currentDate AND p.isPaid = false")
    List<Payment> findOverduePayments(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.group.id = :groupId AND p.isPaid = true AND p.paidAt >= :weekStart")
    long countPaidPaymentsThisWeek(@Param("groupId") Long groupId, @Param("weekStart") LocalDateTime weekStart);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.group.id = :groupId AND p.paymentDate < :currentDate AND p.isPaid = false")
    long countOverduePaymentsByGroup(@Param("groupId") Long groupId, @Param("currentDate") LocalDate currentDate);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.group.id = :groupId AND p.isPaid = true")
    Double sumPaidAmountsByGroup(@Param("groupId") Long groupId);
    
    // For cascade deletion
    @Modifying
    @Query("DELETE FROM Payment p WHERE p.paidByProvider.id = :providerId")
    void deleteByPaidByProviderId(@Param("providerId") Long providerId);
    
    @Modifying
    @Query("DELETE FROM Payment p WHERE p.member.id = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);
}
