package com.simple.social.security;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

@Configuration
public class GoogleIdConfig {

  @Autowired
  private OAuth2ClientContext oauth2ClientContext;
  
  @Value("${spring.google.client.clientId}")
  private String clientId;
  
  @Value("${spring.google.client.clientSecret}")
  private String clientSecret;
  
  @Value("${spring.google.client.accessTokenUri}")
  private String accessTokenUri;

  @Value("${spring.google.client.userAuthorizationUri}")
  private String userAuthorizationUri;

  @Value("${google.resource.redirectUri}")
  private String redirectUri;

  public OAuth2ProtectedResourceDetails googleOpenId() {
      AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
      details.setClientId(clientId);
      details.setClientSecret(clientSecret);
      details.setAccessTokenUri(accessTokenUri);
      details.setUserAuthorizationUri(userAuthorizationUri);
      details.setScope(Arrays.asList("openid", "profile", "email"));
      details.setPreEstablishedRedirectUri(redirectUri);
      details.setUseCurrentUri(false);
      return details;
  }

  public OAuth2RestTemplate googleOpenIdTemplate() {
      return new OAuth2RestTemplate(googleOpenId(), oauth2ClientContext);
  }
  
}
