package com.simple.social.enums;

public enum RoleType {

  CUSTOMER("Customer"),
  MANAGER("Manager"),
  ADMIN("Admin");
  
  private String title;
  
  private RoleType(String title) {
    this.title = title;
  }
  
  public String getTitle() {
    return title;
  }
  
  public static String getRoleTitleByType(RoleType type) {
    for (RoleType current : RoleType.values()) {
      if (type.equals(current)) {
        return current.title;
      }
    }
    return null;
  }
  
  public static String getRoleTitleByCode(String code) {
    for (RoleType current : RoleType.values()) {
      if (current.name().equals(code)) {
        return current.title;
      }
    }
    return null;
  }

  public static RoleType getRoleTypeByTitle(String typeName) {
    for (RoleType current : RoleType.values()) {
      if (current.title.equals(typeName)) {
        return current;
      }
    }
    return null;
  }
  
  public static RoleType getRoleTypeByCode(String code) {
    for (RoleType current : RoleType.values()) {
      if (current.name().equals(code)) {
        return current;
      }
    }
    return null;
  }
  
}
