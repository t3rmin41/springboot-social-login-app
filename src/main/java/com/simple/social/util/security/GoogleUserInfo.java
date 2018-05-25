package com.simple.social.util.security;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleUserInfo implements Serializable {

  private String id;
  private String email;
  @JsonProperty("verified_email")
  private boolean verifiedEmail;
  private String name;
  @JsonProperty("given_name")
  private String givenName;
  @JsonProperty("family_name")
  private String familyName;
  private String link;
  @JsonProperty("picture")
  private String pictureLink;
  private String locale;
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  //@JsonProperty("verified_email")
  public boolean isVerifiedEmail() {
    return verifiedEmail;
  }
  public void setVerifiedEmail(boolean verifiedEmail) {
    this.verifiedEmail = verifiedEmail;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  //@JsonProperty("given_name")
  public String getGivenName() {
    return givenName;
  }
  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }
  //@JsonProperty("family_name")
  public String getFamilyName() {
    return familyName;
  }
  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }
  public String getLink() {
    return link;
  }
  public void setLink(String link) {
    this.link = link;
  }
  //@JsonProperty("picture")
  public String getPictureLink() {
    return pictureLink;
  }
  public void setPictureLink(String pictureLink) {
    this.pictureLink = pictureLink;
  }
  public String getLocale() {
    return locale;
  }
  public void setLocale(String locale) {
    this.locale = locale;
  }
  
  
}
