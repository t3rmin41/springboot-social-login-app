package com.simple.social.security;

@SuppressWarnings("serial")
public class UserNotFoundException extends Exception {

  private String message;
  
  public UserNotFoundException(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
  
}
