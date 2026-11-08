package com.angelbelzunce.ingresos_y_gastos.service.impl;

import com.angelbelzunce.ingresos_y_gastos.dto.GroupMemberResponse;
import com.angelbelzunce.ingresos_y_gastos.dto.GroupRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.GroupResponse;
import com.angelbelzunce.ingresos_y_gastos.dto.UserResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.Group;
import com.angelbelzunce.ingresos_y_gastos.entity.GroupMember;
import com.angelbelzunce.ingresos_y_gastos.entity.User;
import com.angelbelzunce.ingresos_y_gastos.enums.Role;
import com.angelbelzunce.ingresos_y_gastos.exception.GroupNotFoundException;
import com.angelbelzunce.ingresos_y_gastos.exception.UserNotFoundException;
import com.angelbelzunce.ingresos_y_gastos.mapper.GroupMapper;
import com.angelbelzunce.ingresos_y_gastos.mapper.GroupMemberMapper;
import com.angelbelzunce.ingresos_y_gastos.repository.GroupMemberRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.GroupRepository;
import com.angelbelzunce.ingresos_y_gastos.repository.UserRepository;
import com.angelbelzunce.ingresos_y_gastos.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final GroupMapper groupMapper;
    private final GroupMemberMapper groupMemberMapper;

    @Override
    public List<GroupResponse> findAll() {
        return groupRepository.findAll()
                .stream()
                .map(groupMapper::toResponse)
                .toList();
    }

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
    @Transactional
    public GroupResponse create(GroupRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));

        Group group = groupMapper.toEntity(request);
        Group savedGroup = groupRepository.save(group);

        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(savedGroup);
        groupMember.setUser(user);
        groupMember.setRole(Role.ADMIN);

        groupMemberRepository.save(groupMember);

        return groupMapper.toResponse(savedGroup);
    }

    @Override
    public GroupResponse update(Long id, GroupRequest request) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException(id));

        group.setName(request.name());
        Group saved = groupRepository.save(group);
        return groupMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException(id));

        groupRepository.delete(group);
    }
}
