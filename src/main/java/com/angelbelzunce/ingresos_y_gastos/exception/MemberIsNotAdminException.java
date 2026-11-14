package com.angelbelzunce.ingresos_y_gastos.exception;

public class MemberIsNotAdminException extends RuntimeException {
    public MemberIsNotAdminException(Long userId) {
        super("User with id: " + userId + " is not ADMIN");
    }
}
