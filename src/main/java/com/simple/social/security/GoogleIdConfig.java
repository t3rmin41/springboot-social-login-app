package com.simple.social.security;

import java.util.Arrays;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

@Configuration
public class GoogleIdConfig {

  private AuthorizationCodeResourceDetails resourceDetails;
  private ResourceServerProperties resourceProperties;
  
  @Value("${spring.google.client.clientId}")
  private String clientId;
  
  @Value("${spring.google.client.clientSecret}")
  private String clientSecret;
  
  @Value("${spring.google.client.accessTokenUri}")
  private String accessTokenUri;

  @Value("${spring.google.client.userAuthorizationUri}")
  private String userAuthorizationUri;

  @Value("${spring.google.resource.issuer}")
  private String issuer;

  @Value("${spring.google.resource.jwkUrl}")
  private String jwkUrl;
  
  @Value("${spring.google.resource.userInfoUri}")
  private String userInfoUri;
  
  @Value("${google.resource.redirectUri}")
  private String redirectUri;

  @PostConstruct
  public void initGoogleIdConfig() {
      AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
      details.setClientId(clientId);
      details.setClientSecret(clientSecret);
      details.setAccessTokenUri(accessTokenUri);
      details.setUserAuthorizationUri(userAuthorizationUri);
      details.setScope(Arrays.asList("openid", "profile", "email"));
      details.setPreEstablishedRedirectUri(redirectUri);
      details.setUseCurrentUri(false);
      ResourceServerProperties properties = new ResourceServerProperties();
      properties.setUserInfoUri(userInfoUri);
      resourceDetails = details;
      resourceProperties = properties;
  }

  public AuthorizationCodeResourceDetails getResourceDetails() {
    return this.resourceDetails;
  }

  public ResourceServerProperties getResourceProperties() {
    return this.resourceProperties;
  }

}
