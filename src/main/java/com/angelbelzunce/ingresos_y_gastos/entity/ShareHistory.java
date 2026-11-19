package com.angelbelzunce.ingresos_y_gastos.entity;

import com.angelbelzunce.ingresos_y_gastos.enums.ShareStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "share_history")
public class ShareHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User actingUser;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private PaymentShare share;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ShareStatus fromStatus;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ShareStatus toStatus;
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime modifiedAt;

}
