package com.simple.social.filter;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.simple.social.jms.SessionQueueSender;
import com.simple.social.security.FacebookConfig;
import com.simple.social.util.security.FacebookIdUserDetails;
import com.simple.social.util.security.NoopAuthenticationManager;

public class FacebookObtainTokenFilter extends AbstractAuthenticationProcessingFilter {

  private static Logger logger = LoggerFactory.getLogger(FacebookObtainTokenFilter.class);
  
  private static FacebookConfig facebookConfig;

  private static UserInfoTokenServices tokenService;
  private static AuthorizationCodeAccessTokenProvider accessTokenProvider = new AuthorizationCodeAccessTokenProvider();

  private static final String FACEBOOK_FIELDS = "?fields=id,email,first_name,last_name,picture";

  @Inject
  private SessionQueueSender sessionQueueSender;
  
  private final ReentrantLock lock = new ReentrantLock();
  
  private OAuth2AccessToken accessToken = null;

  public FacebookObtainTokenFilter(String defaultFilterProcessesUrl, FacebookConfig config) {
    super(defaultFilterProcessesUrl);
    facebookConfig = config;
    tokenService = new UserInfoTokenServices(config.getResourceProperties().getUserInfoUri(), config.getResourceDetails().getClientId());
    accessTokenProvider.setStateMandatory(false);
    setAuthenticationManager(new NoopAuthenticationManager());
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
  throws AuthenticationException, IOException, ServletException {
    //logger.info("FacebookObtainTokenFilter : attemptAuthentication");
    OAuth2AccessToken accessToken = null;
    this.lock.lock();
    //trying to obtain token redirects to Facebook login form via UserRedirectRequiredException
    try {
      String code = request.getParameter("code");
      sessionQueueSender.sendMessageToQueue(request.getSession().getId());
      if (null == this.accessToken || request.getSession().getId() != this.accessToken.getAdditionalInformation().get("sessionId")) {
        AccessTokenRequest accessTokenRequest = new DefaultAccessTokenRequest();
        accessTokenRequest.setAuthorizationCode(code);
        accessTokenRequest.setCurrentUri(facebookConfig.getResourceDetails().getPreEstablishedRedirectUri());
        accessToken = accessTokenProvider.obtainAccessToken(facebookConfig.getResourceDetails(), accessTokenRequest);
        accessToken.getAdditionalInformation().put("sessionId", request.getSession().getId());
      }
    } catch (OAuth2Exception e) {
      throw new BadCredentialsException("Could not obtain access token", e);
    } finally {
      this.lock.unlock();
    }
    //return nullable UsernamePasswordAuthenticationToken as ObtainToken filter is needed only for UserRedirectRequiredException
    return new UsernamePasswordAuthenticationToken(new FacebookIdUserDetails(null, null), null, null);
  }

}
