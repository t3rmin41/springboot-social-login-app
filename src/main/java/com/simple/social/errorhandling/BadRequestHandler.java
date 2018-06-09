package com.simple.social.errorhandling;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.simple.social.util.security.UserNotAllowedException;

@ControllerAdvice
public class BadRequestHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {WrongBeanFormatException.class})
  protected ResponseEntity<ErrorResponse> handleWrongBeanFormat(RuntimeException ex, WebRequest request) {
      WrongBeanFormatException exception = null;
      HttpHeaders headers = new HttpHeaders();
      headers.add("Content-Type", "application/json;UTF-8");
      try {
        exception = (WrongBeanFormatException) ex;
      } catch (ClassCastException e) {
        throw new RuntimeException(e);
      }
      return new ResponseEntity<ErrorResponse>(new ErrorResponse(exception.getMessage(), exception.getErrors()), headers, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = {UserNotAllowedException.class})
  protected ResponseEntity<UserNotAllowedException> handleUserNotAllowed(RuntimeException ex, WebRequest request) {
    UserNotAllowedException exception = null;
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json;UTF-8");
    try {
      exception = (UserNotAllowedException) ex;
    } catch (ClassCastException e) {
      throw new RuntimeException(e);
    }
    return new ResponseEntity<UserNotAllowedException>(new UserNotAllowedException(exception.getError()).setPath(exception.getPath()), headers, HttpStatus.BAD_REQUEST);
  }
  
}
