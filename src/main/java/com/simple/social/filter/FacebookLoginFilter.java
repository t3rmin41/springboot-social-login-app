package com.simple.social.filter;

import java.io.IOException;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.simple.social.ApplicationContextProvider;
import com.simple.social.service.TokenAuthenticationService;
import com.simple.social.service.UserService;
import com.simple.social.util.security.NoopAuthenticationManager;

public class FacebookLoginFilter extends AbstractAuthenticationProcessingFilter {

  //https://github.com/ozgengunay/FBSpringSocialRESTAuth/blob/master/server/src/main/java/com/ozgen/server/security/oauth/FacebookTokenAuthenticationFilter.java
  
  private static final Logger logger = LoggerFactory.getLogger(FacebookLoginFilter.class);
  
  private OAuth2RestTemplate restTemplate;
  
  @Autowired
  private UserService userService;

  @Autowired
  private JmsTemplate jmsTemplate;
  
  @Value("${spring.facebook.client.appId}")
  private String appId;

  @Value("${spring.facebook.client.appSecret}")
  private String appSecret;

  @Value("${spring.facebook.client.appAccessToken}")
  private String appAccessToken;
  
  @Value("${spring.facebook.resource.userInfoUri}")
  private String userInfoUri;
  
  public FacebookLoginFilter(String url) {
    super(new AntPathRequestMatcher(url));
    setAuthenticationManager(new NoopAuthenticationManager());
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
  throws AuthenticationException, IOException, ServletException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth)
  throws IOException, ServletException {
    TokenAuthenticationService tokenService = ApplicationContextProvider.getApplicationContext().getBean(TokenAuthenticationService.class);
    tokenService.addAuthentication(res, auth.getName(), auth.getAuthorities(), new Date());
  }
  
  public void setRestTemplate(OAuth2RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
  
}
