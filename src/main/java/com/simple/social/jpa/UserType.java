package com.simple.social.jpa;

public enum UserType {

  APP("Application"),
  FB("Facebook"),
  GOOGLE("Google");
  
  private String title;
  
  private UserType(String title) {
    this.title = title;
  }
  
  public String getTitle() {
    return this.title;
  }
  
  public UserType getTypeByTitle(String title) {
    for (UserType type : UserType.values()) {
      if (type.getTitle().equals(title)) {
        return type;
      }
    }
    return null;
  }
}
