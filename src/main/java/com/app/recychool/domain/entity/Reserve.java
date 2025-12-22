package com.app.recychool.domain.entity;

import com.app.recychool.domain.enums.ReserveStatus;
import com.app.recychool.domain.enums.ReserveType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_RESERVE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "SEQ_RESERVE_GENERATOR",
        sequenceName = "SEQ_RESERVE",
        allocationSize = 1
)
public class Reserve {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_RESERVE_GENERATOR")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCHOOL_ID", nullable = false)
    private School school;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReserveType reserveType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReserveStatus reserveStatus;

    private Integer waitingOrder;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer reservePrice;

    @Column(nullable = false)
    private Integer reserveDeposit;

    private LocalDateTime createdAt;

    public void cancel() {
        if (this.reserveStatus == ReserveStatus.CANCELED) return;
        this.reserveStatus = ReserveStatus.CANCELED;
    }

    public void expire() {
        if (this.reserveStatus != ReserveStatus.COMPLETED) return;
        this.reserveStatus = ReserveStatus.EXPIRED;
    }

    public void decreaseWaitingOrder() {
        if (this.waitingOrder != null && this.waitingOrder > 0) {
            this.waitingOrder--;
        }
    }

}
