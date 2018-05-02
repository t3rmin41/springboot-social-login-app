package com.simple.fb.jpa;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "USERS")
public class UserDao {

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  @Column(name = "ID")
  private Long id;
  @Column(name = "FIRST_NAME")
  private String firstName;
  @Column(name = "LAST_NAME")
  private String lastName;
  @Column(name = "EMAIL")
  private String email;
  @Column(name = "PASSWORD")
  private String password;
  //@OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL, mappedBy="user") // for using custom userDetails implementation - need to fetch roles eagerly 
  @OneToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="user")
  private Set<RoleDao> roles = new HashSet<RoleDao>();
  @Column(name = "ENABLED")
  private boolean enabled;

  public Long getId() {
      return id;
  }
  public void setId(Long id) {
      this.id = id;
  }
  public String getFirstName() {
    return firstName;
  }
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  public String getLastName() {
    return lastName;
  }
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getPassword() {
      return password;
  }
  public void setPassword(String password) {
      this.password = password;
  }
  public Set<RoleDao> getRoles() {
      return roles;
  }
  public void setRoles(Set<RoleDao> roles) {
      this.roles = roles;
  }
  public boolean getEnabled() {
      return enabled;
  }
  public void setEnabled(boolean enabled) {
      this.enabled = enabled;
  }
  
}
