package com.seettu.backend.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private SeettuGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer orderNumber; // The month number when member receives package

    @Column(nullable = false)
    private LocalDate joinDate;

    @Column(nullable = false)
    private Boolean isActive = true;

    // Calculated field for when this member receives the package
    public LocalDate getPackageReceiveDate() {
        if (group != null && group.getStartDate() != null) {
            return group.getStartDate().plusMonths(orderNumber - 1);
        }
        return null;
    }
}
