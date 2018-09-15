package com.simple.social.filter;

import java.util.concurrent.locks.ReentrantLock;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.simple.social.jms.SessionQueueSender;
import com.simple.social.security.GoogleIdConfig;
import com.simple.social.util.security.GoogleIdUserDetails;
import com.simple.social.util.security.NoopAuthenticationManager;

public class GoogleObtainTokenFilter extends AbstractAuthenticationProcessingFilter {

  private static Logger logger = LoggerFactory.getLogger(GoogleObtainTokenFilter.class);

  private static GoogleIdConfig config;
  
  private static UserInfoTokenServices userInfoTokenService;
  private static AuthorizationCodeAccessTokenProvider accessTokenProvider = new AuthorizationCodeAccessTokenProvider();

  private static final String GOOGLE_REQUEST = "?alt=json";
  
  @Value("${spring.google.client.clientId}")
  private String clientId;

  @Value("${spring.google.resource.issuer}")
  private String issuer;

  @Value("${spring.google.resource.jwkUrl}")
  private String jwkUrl;
  
  @Value("${spring.google.resource.userInfoUri}")
  private String userInfoUri;

  @Inject
  private SessionQueueSender sessionQueueSender;

  public GoogleObtainTokenFilter(String url, GoogleIdConfig googleIdConfig) {
    super(new AntPathRequestMatcher(url));
    config = googleIdConfig;
    userInfoTokenService = new UserInfoTokenServices(config.getResourceProperties().getUserInfoUri(), config.getResourceDetails().getClientId());
    accessTokenProvider.setStateMandatory(false);
    setAuthenticationManager(new NoopAuthenticationManager());
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
  throws AuthenticationException, IOException, ServletException {
    //logger.info("GoogleObtainTokenFilter : attemptAuthentication");
    //trying to obtain token redirects to Google login form via UserRedirectRequiredException
    try {
      OAuth2AccessToken googleAccessToken = null;
      googleAccessToken = (OAuth2AccessToken) request.getSession().getAttribute("googleAccessToken");
      String code = request.getParameter("code");
        if (null == googleAccessToken) {
          sessionQueueSender.sendMessageToQueue(request.getSession().getId());
          AccessTokenRequest accessTokenRequest = new DefaultAccessTokenRequest();
          accessTokenRequest.setAuthorizationCode(code);
          accessTokenRequest.setCurrentUri(config.getResourceDetails().getPreEstablishedRedirectUri());
          googleAccessToken = accessTokenProvider.obtainAccessToken(config.getResourceDetails(), accessTokenRequest);
          googleAccessToken.getAdditionalInformation().put("sessionId", request.getSession().getId());
          request.getSession().setAttribute("googleAccessToken", googleAccessToken);
        }
    } catch (final OAuth2Exception e) {
        throw new BadCredentialsException("Could not obtain access token", e);
    } 
    //return nullable UsernamePasswordAuthenticationToken as ObtainToken filter is needed only for UserRedirectRequiredException
    return new UsernamePasswordAuthenticationToken(new GoogleIdUserDetails(null, null), null, null);
  }

}
