package com.simple.social.errorhandling;

public class ErrorField {

  private String field;
  private String errorMessage;

  public ErrorField(String field, String message) {
    this.field = field;
    this.errorMessage = message;
  }
  
  public String getField() {
    return field;
  }
  public ErrorField setField(String field) {
    this.field = field;
    return this;
  }
  public String getErrorMessage() {
    return errorMessage;
  }
  public ErrorField setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }
  
}
