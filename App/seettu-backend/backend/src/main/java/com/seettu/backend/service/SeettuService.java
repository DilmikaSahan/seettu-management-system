package com.seettu.backend.service;

import com.seettu.backend.dto.CreateGroupRequest;
import com.seettu.backend.dto.GroupDTO;
import com.seettu.backend.dto.GroupDetailsResponse;
import com.seettu.backend.dto.PaymentStatus;
import com.seettu.backend.dto.SubscriberGroupDetails;
import com.seettu.backend.entity.*;
import com.seettu.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeettuService {
    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private SeettuPackageRepository packageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private SmsService smsService;

    public List<SeettuGroup> getAllGroups() {
        return groupRepository.findAll();
    }

    public List<GroupDTO> getGroupsByProvider(User provider) {
        List<SeettuGroup> groups = groupRepository.findByProviderOrderByStartDateDesc(provider);
        return groups.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private GroupDTO convertToDTO(SeettuGroup group) {
        GroupDTO dto = new GroupDTO();
        dto.setId(group.getId());
        dto.setGroupName(group.getGroupName());
        dto.setProviderName(group.getProvider().getName());
        dto.setProviderEmail(group.getProvider().getEmail());
        dto.setMonthlyAmount(group.getMonthlyAmount());
        dto.setNumberOfMonths(group.getNumberOfMonths());
        dto.setStartDate(group.getStartDate());
        dto.setIsActive(group.getIsActive());
        dto.setIsStarted(group.getIsStarted());
        dto.setStatus(group.getStatus());

        // Convert package
        GroupDTO.PackageDTO packageDTO = new GroupDTO.PackageDTO();
        packageDTO.setId(group.getSeettuPackage().getId());
        packageDTO.setPackageName(group.getSeettuPackage().getPackageName());
        packageDTO.setDescription(group.getSeettuPackage().getDescription());
        packageDTO.setPackageValue(group.getSeettuPackage().getPackageValue());
        packageDTO.setIsActive(group.getSeettuPackage().getIsActive());
        dto.setSeettuPackage(packageDTO);

        // Get member count
        List<Member> members = memberRepository.findByGroup(group);
        dto.setMemberCount(members.size());

        return dto;
    }

    public List<GroupDTO> getSubscriberGroups(User subscriber) {
        List<Member> memberships = memberRepository.findByUser(subscriber);
        return memberships.stream()
                .map(Member::getGroup)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SeettuGroup createGroup(CreateGroupRequest request, User provider) {
        SeettuPackage seettuPackage = packageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new RuntimeException("Package not found"));

        SeettuGroup group = new SeettuGroup();
        group.setGroupName(request.getGroupName());
        group.setProvider(provider);
        group.setSeettuPackage(seettuPackage);
        group.setMonthlyAmount(request.getMonthlyAmount());
        group.setNumberOfMonths(request.getNumberOfMonths());
        group.setStartDate(request.getStartDate());
        group.setIsActive(true);
        group.setIsStarted(false);
        group.setStatus(GroupStatus.PENDING);

        SeettuGroup savedGroup = groupRepository.save(group);

        // Add members
        List<Member> members = new ArrayList<>();
        for (CreateGroupRequest.MemberRequest memberRequest : request.getMembers()) {
            User user = userRepository.findById(memberRequest.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if order number is already taken
            if (memberRepository.existsByGroupAndOrderNumber(savedGroup, memberRequest.getOrderNumber())) {
                throw new RuntimeException("Order number " + memberRequest.getOrderNumber() + " is already taken");
            }

            Member member = new Member();
            member.setGroup(savedGroup);
            member.setUser(user);
            member.setOrderNumber(memberRequest.getOrderNumber());
            member.setJoinDate(LocalDate.now());
            member.setIsActive(true);

            members.add(memberRepository.save(member));
        }

        // Create payment records for all months
        createPaymentRecords(savedGroup, members);

        // Send SMS notification to provider about group creation
        if (provider.getPhoneNumber() != null && !provider.getPhoneNumber().isEmpty()) {
            try {
                smsService.sendGroupCreatedSms(
                    provider.getPhoneNumber(), 
                    savedGroup.getGroupName(), 
                    savedGroup.getMonthlyAmount(), 
                    provider.getName()
                );
            } catch (Exception e) {
                // Log error but don't fail group creation if SMS fails
                System.err.println("Failed to send group creation SMS to provider: " + e.getMessage());
            }
        }

        return savedGroup;
    }

    private void createPaymentRecords(SeettuGroup group, List<Member> members) {
        for (int month = 1; month <= group.getNumberOfMonths(); month++) {
            LocalDate paymentDate = group.getStartDate().plusMonths(month - 1);
            
            for (Member member : members) {
                Payment payment = new Payment();
                payment.setGroup(group);
                payment.setMember(member);
                payment.setAmount(group.getMonthlyAmount());
                payment.setPaymentDate(paymentDate);
                payment.setMonthNumber(month);
                payment.setIsPaid(false);
                
                paymentRepository.save(payment);
            }
        }
    }

    @Transactional
    public void startGroup(Long groupId, User provider) {
        SeettuGroup group = getGroupById(groupId);
        
        if (!group.getProvider().getId().equals(provider.getId())) {
            throw new RuntimeException("Only the provider can start the group");
        }
        
        if (group.getIsStarted()) {
            throw new RuntimeException("Group is already started");
        }

        group.setIsStarted(true);
        group.setStatus(GroupStatus.ACTIVE);
        groupRepository.save(group);

        // Send notifications to all members
        notificationService.createGroupStartedNotification(group);
        
        // Send SMS notifications to all members
        List<Member> members = memberRepository.findByGroup(group);
        for (Member member : members) {
            if (member.getUser().getPhoneNumber() != null && !member.getUser().getPhoneNumber().isEmpty()) {
                try {
                    smsService.sendGroupStartNotificationSms(
                        member.getUser().getPhoneNumber(),
                        group.getGroupName(),
                        group.getMonthlyAmount(),
                        member.getUser().getName()
                    );
                } catch (Exception e) {
                    // Log error but don't fail group start if SMS fails
                    System.err.println("Failed to send group start SMS to member " + member.getUser().getEmail() + ": " + e.getMessage());
                }
            }
        }
    }

    @Transactional
    public void markPaymentAsPaid(Long paymentId, User provider) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (!payment.getGroup().getProvider().getId().equals(provider.getId())) {
            throw new RuntimeException("Only the provider can mark payments as paid");
        }

        payment.setIsPaid(true);
        payment.setPaidAt(LocalDateTime.now());
        payment.setPaidByProvider(provider);
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Send SMS confirmation to the member
        User member = savedPayment.getMember().getUser();
        if (member.getPhoneNumber() != null && !member.getPhoneNumber().isEmpty()) {
            try {
                String monthName = savedPayment.getGroup().getStartDate()
                    .plusMonths(savedPayment.getMonthNumber() - 1)
                    .getMonth().toString();
                
                smsService.sendPaymentReceivedSms(
                    member.getPhoneNumber(),
                    savedPayment.getGroup().getGroupName(),
                    savedPayment.getGroup().getMonthlyAmount(),
                    monthName,
                    member.getName()
                );
            } catch (Exception e) {
                // Log error but don't fail payment marking if SMS fails
                System.err.println("Failed to send payment confirmation SMS to member " + member.getEmail() + ": " + e.getMessage());
            }
        }

        // Check if this payment triggers a payout
        checkForPayout(savedPayment);

        // Check if all payments are made to complete the group
        checkAndCompleteGroup(payment.getGroup().getId());
    }
    
    private void checkForPayout(Payment payment) {
        int monthNumber = payment.getMonthNumber();
        SeettuGroup group = payment.getGroup();
        
        // Find who should receive the payout this month
        List<Member> members = memberRepository.findByGroup(group);
        Member payoutRecipient = members.stream()
            .filter(member -> member.getOrderNumber() == monthNumber)
            .findFirst()
            .orElse(null);
            
        if (payoutRecipient != null) {
            // Check if all members have paid for this month
            boolean allPaidForMonth = true;
            for (Member member : members) {
                Payment memberPayment = paymentRepository.findByMemberAndMonthNumber(member, monthNumber).orElse(null);
                if (memberPayment == null || !memberPayment.getIsPaid()) {
                    allPaidForMonth = false;
                    break;
                }
            }
            
            if (allPaidForMonth) {
                // Send payout notification SMS
                User recipient = payoutRecipient.getUser();
                if (recipient.getPhoneNumber() != null && !recipient.getPhoneNumber().isEmpty()) {
                    try {
                        double payoutAmount = group.getMonthlyAmount() * members.size();
                        smsService.sendPayoutNotificationSms(
                            recipient.getPhoneNumber(),
                            group.getGroupName(),
                            payoutAmount,
                            recipient.getName()
                        );
                    } catch (Exception e) {
                        System.err.println("Failed to send payout notification SMS: " + e.getMessage());
                    }
                }
            }
        }
    }

    public GroupDetailsResponse getGroupDetails(Long groupId) {
        SeettuGroup group = getGroupById(groupId);
        
        GroupDetailsResponse response = new GroupDetailsResponse();
        response.setId(group.getId());
        response.setGroupName(group.getGroupName());
        response.setPackageName(group.getSeettuPackage().getPackageName());
        response.setMonthlyAmount(group.getMonthlyAmount());
        response.setNumberOfMonths(group.getNumberOfMonths());
        response.setStartDate(group.getStartDate());
        response.setIsActive(group.getIsActive());
        response.setIsStarted(group.getIsStarted());

        // Get members
        List<Member> members = memberRepository.findByGroup(group);
        List<GroupDetailsResponse.MemberDetails> memberDetails = members.stream()
                .map(member -> {
                    GroupDetailsResponse.MemberDetails details = new GroupDetailsResponse.MemberDetails();
                    details.setId(member.getId());
                    details.setName(member.getUser().getName());
                    details.setPhoneNumber(member.getUser().getPhoneNumber());
                    details.setOrderNumber(member.getOrderNumber());
                    // Calculate package receive date manually to avoid lazy loading issues
                    details.setPackageReceiveDate(group.getStartDate().plusMonths(member.getOrderNumber() - 1));
                    return details;
                })
                .collect(Collectors.toList());
        response.setMembers(memberDetails);

        // Get monthly payment status
        List<GroupDetailsResponse.MonthlyPaymentStatus> monthlyPayments = new ArrayList<>();
        for (int month = 1; month <= group.getNumberOfMonths(); month++) {
            GroupDetailsResponse.MonthlyPaymentStatus monthStatus = new GroupDetailsResponse.MonthlyPaymentStatus();
            monthStatus.setMonthNumber(month);
            monthStatus.setPaymentDate(group.getStartDate().plusMonths(month - 1));

            List<GroupDetailsResponse.MemberPaymentStatus> memberPayments = new ArrayList<>();
            for (Member member : members) {
                Payment payment = paymentRepository.findByMemberAndMonthNumber(member, month).orElse(null);
                
                GroupDetailsResponse.MemberPaymentStatus memberPayment = new GroupDetailsResponse.MemberPaymentStatus();
                memberPayment.setMemberId(member.getId());
                memberPayment.setMemberName(member.getUser().getName());
                memberPayment.setIsPaid(payment != null ? payment.getIsPaid() : false);
                memberPayment.setPaymentId(payment != null ? payment.getId() : null);
                
                memberPayments.add(memberPayment);
            }
            
            monthStatus.setMemberPayments(memberPayments);
            monthlyPayments.add(monthStatus);
        }
        response.setMonthlyPayments(monthlyPayments);

        return response;
    }

    public SeettuGroup getGroupById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found"));
    }

    @Transactional
    public void cancelGroup(Long groupId, User provider) {
        SeettuGroup group = getGroupById(groupId);
        
        if (!group.getProvider().getId().equals(provider.getId())) {
            throw new RuntimeException("Only the provider can cancel the group");
        }
        
        if (group.getStatus() == GroupStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel a completed group");
        }
        
        if (group.getStatus() == GroupStatus.CANCELLED) {
            throw new RuntimeException("Group is already cancelled");
        }

        group.setStatus(GroupStatus.CANCELLED);
        group.setIsActive(false);
        groupRepository.save(group);

        // Send notifications to all members
        notificationService.createGroupCancelledNotification(group);
    }

    @Transactional
    public void checkAndCompleteGroup(Long groupId) {
        SeettuGroup group = getGroupById(groupId);
        
        if (group.getStatus() != GroupStatus.ACTIVE) {
            return; // Only check active groups
        }

        // Check if all payments are made
        List<Member> members = memberRepository.findByGroup(group);
        boolean allPaymentsMade = true;
        
        for (Member member : members) {
            List<Payment> memberPayments = paymentRepository.findByMember(member);
            for (Payment payment : memberPayments) {
                if (!payment.getIsPaid()) {
                    allPaymentsMade = false;
                    break;
                }
            }
            if (!allPaymentsMade) break;
        }

        // Check if group duration is complete
        LocalDate endDate = group.getStartDate().plusMonths(group.getNumberOfMonths());
        boolean durationComplete = LocalDate.now().isAfter(endDate) || LocalDate.now().equals(endDate);

        if (allPaymentsMade || durationComplete) {
            group.setStatus(GroupStatus.COMPLETED);
            groupRepository.save(group);
            
            // Send completion notifications
            notificationService.createGroupCompletedNotification(group);
        }
    }

    public List<GroupDTO> getGroupsByStatus(User provider, GroupStatus status) {
        List<SeettuGroup> groups;
        if (status == null) {
            groups = groupRepository.findByProviderOrderByStartDateDesc(provider);
        } else {
            groups = groupRepository.findByProviderAndStatusOrderByStartDateDesc(provider, status);
        }
        
        return groups.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteGroup(Long id) {
        if (!groupRepository.existsById(id)) {
            throw new RuntimeException("Group not found");
        }
        groupRepository.deleteById(id);
    }

    // New methods for subscriber payment details
    public List<PaymentStatus> getSubscriberPayments(Long groupId, User subscriber) {
        SeettuGroup group = getGroupById(groupId);
        
        // Find the member record for this subscriber in this group
        Member member = memberRepository.findByGroupAndUser(group, subscriber)
                .orElseThrow(() -> new RuntimeException("You are not a member of this group"));
        
        // Get all payments for this member
        List<Payment> payments = paymentRepository.findByMember(member);
        
        return payments.stream()
                .map(payment -> new PaymentStatus(
                    payment.getId(),
                    payment.getMonthNumber(),
                    payment.getAmount(),
                    payment.getPaymentDate(),
                    payment.getIsPaid(),
                    payment.getPaidAt()
                ))
                .sorted((a, b) -> a.getMonthNumber().compareTo(b.getMonthNumber()))
                .collect(Collectors.toList());
    }

    public SubscriberGroupDetails getGroupDetailsForSubscriber(Long groupId, User subscriber) {
        SeettuGroup group = getGroupById(groupId);
        
        // Find the member record for this subscriber in this group
        Member member = memberRepository.findByGroupAndUser(group, subscriber)
                .orElseThrow(() -> new RuntimeException("You are not a member of this group"));
        
        SubscriberGroupDetails details = new SubscriberGroupDetails();
        details.setGroupId(group.getId());
        details.setGroupName(group.getGroupName());
        details.setPackageName(group.getSeettuPackage().getPackageName());
        details.setMonthlyAmount(group.getMonthlyAmount());
        details.setNumberOfMonths(group.getNumberOfMonths());
        details.setStartDate(group.getStartDate());
        details.setIsActive(group.getIsActive());
        details.setIsStarted(group.getIsStarted());
        
        // Member-specific details
        details.setMyOrderNumber(member.getOrderNumber());
        details.setMyPackageReceiveDate(member.getPackageReceiveDate());
        details.setHasReceivedPackage(member.getPackageReceiveDate().isBefore(LocalDate.now()) || 
                                     member.getPackageReceiveDate().equals(LocalDate.now()));
        
        // Get payment details
        List<PaymentStatus> payments = getSubscriberPayments(groupId, subscriber);
        details.setPayments(payments);
        
        // Calculate statistics
        long paidCount = payments.stream().filter(p -> p.getIsPaid()).count();
        long overdueCount = payments.stream().filter(p -> p.getIsOverdue()).count();
        double totalPaid = payments.stream().filter(p -> p.getIsPaid()).mapToDouble(PaymentStatus::getAmount).sum();
        double totalOwed = payments.stream().filter(p -> !p.getIsPaid()).mapToDouble(PaymentStatus::getAmount).sum();
        
        details.setTotalPaidMonths((int) paidCount);
        details.setTotalOverdueMonths((int) overdueCount);
        details.setTotalPaidAmount(totalPaid);
        details.setTotalOwedAmount(totalOwed);
        
        // Calculate progress
        if (group.getIsStarted()) {
            LocalDate startDate = group.getStartDate();
            LocalDate currentDate = LocalDate.now();
            long monthsDiff = java.time.temporal.ChronoUnit.MONTHS.between(startDate, currentDate) + 1;
            int currentMonth = (int) Math.min(monthsDiff, group.getNumberOfMonths());
            details.setCurrentMonth(currentMonth);
            details.setProgressPercentage((double) currentMonth / group.getNumberOfMonths() * 100);
        } else {
            details.setCurrentMonth(0);
            details.setProgressPercentage(0.0);
        }
        
        return details;
    }
    
    // Alias method for the controller
    public SubscriberGroupDetails getSubscriberGroupDetails(Long groupId, User subscriber) {
        return getGroupDetailsForSubscriber(groupId, subscriber);
    }
}
