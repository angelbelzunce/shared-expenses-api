package com.angelbelzunce.ingresos_y_gastos.service.impl;

import com.angelbelzunce.ingresos_y_gastos.dto.GroupMemberResponse;
import com.angelbelzunce.ingresos_y_gastos.dto.GroupRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.GroupResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.Group;
import com.angelbelzunce.ingresos_y_gastos.entity.GroupMember;
import com.angelbelzunce.ingresos_y_gastos.entity.User;
import com.angelbelzunce.ingresos_y_gastos.enums.UserRole;
import com.angelbelzunce.ingresos_y_gastos.exception.GroupNotFoundException;
import com.angelbelzunce.ingresos_y_gastos.exception.UserIsNotAdminException;
import com.angelbelzunce.ingresos_y_gastos.exception.UserNotFoundException;
import com.angelbelzunce.ingresos_y_gastos.exception.UserNotInGroupException;
import com.angelbelzunce.ingresos_y_gastos.mapper.GroupMapper;
import com.angelbelzunce.ingresos_y_gastos.mapper.GroupMemberMapper;
import com.angelbelzunce.ingresos_y_gastos.repository.GroupMemberRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.GroupRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.UserRepository;
import com.angelbelzunce.ingresos_y_gastos.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    public List<GroupResponse> findGroups(Long userId) {
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
}
