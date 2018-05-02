package com.simple.fb.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ROLES")
public class RoleDao {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "ID")
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "USER_ID", nullable = false)
  private UserDao user;
  @Column(name = "ROLE")
  private String role;
  @Column(name = "ACTIVE")
  private Long active = 1L;

  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public UserDao getUser() {
    return user;
  }
  public void setUser(UserDao user) {
    this.user = user;
  }
  public String getRole() {
    return role;
  }
  public void setRole(String role) {
    this.role = role;
  }
  public Long getActive() {
    return active;
  }
  public void setActive(Long active) {
    this.active = active;
  }
  
}
