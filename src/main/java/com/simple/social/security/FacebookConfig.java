package com.simple.social.security;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

@Configuration
public class FacebookConfig {

  @Autowired
  private OAuth2ClientContext oauth2ClientContext;
  
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

  @Value("${facebook.resource.redirectUri}")
  private String redirectUri;

  public OAuth2ProtectedResourceDetails facebookConfig() {
    AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
    details.setClientId(appId);
    details.setClientSecret(appSecret);
    details.setAccessTokenUri(accessTokenUri);
    details.setUserAuthorizationUri(userAuthorizationUri);
    details.setScope(Arrays.asList("email", "public_profile"));
    details.setPreEstablishedRedirectUri(redirectUri);
    details.setUseCurrentUri(false);
    return details;
  }

//  public OAuth2RestTemplate facebookTemplate() {
//    return new OAuth2RestTemplate(facebookConfig(), oauth2ClientContext);
//  }
  
}
