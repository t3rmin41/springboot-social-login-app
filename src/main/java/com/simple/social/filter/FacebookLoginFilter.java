package com.simple.social.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import javax.inject.Inject;
import javax.jms.Message;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.social.ApplicationContextProvider;
import com.simple.social.domain.RoleBean;
import com.simple.social.domain.UserBean;
import com.simple.social.enums.RoleType;
import com.simple.social.enums.UserType;
import com.simple.social.security.FacebookConfig;
import com.simple.social.service.TokenAuthenticationService;
import com.simple.social.service.UserService;
import com.simple.social.util.security.FacebookIdUserDetails;
import com.simple.social.util.security.NoopAuthenticationManager;

public class FacebookLoginFilter extends AbstractAuthenticationProcessingFilter {

  private static final Logger logger = LoggerFactory.getLogger(FacebookLoginFilter.class);
  
  private static FacebookConfig facebookConfig;

  private static UserInfoTokenServices userInfoTokenService;
  private static AuthorizationCodeAccessTokenProvider accessTokenProvider = new AuthorizationCodeAccessTokenProvider();
  
  @Inject
  private RestTemplate restTemplate;

  @Inject
  private UserService userService;

  @Inject
  private JmsTemplate jmsTemplate;
  
  private final ReentrantLock lock = new ReentrantLock();

  private OAuth2AccessToken accessToken = null;

  private static final String FACEBOOK_FIELDS = "?fields=id,email,first_name,last_name,picture";
  
  public FacebookLoginFilter(String url, FacebookConfig config) {
    super(new AntPathRequestMatcher(url));
    facebookConfig = config;
    userInfoTokenService = new UserInfoTokenServices(config.getResourceProperties().getUserInfoUri(), config.getResourceDetails().getClientId());
    accessTokenProvider.setStateMandatory(false);
    setAuthenticationManager(new NoopAuthenticationManager());
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
  throws AuthenticationException, IOException, ServletException {
    //logger.info("FacebookLoginFilter : attemptAuthentication");
    this.lock.lock();
    try {
      OAuth2AccessToken accessToken = null;
      try {
        String code = request.getParameter("code");
        if (null == this.accessToken || request.getSession().getId() != this.accessToken.getAdditionalInformation().get("sessionId")) {
          AccessTokenRequest accessTokenRequest = new DefaultAccessTokenRequest();
          accessTokenRequest.setAuthorizationCode(code);
          accessTokenRequest.setCurrentUri(facebookConfig.getResourceDetails().getPreEstablishedRedirectUri());
          accessToken = accessTokenProvider.obtainAccessToken(facebookConfig.getResourceDetails(), accessTokenRequest);
          accessToken.getAdditionalInformation().put("sessionId", request.getSession().getId());
        }
      } catch (OAuth2Exception | UserRedirectRequiredException e) {
        setFacebookLoginRequiredHeader(response);
        throw new BadCredentialsException("Could not obtain access token", e);
      }
      try {
        if (null == this.accessToken || request.getSession().getId() != this.accessToken.getAdditionalInformation().get("sessionId")) {
          this.accessToken = accessToken;
          userInfoTokenService.loadAuthentication(accessToken.getValue());
        }
      } catch (final OAuth2Exception e) {
        setFacebookLoginRequiredHeader(response);
        response.sendRedirect("/");
        throw new BadCredentialsException("Could not obtain access token", e);
      } catch (final  UserRedirectRequiredException e) {
        setFacebookLoginRequiredHeader(response);
      }
      try {
        String json = restTemplate.getForObject(facebookConfig.getResourceProperties().getUserInfoUri()+FACEBOOK_FIELDS+"&access_token="+this.accessToken.getValue(), String.class);
        final Map<String, String> authInfo = new ObjectMapper().readValue(json, Map.class);
        final FacebookIdUserDetails fbUser = new FacebookIdUserDetails(authInfo, this.accessToken);
        return new UsernamePasswordAuthenticationToken(fbUser, null, fbUser.getAuthorities());
      } catch (final Exception e) {
        setFacebookLoginRequiredHeader(response);
        throw new BadCredentialsException("Could not obtain user details from token", e);
      }
    } finally {
      this.lock.unlock();
    }

  }

  @Override
  protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth)
  throws IOException, ServletException {
    Message message = jmsTemplate.receiveSelected("JMSCorrelationID = '"+req.getSession().getId()+"'");
    TokenAuthenticationService tokenService = ApplicationContextProvider.getApplicationContext().getBean(TokenAuthenticationService.class);
    FacebookIdUserDetails facebookUserDetails = (FacebookIdUserDetails) auth.getPrincipal();
    Collection<GrantedAuthority> authorities = new LinkedList<GrantedAuthority>();
    UserBean userBean = userService.getUserByEmailAndType(facebookUserDetails.getEmail(), UserType.FB);
    if (null == userBean) {
      UserBean newUserBean = new UserBean().setFirstName(facebookUserDetails.getFirstName())
                                           .setLastName(facebookUserDetails.getLastName())
                                           .setEmail(facebookUserDetails.getEmail())
                                           .setPassword("123").setEnabled(true);
      List<RoleBean> roles = new LinkedList<RoleBean>();
      roles.add(new RoleBean().setCode(RoleType.CUSTOMER.toString()).setTitle(RoleType.CUSTOMER.getTitle()));
      authorities.add(new SimpleGrantedAuthority("ROLE_"+RoleType.CUSTOMER.toString()));
      newUserBean.getRoles().addAll(roles);
      userService.saveUserFromSocial(newUserBean, UserType.FB);
    } else {
      userBean.getRoles().stream().forEach(r -> {
        authorities.add(new SimpleGrantedAuthority("ROLE_"+r.getCode()));
      });
    }
    //logger.info("FacebookLoginFilter : successfulAuthentication");
    tokenService.addAuthentication(res, facebookUserDetails.getEmail(), authorities, facebookUserDetails.getToken().getExpiration());
    if (null != message) {
      res.sendRedirect("/");
    }
  }

  private void setFacebookLoginRequiredHeader(HttpServletResponse response) {
    if (null == response.getHeader("FacebookLoginRequired") || !"true".equals(response.getHeader("FacebookLoginRequired"))) {
      response.addHeader("FacebookLoginRequired", "true");
    }
  }
  
}
