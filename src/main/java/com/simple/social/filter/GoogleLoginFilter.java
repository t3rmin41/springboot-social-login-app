package com.simple.social.filter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Collection;
import java.util.Date;
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
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
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
import com.simple.social.app.ApplicationContextProvider;
import com.simple.social.domain.RoleBean;
import com.simple.social.domain.UserBean;
import com.simple.social.enums.RoleType;
import com.simple.social.enums.UserType;
import com.simple.social.security.GoogleIdConfig;
import com.simple.social.service.TokenAuthenticationService;
import com.simple.social.service.UserService;
import com.simple.social.util.security.GoogleIdUserDetails;
import com.simple.social.util.security.GoogleUserInfo;
import com.simple.social.util.security.NoopAuthenticationManager;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;

public class GoogleLoginFilter extends AbstractAuthenticationProcessingFilter {

  private static Logger logger = LoggerFactory.getLogger(GoogleLoginFilter.class);

  private static GoogleIdConfig config;
  
  private static UserInfoTokenServices userInfoTokenService;
  private static AuthorizationCodeAccessTokenProvider accessTokenProvider = new AuthorizationCodeAccessTokenProvider();
  
  private static final String GOOGLE_REQUEST = "?alt=json";

  @Inject
  private RestTemplate restTemplate;

  @Inject
  private UserService userService;

  @Inject
  private JmsTemplate jmsTemplate;

  @Value("${spring.google.client.clientId}")
  private String clientId;

  @Value("${spring.google.resource.issuer}")
  private String issuer;

  @Value("${spring.google.resource.jwkUrl}")
  private String jwkUrl;

