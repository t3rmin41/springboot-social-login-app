package com.simple.social.domain;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleBean implements Serializable {

  private String code;
  private String title;

  public RoleBean setCode(String code) {
    this.code = code;
    return this;
  }
  
  public RoleBean setTitle(String title) {
    this.title = title;
    return this;
  }
  
  public String getCode() {
    return code;
  }

  public String getTitle() {
    return title;
  }
  
}
