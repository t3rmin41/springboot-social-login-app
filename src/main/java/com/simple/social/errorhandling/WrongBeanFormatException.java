package com.simple.social.errorhandling;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("serial")
public class WrongBeanFormatException extends RuntimeException {

  private final List<ErrorField> errors = new LinkedList<ErrorField>();

  public WrongBeanFormatException(String message, List<ErrorField> errors) {
    super(message);
    this.errors.addAll(errors);
  }
  
  public WrongBeanFormatException(String message, Throwable cause, List<ErrorField> errors) {
    super(message, cause);
    this.errors.addAll(errors);
  }
  
  public WrongBeanFormatException(Throwable cause, List<ErrorField> errors) {
    super(cause);
    this.errors.addAll(errors);
  }
  
  public WrongBeanFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<ErrorField> errors) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.errors.addAll(errors);
  }
  
  public List<ErrorField> getErrors() {
    return errors;
  }
  
}
