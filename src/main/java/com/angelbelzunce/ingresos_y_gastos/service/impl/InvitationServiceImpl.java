package com.angelbelzunce.ingresos_y_gastos.service.impl;

import com.angelbelzunce.ingresos_y_gastos.dto.InvitationRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.InvitationResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.Group;
import com.angelbelzunce.ingresos_y_gastos.entity.Invitation;
import com.angelbelzunce.ingresos_y_gastos.entity.GroupMember;
import com.angelbelzunce.ingresos_y_gastos.entity.User;
import com.angelbelzunce.ingresos_y_gastos.enums.InvitationStatus;
import com.angelbelzunce.ingresos_y_gastos.enums.UserRole;
import com.angelbelzunce.ingresos_y_gastos.exception.*;
import com.angelbelzunce.ingresos_y_gastos.mapper.InvitationMapper;
import com.angelbelzunce.ingresos_y_gastos.repository.InvitationRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.GroupMemberRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.GroupRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.UserRepository;
import com.angelbelzunce.ingresos_y_gastos.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final InvitationMapper invitationMapper;


    @Override
    public InvitationResponse create(InvitationRequest request) {

        // Guardamos el grupo al que pertenece la invitacion
        Group group = groupRepository.findById(request.groupId())
                .orElseThrow(() -> new GroupNotFoundException(request.groupId()));

        // Guardamos al usuario que invita
        User invitedBy = userRepository.findById(request.invitedById())
                .orElseThrow(() -> new UserNotFoundException(request.invitedById()));

        // Guardamos al usuario invitado
        User invitedUser = userRepository.findById(request.invitedUserId())
                .orElseThrow(() -> new UserNotFoundException(request.invitedUserId()));

        // Guardamos al miembro del grupo que invita
        GroupMember invitedByMember = groupMemberRepository.findByGroupIdAndUserId(request.groupId(), request.invitedById())
                .orElseThrow(() -> new UserNotInGroupException(invitedBy.getId(), group.getId()));
        // Comprobamos si el usuario que invita es ADMIN
        if (!invitedByMember.getUserRole().equals(UserRole.ADMIN)){
            throw new UserIsNotAdminException();
        }

        // Comprobamos que el usuario invitado no sea ya miembro del grupo
        if(groupMemberRepository.existsByGroupIdAndUserId(request.groupId(), request.invitedUserId())){
            throw new InvitedUserAlreadyMemberException(request.groupId());
        }

        // Comprobamos que el ususario invitado no tenga invitaciones pendientes para el mismo grupo
        if (invitationRepository.existsByGroupIdAndInvitedUserIdAndInvitationStatus(request.groupId(), request.invitedUserId(), InvitationStatus.PENDING)){
            throw new InvitationAlreadyExistsException();
        }


        Invitation invitation = new Invitation();
        invitation.setGroup(group);
        invitation.setInvitedBy(invitedBy);
        invitation.setInvitedUser(invitedUser);
        invitation.setInvitationStatus(InvitationStatus.PENDING);

        return invitationMapper.toResponse(invitationRepository.save(invitation));
    }

    @Override
    public List<InvitationResponse> findMyPendingInvitations(Long userId) {
        return invitationRepository.findByInvitedUserIdAndInvitationStatus(userId, InvitationStatus.PENDING)
                .stream()
                .map(invitationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public InvitationResponse accept(Long invitationId, Long userId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new InvitationNotFoundException(invitationId));

        if (!invitation.getInvitedUser().getId().equals(userId)){
            throw new NotInvitedUserException(userId);
        }
        if (invitation.getInvitationStatus() != InvitationStatus.PENDING){
            throw new InvitationNotPendingException(invitationId);
        }

        invitation.setInvitationStatus(InvitationStatus.ACCEPTED);

        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(invitation.getGroup());
        groupMember.setUser(invitation.getInvitedUser());
        groupMember.setUserRole(UserRole.MEMBER);
        groupMemberRepository.save(groupMember);

        return invitationMapper.toResponse(invitationRepository.save(invitation));
    }

    @Override
    public InvitationResponse reject(Long invitationId, Long userId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new InvitationNotFoundException(invitationId));

        if (!invitation.getInvitedUser().getId().equals(userId)){
            throw new NotInvitedUserException(userId);
        }
        if (invitation.getInvitationStatus() != InvitationStatus.PENDING){
            throw new InvitationNotPendingException(invitationId);
        }

        invitation.setInvitationStatus(InvitationStatus.REJECTED);

        return invitationMapper.toResponse(invitationRepository.save(invitation));
    }


}
