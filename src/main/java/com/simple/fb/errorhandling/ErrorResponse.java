package com.simple.fb.errorhandling;

import java.util.LinkedList;
import java.util.List;

public class ErrorResponse {

  private String errorMessage;
  private final List<ErrorField> errors = new LinkedList<ErrorField>();
  
  public ErrorResponse(String message, List<ErrorField> errors) {
    this.errorMessage = message;
    this.errors.addAll(errors);
  }
  
  public String getErrorMessage() {
    return errorMessage;
  }
  public ErrorResponse setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }
  public List<ErrorField> getErrors() {
    return errors;
  }
  
}
