package com.simple.fb.security;

@SuppressWarnings("serial")
public class UserNotAllowedException extends RuntimeException {

  private String path;
  private String error;

  public UserNotAllowedException(String message) {
    super(message);
    this.error = message;
  }
  
  public UserNotAllowedException(String path, String message) {
    super(message);
    this.error = message;
  }
  
  public UserNotAllowedException(String path, String message, Throwable cause) {
    super(message, cause);
    this.error = message;
  }
  
  public UserNotAllowedException(String path, Throwable cause) {
    super(cause);
    this.path = path;
  }

  public UserNotAllowedException(String path, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.error = message;
  }

  public String getPath() {
    return path;
  }
  
  public UserNotAllowedException setPath(String path) {
    this.path = path;
    return this;
  }

  public String getError() {
    return error;
  }
  
}
