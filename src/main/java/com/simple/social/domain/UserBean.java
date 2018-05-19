package com.simple.social.domain;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserBean implements Serializable {

  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private List<RoleBean> roles = new LinkedList<RoleBean>();
  private boolean enabled;
  private String type;

  public Long getId() {
      return id;
  }
  public UserBean setId(Long id) {
      this.id = id;
      return this;
  }
  public String getFirstName() {
    return firstName;
  }
  public UserBean setFirstName(String firstName) {
    this.firstName = firstName;
    return this;
  }
  public String getLastName() {
    return lastName;
  }
  public UserBean setLastName(String lastName) {
    this.lastName = lastName;
    return this;
  }
  public String getEmail() {
    return email;
  }
  public UserBean setEmail(String email) {
    this.email = email;
    return this;
  }
  public String getPassword() {
      return password;
  }
  public UserBean setPassword(String password) {
      this.password = password;
      return this;
  }
  public List<RoleBean> getRoles() {
      return roles;
  }
  public UserBean setRoles(List<RoleBean> roles) {
      this.roles = roles;
      return this;
  }
  public boolean isEnabled() {
      return enabled;
  }
  public UserBean setEnabled(boolean enabled) {
      this.enabled = enabled;
      return this;
  }
  public String getType() {
    return type;
  }
  public UserBean setType(String type) {
    this.type = type;
    return this;
  }
  public List<String> getRolesAsStrings() {
    List<String> roleStrings = new LinkedList<String>();
    roles.stream().forEach(r -> {
      roleStrings.add(r.getCode());
    });
    return roleStrings;
  }
  
}