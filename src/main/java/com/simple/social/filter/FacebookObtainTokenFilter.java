package com.simple.social.filter;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import com.simple.social.jms.SessionQueueSender;
import com.simple.social.util.security.NoopAuthenticationManager;

public class FacebookObtainTokenFilter extends AbstractAuthenticationProcessingFilter {

  private static Logger logger = LoggerFactory.getLogger(FacebookObtainTokenFilter.class);
  
  private OAuth2RestTemplate restTemplate;

  @Autowired
  private SessionQueueSender sessionQueueSender;
  
  public FacebookObtainTokenFilter(String defaultFilterProcessesUrl) {
    super(defaultFilterProcessesUrl);
    setAuthenticationManager(new NoopAuthenticationManager());
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
  throws AuthenticationException, IOException, ServletException {
    //logger.info("FacebookObtainTokenFilter : attemptAuthentication");
    sessionQueueSender.sendMessageToQueue(request.getSession().getId());
    //trying to obtain token initiates redirect to Facebook
    OAuth2AccessToken accessToken = null;
    try {
        accessToken = restTemplate.getAccessToken();
    } catch (final OAuth2Exception e) {
        throw new BadCredentialsException("Could not obtain access token", e);
    }
    return null;
  }
  
  public void setRestTemplate(OAuth2RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

}
