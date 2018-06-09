package com.simple.social.util.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

public class GoogleIdUserDetails implements UserDetails {

  private String userId;
  private String username;
  private String firstName;
  private String lastName;
  private String email;
  private OAuth2AccessToken token;

  public GoogleIdUserDetails(Map<String, String> userInfo, OAuth2AccessToken token) {
      this.userId = userInfo.get("sub");
      this.username = userInfo.get("email");
      this.email = userInfo.get("email");
      this.token = token;
  }

  public String getUserId() {
      return userId;
  }

  public void setUserId(String userId) {
      this.userId = userId;
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

  public OAuth2AccessToken getToken() {
    return token;
  }

  public void setToken(OAuth2AccessToken token) {
    this.token = token;
  }

  @Override
  public String getUsername() {
      return username;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
      return Arrays.asList(new SimpleGrantedAuthority("CUSTOMER"));
  }

  @Override
  public String getPassword() {
      return null;
  }

  @Override
  public boolean isAccountNonExpired() {
      return true;
  }

  @Override
  public boolean isAccountNonLocked() {
      return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
      return true;
  }

  @Override
  public boolean isEnabled() {
      return true;
  }

}
