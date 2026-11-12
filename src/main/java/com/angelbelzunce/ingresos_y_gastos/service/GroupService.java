package com.angelbelzunce.ingresos_y_gastos.service;

import com.angelbelzunce.ingresos_y_gastos.dto.GroupMemberResponse;
import com.angelbelzunce.ingresos_y_gastos.dto.GroupRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.GroupResponse;

import java.util.List;

public interface GroupService {

    GroupResponse findById(Long id);
    List<GroupMemberResponse> findMembers(Long groupId);
    List<GroupResponse> findGroups(Long userId);
    GroupResponse create(GroupRequest request, Long userId);
    GroupResponse update(Long id, GroupRequest request, Long userId);
    void delete(Long id, Long userId);
}
