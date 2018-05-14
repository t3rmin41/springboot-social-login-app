package com.simple.fb.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.simple.fb.app.ApplicationContextProvider;

public class FacebookLoginFilter extends AbstractAuthenticationProcessingFilter {

  //https://github.com/ozgengunay/FBSpringSocialRESTAuth/blob/master/server/src/main/java/com/ozgen/server/security/oauth/FacebookTokenAuthenticationFilter.java
  
  protected FacebookLoginFilter(String url, AuthenticationManager authManager) {
    super(new AntPathRequestMatcher(url));
    setAuthenticationManager(authManager);
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
    tokenService.addAuthentication(res, auth.getName(), auth.getAuthorities());
  }
  
}
