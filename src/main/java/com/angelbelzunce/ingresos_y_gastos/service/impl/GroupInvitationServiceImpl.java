package com.angelbelzunce.ingresos_y_gastos.service.impl;

import com.angelbelzunce.ingresos_y_gastos.dto.GroupInvitationRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.GroupInvitationResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.Group;
import com.angelbelzunce.ingresos_y_gastos.entity.GroupInvitation;
import com.angelbelzunce.ingresos_y_gastos.entity.GroupMember;
import com.angelbelzunce.ingresos_y_gastos.entity.User;
import com.angelbelzunce.ingresos_y_gastos.enums.InvitationStatus;
import com.angelbelzunce.ingresos_y_gastos.enums.UserRole;
import com.angelbelzunce.ingresos_y_gastos.exception.GroupNotFoundException;
import com.angelbelzunce.ingresos_y_gastos.exception.UserNotFoundException;
import com.angelbelzunce.ingresos_y_gastos.mapper.GroupInvitationMapper;
import com.angelbelzunce.ingresos_y_gastos.repository.GroupInvitationRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.GroupMemberRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.GroupRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.UserRepository;
import com.angelbelzunce.ingresos_y_gastos.service.GroupInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupInvitationServiceImpl implements GroupInvitationService {

    private final GroupInvitationRepository invitationRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupInvitationMapper invitationMapper;


    @Override
    public GroupInvitationResponse create(GroupInvitationRequest request) {

        GroupInvitation invitation = new GroupInvitation();

        Group group = groupRepository.findById(request.groupId())
                .orElseThrow(() -> new GroupNotFoundException(request.groupId()));

        User invitedBy = userRepository.findById(request.invitedById())
                .orElseThrow(() -> new UserNotFoundException(request.invitedById()));

        User invitedUser = userRepository.findById(request.invitedUserId())
                .orElseThrow(() -> new UserNotFoundException(request.invitedUserId()));

        invitation.setGroup(group);
        invitation.setInvitedBy(invitedBy);
        invitation.setInvitedUser(invitedUser);
        invitation.setInvitationStatus(InvitationStatus.PENDING);

        return invitationMapper.toResponse(invitationRepository.save(invitation));
    }

    @Override
    public List<GroupInvitationResponse> findMyPendingInvitations(Long userId) {
        return invitationRepository.findByInvitedUserIdAndInvitationStatus(userId, InvitationStatus.PENDING)
                .stream()
                .map(invitationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public GroupInvitationResponse accept(Long invitationId, Long userId) {
        GroupInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException()); // TODO: Create exception

        if (!invitation.getInvitedUser().getId().equals(userId)){
            throw new RuntimeException();// TODO: Create exception
        }
        if (invitation.getInvitationStatus() != InvitationStatus.PENDING){
            throw new RuntimeException();// TODO: Create exception
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
    public GroupInvitationResponse reject(Long invitationId, Long userId) {
        GroupInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException()); // TODO: Create exception

        if (!invitation.getInvitedUser().getId().equals(userId)){
            throw new RuntimeException();// TODO: Create exception
        }
        if (invitation.getInvitationStatus() != InvitationStatus.PENDING){
            throw new RuntimeException();// TODO: Create exception
        }

        invitation.setInvitationStatus(InvitationStatus.REJECTED);

        return invitationMapper.toResponse(invitationRepository.save(invitation));
    }


}
