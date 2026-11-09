package com.angelbelzunce.ingresos_y_gastos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    public ProblemDetail paymentNotFound(PaymentNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Payment not found");
        return problemDetail;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail userNotFound(UserNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("User not found");
        return problemDetail;
    }

    @ExceptionHandler(GroupNotFoundException.class)
    public ProblemDetail groupNotFound(GroupNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Group not found");
        return problemDetail;
    }

    @ExceptionHandler(UserNotInGroupException.class)
    public ProblemDetail userNotInGroup(UserNotInGroupException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("User not in group");
        return problemDetail;
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ProblemDetail emailAlreadyExists(EmailAlreadyExistsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Email already exists");
        return problemDetail;
    }

    @ExceptionHandler(InvitationNotFoundException.class)
    public ProblemDetail invitationNotFound(InvitationNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Invitation not found");
        return problemDetail;
    }

    @ExceptionHandler(NotInvitedUserException.class)
    public ProblemDetail notInvitedUser(NotInvitedUserException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("Not invited user");
        return problemDetail;

    }

    @ExceptionHandler(InvitationNotPendingException.class)
    public ProblemDetail invitationNotPending(InvitationNotPendingException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Invitation not pending");
        return problemDetail;
    }

    @ExceptionHandler(InvitationAlreadyExistsException.class)
    public ProblemDetail invitationAlreadyExists(InvitationAlreadyExistsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Invitation already exists");
        return problemDetail;
    }

    @ExceptionHandler(InvitedUserAlreadyMemberException.class)
    public ProblemDetail invitedUserAlreadyMember(InvitedUserAlreadyMemberException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Invited user is already a member");
        return problemDetail;
    }

    @ExceptionHandler(UserIsNotAdminException.class)
    public ProblemDetail userIsNotAdmin(UserIsNotAdminException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("User needs ADMIN role to invite");
        return problemDetail;
    }

    @ExceptionHandler(NotCreatorOrAdminException.class)
    public ProblemDetail userIsNotAdminOrCreator(NotCreatorOrAdminException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("User is not the creator or an ADMIN");
        return problemDetail;
    }


}
