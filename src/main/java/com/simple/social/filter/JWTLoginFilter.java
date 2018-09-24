package com.simple.social.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.simple.social.app.ApplicationContextProvider;
import com.simple.social.service.TokenAuthenticationService;
import com.simple.social.util.security.UserCredentials;
import com.simple.social.util.security.UserNotAllowedException;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

  private static long EXPIRATIONTIME = 86400_000; // 1 day
  
  public JWTLoginFilter(String url, AuthenticationManager authManager) {
    super(new AntPathRequestMatcher(url));
    setServletContext(ApplicationContextProvider.getApplicationContext().getBean(ServletContext.class));
    setAuthenticationManager(authManager);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse response)
  throws AuthenticationException, IOException, ServletException {
    UserCredentials creds = null;
    try {
      creds = new ObjectMapper().readValue(req.getInputStream(), UserCredentials.class);
    } catch (MismatchedInputException e) {
      throw new UserNotAllowedException("User or token not found");
    }
    return getAuthenticationManager().authenticate(
        new UsernamePasswordAuthenticationToken(
            creds.getEmail(),
            creds.getPassword(),
            Collections.emptyList()
        )
    );
  }
  
  @Override
  protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth)
  throws IOException, ServletException {
    TokenAuthenticationService tokenService = ApplicationContextProvider.getApplicationContext().getBean(TokenAuthenticationService.class);
    tokenService.addAuthentication(res, auth.getName(), auth.getAuthorities(), new Date(System.currentTimeMillis() + EXPIRATIONTIME));
    invalidateIrrelevantSessionAttributes(req.getSession());
  }
  
  private void invalidateIrrelevantSessionAttributes(HttpSession session) {
    session.setAttribute("googleAccessToken", null);
    session.setAttribute("fbAccessToken", null);
  }
  
}
