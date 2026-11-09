package com.angelbelzunce.ingresos_y_gastos.entity;

import com.angelbelzunce.ingresos_y_gastos.enums.InvitationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "group_invitations")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Group group;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_user_id", nullable = false)
    private User invitedUser;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by_id", nullable = false)
    private User invitedBy;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InvitationStatus invitationStatus;
    @CreationTimestamp
    private LocalDateTime createdAt;



}
