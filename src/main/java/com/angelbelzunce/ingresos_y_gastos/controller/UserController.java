package com.angelbelzunce.ingresos_y_gastos.controller;

import com.angelbelzunce.ingresos_y_gastos.dto.*;
import com.angelbelzunce.ingresos_y_gastos.service.GroupService;
import com.angelbelzunce.ingresos_y_gastos.service.PaymentShareService;
import com.angelbelzunce.ingresos_y_gastos.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final GroupService groupService;
    private final PaymentShareService shareService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/me/shares")
    public ResponseEntity<List<UserShareResponse>> findUserShares(@AuthenticationPrincipal Long userId){
        return ResponseEntity.ok(shareService.findSharesByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @GetMapping("/{id}/groups")
    public ResponseEntity<List<GroupResponse>> findGroups(@PathVariable Long id){
        return ResponseEntity.ok(groupService.findGroupsByUserId(id));
    }
}
