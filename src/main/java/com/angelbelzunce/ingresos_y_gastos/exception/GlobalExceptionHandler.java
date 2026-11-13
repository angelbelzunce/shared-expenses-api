package com.angelbelzunce.ingresos_y_gastos.exception;

import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problemDetail.setTitle("Invalid request");

        problemDetail.setProperty("errors", errors);

        return ResponseEntity.badRequest().body(problemDetail);
    }

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

    @ExceptionHandler(UserNotInPaymentException.class)
    public ProblemDetail userNotInPayment(UserNotInPaymentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("User not in payment");
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
        problemDetail.setTitle("This action requires the ADMIN role");
        return problemDetail;
    }

    @ExceptionHandler(NotCreatorOrAdminException.class)
    public ProblemDetail userIsNotAdminOrCreator(NotCreatorOrAdminException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("User is not the creator or an ADMIN");
        return problemDetail;
    }

    @ExceptionHandler(CreatorInShareUsersException.class)
    public ProblemDetail creatorInShareUsers(CreatorInShareUsersException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("The creator cannot be included in share users");
        return problemDetail;
    }

    @ExceptionHandler(DuplicatedShareUserException.class)
    public ProblemDetail duplicateUserInPaymentShare(DuplicatedShareUserException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Duplicate user in payment share");
        return problemDetail;
    }

    @ExceptionHandler(PaymentShareNotFoundException.class)
    public ProblemDetail paymentShareNotFound(PaymentShareNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Payment share not found");
        return problemDetail;
    }

    @ExceptionHandler(IsNotShareCreatorException.class)
    public ProblemDetail notShareCreator(IsNotShareCreatorException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("Not creator user");
        return problemDetail;
    }

    @ExceptionHandler(IsNotShareOwnerException.class)
    public ProblemDetail notShareOwner(IsNotShareOwnerException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("Not owner user");
        return problemDetail;
    }

    @ExceptionHandler(NotPendingShareException.class)
    public ProblemDetail notPendingShare(NotPendingShareException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Share status is not pending");
        return problemDetail;
    }

    @ExceptionHandler(PaymentWithNotPendingSharesException.class)
    public ProblemDetail paymentWithNotPendingShares(PaymentWithNotPendingSharesException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Payment with not pending shares");
        return problemDetail;
    }

    @ExceptionHandler(UserCannotUndoTheirShare.class)
    public ProblemDetail cannotUndoTheirShare(UserCannotUndoTheirShare ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("The creator cannot undo their own confirmed share");
        return problemDetail;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail invalidUserOrPassword(BadCredentialsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problemDetail.setTitle("Invalid user or password");
        return problemDetail;
    }

    @ExceptionHandler(AdminCannotLeaveGroupException.class)
    public ProblemDetail adminCannotLeaveGroup(AdminCannotLeaveGroupException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("Admin cannot leave the group");
        return problemDetail;
    }

    @ExceptionHandler(CreatorCannotLeavePaymentException.class)
    public ProblemDetail creatorCannotLeaveTheirOwnPayment(CreatorCannotLeavePaymentException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("Creator cannot leave their own payment");
        return problemDetail;

    }
}
