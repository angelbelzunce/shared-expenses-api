package com.angelbelzunce.ingresos_y_gastos.exception;

public class UserNotInGroupException extends RuntimeException {
    public UserNotInGroupException(Long userId, Long groupId) {
        super("User with id " + userId + " is not a member of group with id " + groupId);
    }
}
