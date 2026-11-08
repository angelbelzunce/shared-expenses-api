package com.angelbelzunce.ingresos_y_gastos.mapper;

import com.angelbelzunce.ingresos_y_gastos.dto.GroupRequest;
import com.angelbelzunce.ingresos_y_gastos.dto.GroupResponse;
import com.angelbelzunce.ingresos_y_gastos.entity.Group;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper {

    public Group toEntity(GroupRequest request){
        Group group = new Group();
        group.setName(request.name());
        return group;
    }

    public GroupResponse toResponse(Group group){
        return new GroupResponse(
                group.getId(),
                group.getName());
    }
}
