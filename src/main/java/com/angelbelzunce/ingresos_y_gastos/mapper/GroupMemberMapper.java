package com.angelbelzunce.ingresos_y_gastos.mapper;

import com.angelbelzunce.ingresos_y_gastos.dto.GroupMemberResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.GroupMember;
import org.springframework.stereotype.Component;

@Component
public class GroupMemberMapper {

    public GroupMemberResponse toResponse(GroupMember groupMember){
        return new GroupMemberResponse(
                groupMember.getUser().getId(),
                groupMember.getUser().getName(),
                groupMember.getRole(),
                groupMember.getJoinedAt());
    }
}