  public GoogleLoginFilter(String url, GoogleIdConfig googleIdConfig) {
    super(new AntPathRequestMatcher(url));
    config = googleIdConfig;
    userInfoTokenService = new UserInfoTokenServices(config.getResourceProperties().getUserInfoUri(), config.getResourceDetails().getClientId());
    accessTokenProvider.setStateMandatory(false);
    setAuthenticationManager(new NoopAuthenticationManager());
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
  throws AuthenticationException, IOException, ServletException {
    //logger.info("GoogleLoginFilter : attemptAuthentication");
    OAuth2AccessToken googleAccessToken = null;
    googleAccessToken = (OAuth2AccessToken) request.getSession().getAttribute("googleAccessToken");
      try {
        String code = request.getParameter("code");
        if (null == googleAccessToken) {
            AccessTokenRequest accessTokenRequest = new DefaultAccessTokenRequest();
            accessTokenRequest.setAuthorizationCode(code);
            accessTokenRequest.setCurrentUri(config.getResourceDetails().getPreEstablishedRedirectUri());
            googleAccessToken = accessTokenProvider.obtainAccessToken(config.getResourceDetails(), accessTokenRequest);
            request.getSession().setAttribute("googleAccessToken", googleAccessToken);
        }
      } catch (OAuth2Exception | UserRedirectRequiredException e) {
        setGoogleLoginRequiredHeader(response);
        throw new BadCredentialsException("Could not obtain access token", e);
      }
      try {
        if (null != googleAccessToken) {
            userInfoTokenService.loadAuthentication(googleAccessToken.getValue());
        }
      } catch (final OAuth2Exception e) {
        setGoogleLoginRequiredHeader(response);
        response.sendRedirect("/");
        throw new BadCredentialsException("Could not obtain access token", e);
      } catch (final  UserRedirectRequiredException e) {
        setGoogleLoginRequiredHeader(response);
      }
      try {
          final String idToken = googleAccessToken.getAdditionalInformation().get("id_token").toString();
          String kid = JwtHelper.headers(idToken).get("kid");
          final Jwt tokenDecoded = JwtHelper.decodeAndVerify(idToken, verifier(kid));
          final Map<String, String> authInfo = new ObjectMapper().readValue(tokenDecoded.getClaims(), Map.class);
          verifyClaims(authInfo);
          final GoogleIdUserDetails user = new GoogleIdUserDetails(authInfo, googleAccessToken);
          return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
      } catch (final Exception e) {
        setGoogleLoginRequiredHeader(response);
        throw new BadCredentialsException("Could not obtain user details from token", e);
      }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth)
  throws IOException, ServletException {
    Message message = jmsTemplate.receiveSelected("JMSCorrelationID = '"+req.getSession().getId()+"'");
    TokenAuthenticationService tokenService = ApplicationContextProvider.getApplicationContext().getBean(TokenAuthenticationService.class);
    
    OAuth2AccessToken googleAccessToken = null;
    googleAccessToken = (OAuth2AccessToken) req.getSession().getAttribute("googleAccessToken");
    String email = null;
    GoogleUserInfo userInfo = null;

    userInfo = restTemplate.getForObject(config.getResourceProperties().getUserInfoUri()+GOOGLE_REQUEST+"&access_token="+googleAccessToken.getValue(), GoogleUserInfo.class);
    GoogleIdUserDetails googleUserDetails = (GoogleIdUserDetails) auth.getPrincipal();
    googleUserDetails.setFirstName(userInfo.getGivenName());
    googleUserDetails.setLastName(userInfo.getFamilyName());
    
    Collection<GrantedAuthority> authorities = new LinkedList<GrantedAuthority>();
    
    try {
      Field emailField = auth.getPrincipal().getClass().getDeclaredField("username");
      emailField.setAccessible(true);
      email = (String) emailField.get(auth.getPrincipal());
      UserBean userBean = userService.getUserByEmailAndType(email, UserType.GOOGLE);
      if (null == userBean) {
        UserBean newUserBean = new UserBean().setFirstName(googleUserDetails.getFirstName())
                                             .setLastName(googleUserDetails.getLastName())
                                             .setEmail(email).setPassword("123").setEnabled(true);
        List<RoleBean> roles = new LinkedList<RoleBean>();
        roles.add(new RoleBean().setCode(RoleType.CUSTOMER.toString()).setTitle(RoleType.CUSTOMER.getTitle()));
        authorities.add(new SimpleGrantedAuthority("ROLE_"+RoleType.CUSTOMER.toString()));
        newUserBean.getRoles().addAll(roles);
        userService.saveUserFromSocial(newUserBean, UserType.GOOGLE);
      } else {
        userBean.getRoles().stream().forEach(r -> {
          authorities.add(new SimpleGrantedAuthority("ROLE_"+r.getCode()));
        });
      }
    } catch (NoSuchFieldException | IllegalAccessException e) {
      logger.error("{}", e);
    }
    //logger.info("GoogleLoginFilter : successfulAuthentication");
    tokenService.addAuthentication(res, email, authorities, googleUserDetails.getToken().getExpiration());
    if (null != message) {
      res.sendRedirect("/");
    }
    invalidateIrrelevantSessionAttributes(req.getSession());
    //chain.doFilter(req, res); //include other filters in chain
  }

  private void verifyClaims(Map claims) {
    int exp = (int) claims.get("exp");
    Date expireDate = new Date(exp * 1000L);
    Date now = new Date();
    if (expireDate.before(now) || !claims.get("iss").equals(issuer) || !claims.get("aud").equals(clientId)) {
        throw new RuntimeException("Invalid claims");
    }
  }

  private RsaVerifier verifier(String kid) throws Exception {
    JwkProvider provider = new UrlJwkProvider(new URL(jwkUrl));
    Jwk jwk = provider.get(kid);
    return new RsaVerifier((RSAPublicKey) jwk.getPublicKey());
  }

  private void setGoogleLoginRequiredHeader(HttpServletResponse response) {
    if (null == response.getHeader("GoogleLoginRequired") || !"true".equals(response.getHeader("GoogleLoginRequired"))) {
      response.addHeader("GoogleLoginRequired", "true");
    }
  }
  
  private void invalidateIrrelevantSessionAttributes(HttpSession session) {
    session.setAttribute("fbAccessToken", null);
  }
  
}
