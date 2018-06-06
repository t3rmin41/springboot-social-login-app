package com.simple.social.security;

import java.util.Arrays;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

public class FacebookConfig {

  private AuthorizationCodeResourceDetails resourceDetails;
  private ResourceServerProperties resourceProperties;
  
  @Value("${spring.facebook.client.appId}")
  private String appId;
  
  @Value("${spring.facebook.client.appSecret}")
  private String appSecret;
  
  @Value("${spring.facebook.client.appAccessToken}")
  private String appAccessToken;
  
  @Value("${spring.facebook.client.accessTokenUri}")
  private String accessTokenUri;

  @Value("${spring.facebook.client.userAuthorizationUri}")
  private String userAuthorizationUri;

  @Value("${spring.facebook.resource.userInfoUri}")
  private String userInfoUri;
  
  @Value("${facebook.resource.redirectUri}")
  private String redirectUri;

  @PostConstruct
  public void initFacebookConfig() {
    AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
    details.setClientId(appId);
    details.setClientSecret(appSecret);
    details.setAccessTokenUri(accessTokenUri);
    details.setUserAuthorizationUri(userAuthorizationUri);
    details.setScope(Arrays.asList("email", "public_profile"));
    details.setPreEstablishedRedirectUri(redirectUri);
    details.setUseCurrentUri(false);
    ResourceServerProperties properties = new ResourceServerProperties();
    properties.setUserInfoUri(userInfoUri);
    resourceDetails = details;
    resourceProperties = properties;
  }

  public AuthorizationCodeResourceDetails getResourceDetails() {
    return resourceDetails;
  }

  public ResourceServerProperties getResourceProperties() {
    return resourceProperties;
  }

}
