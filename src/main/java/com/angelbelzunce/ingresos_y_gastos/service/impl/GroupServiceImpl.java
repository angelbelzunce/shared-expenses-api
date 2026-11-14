package com.angelbelzunce.ingresos_y_gastos.service.impl;

import com.angelbelzunce.ingresos_y_gastos.dto.GroupMemberResponse;
import com.angelbelzunce.ingresos_y_gastos.dto.GroupRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.GroupResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.Group;
import com.angelbelzunce.ingresos_y_gastos.entity.GroupMember;
import com.angelbelzunce.ingresos_y_gastos.entity.User;
import com.angelbelzunce.ingresos_y_gastos.enums.UserRole;
import com.angelbelzunce.ingresos_y_gastos.exception.*;
import com.angelbelzunce.ingresos_y_gastos.mapper.GroupMapper;
import com.angelbelzunce.ingresos_y_gastos.mapper.GroupMemberMapper;
import com.angelbelzunce.ingresos_y_gastos.repository.GroupMemberRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.GroupRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.UserRepository;
import com.angelbelzunce.ingresos_y_gastos.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final GroupMapper groupMapper;
    private final GroupMemberMapper groupMemberMapper;

    @Override
    public GroupResponse findById(Long id) {
        return groupMapper.toResponse(groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupMemberResponse> findMembers(Long groupId) {
        groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));

        return groupMemberRepository.findByGroupId(groupId)
                .stream()
                .map(groupMemberMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupResponse> findGroupsByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return groupMemberRepository.findByUserId(userId)
                .stream()
                .map(groupMember -> groupMapper.toResponse(groupMember.getGroup()))
                .toList();

    }

    @Override
    @Transactional
    public GroupResponse create(GroupRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Group group = groupMapper.toEntity(request);
        group.setGroupOwner(user);
        Group savedGroup = groupRepository.save(group);

        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(savedGroup);
        groupMember.setUser(user);
        groupMember.setUserRole(UserRole.ADMIN);

        groupMemberRepository.save(groupMember);

        return groupMapper.toResponse(savedGroup);
    }

    @Override
    public GroupResponse update(Long id, GroupRequest request, Long userId) {

        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException(id));

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(id, userId)
                .orElseThrow(() -> new UserNotInGroupException(userId, id));

        if (!groupMember.getUserRole().equals(UserRole.ADMIN)){
            throw new UserIsNotAdminException();
        }

        group.setName(request.name());
        Group saved = groupRepository.save(group);
        return groupMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id, Long userId) {

        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException(id));

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(id, userId)
                .orElseThrow(() -> new UserNotInGroupException(userId, id));

        if (!groupMember.getUserRole().equals(UserRole.ADMIN)){
            throw new UserIsNotAdminException();
        }

        groupRepository.delete(group);
    }

    @Transactional
    @Override
    public void leave(Long groupId, Long currentUserId) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, currentUserId)
                .orElseThrow(() -> new UserNotInGroupException(currentUserId, groupId));

        List<GroupMember> memberList = groupMemberRepository.findByGroupId(groupId);

        if (memberList.size() == 1){
            delete(groupId, currentUserId);
            return;
        }

        List<GroupMember> adminRoles = memberList
                .stream()
                .filter(member -> member.getUserRole().equals(UserRole.ADMIN))
                .toList();

        if (adminRoles.size() == 1 && groupMember.getUserRole().equals(UserRole.ADMIN)){
            throw new AdminCannotLeaveGroupException();
        }

        if (group.getGroupOwner().getId().equals(currentUserId)){

            GroupMember nextOlderAdmin = adminRoles
                    .stream()
                    .filter(member -> !member.getUser().getId().equals(currentUserId))
                    .min(Comparator.comparing(GroupMember::getJoinedAt))
                    .orElseThrow(RuntimeException::new);


            group.setGroupOwner(nextOlderAdmin.getUser());

            groupRepository.save(group);
        }



        groupMemberRepository.delete(groupMember);
    }

    @Override
    public void addNewAdmin(Long groupId, Long newAdminId, Long currentUserId) {

        userRepository.findById(newAdminId)
                .orElseThrow(() -> new UserNotFoundException(newAdminId));

        userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException(currentUserId));

        groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        GroupMember newAdminMember = groupMemberRepository.findByGroupIdAndUserId(groupId, newAdminId)
                .orElseThrow(() -> new UserNotInGroupException(newAdminId, groupId));

        if (newAdminMember.getUserRole().equals(UserRole.ADMIN)){
            throw new UserIsAlreadyAdminException(newAdminId);
        }

        GroupMember currentAdmin = groupMemberRepository.findByGroupIdAndUserId(groupId, currentUserId)
                .orElseThrow(() -> new UserNotInGroupException(currentUserId, groupId));

        if (!currentAdmin.getUserRole().equals(UserRole.ADMIN)){
            throw new UserIsNotAdminException();
        }

        newAdminMember.setUserRole(UserRole.ADMIN);

        groupMemberRepository.save(newAdminMember);


    }
}
