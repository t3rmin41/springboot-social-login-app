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
import javax.jms.Message;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.social.ApplicationContextProvider;
import com.simple.social.domain.RoleBean;
import com.simple.social.domain.UserBean;
import com.simple.social.enums.RoleType;
import com.simple.social.enums.UserType;
import com.simple.social.service.TokenAuthenticationService;
import com.simple.social.service.UserService;
import com.simple.social.util.security.GoogleIdConnectUserDetails;
import com.simple.social.util.security.GoogleUserInfo;
import com.simple.social.util.security.NoopAuthenticationManager;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;


public class GoogleLoginFilter extends AbstractAuthenticationProcessingFilter {

  private static Logger logger = LoggerFactory.getLogger(GoogleLoginFilter.class);

  @Autowired
  private UserService userService;

  @Autowired
  private OAuth2RestOperations restTemplate;

  @Autowired
  private JmsTemplate jmsTemplate;
  
  @Value("${spring.google.client.clientId}")
  private String clientId;

  @Value("${spring.google.resource.issuer}")
  private String issuer;

  @Value("${spring.google.resource.jwkUrl}")
  private String jwkUrl;
  
  @Value("${spring.google.resource.userInfoUri}")
  private String userInfoUri;
  
  public GoogleLoginFilter(String url) {
    super(new AntPathRequestMatcher(url));
    setAuthenticationManager(new NoopAuthenticationManager());
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
  throws AuthenticationException, IOException, ServletException {
    logger.info("GoogleLoginFilter : attemptAuthentication");
    OAuth2AccessToken accessToken = null;
    try {
        accessToken = restTemplate.getAccessToken();
    } catch (final OAuth2Exception e) {
        throw new BadCredentialsException("Could not obtain access token", e);
    } catch (final UserRedirectRequiredException e) {
      response.addHeader("GoogleLoginRequired", "true");
    }
    try {
        final String idToken = accessToken.getAdditionalInformation().get("id_token").toString();
        String kid = JwtHelper.headers(idToken).get("kid");
        final Jwt tokenDecoded = JwtHelper.decodeAndVerify(idToken, verifier(kid));
        final Map<String, String> authInfo = new ObjectMapper().readValue(tokenDecoded.getClaims(), Map.class);
        verifyClaims(authInfo);
        final GoogleIdConnectUserDetails user = new GoogleIdConnectUserDetails(authInfo, accessToken);
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    } catch (final Exception e) {
        throw new BadCredentialsException("Could not obtain user details from token", e);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth)
  throws IOException, ServletException {
    Message message = jmsTemplate.receiveSelected("JMSCorrelationID = '"+req.getSession().getId()+"'");
    TokenAuthenticationService tokenService = ApplicationContextProvider.getApplicationContext().getBean(TokenAuthenticationService.class);
    String email = null;
    OAuth2AccessToken accessToken = null;
    try {
      accessToken = restTemplate.getAccessToken();
    } catch (final OAuth2Exception e) {
      throw new BadCredentialsException("Could not obtain access token", e);
    }
    Collection<GrantedAuthority> authorities = new LinkedList<GrantedAuthority>();
    GoogleUserInfo userInfo = restTemplate.getForObject(userInfoUri, GoogleUserInfo.class);
    try {
      Field emailField = auth.getPrincipal().getClass().getDeclaredField("username");
      emailField.setAccessible(true);
      email = (String) emailField.get(auth.getPrincipal());
      UserBean userBean = userService.getUserByEmailAndType(email, UserType.GOOGLE);
      if (null == userBean) {
        UserBean newUserBean = new UserBean().setFirstName(userInfo.getGivenName()).setLastName(userInfo.getFamilyName())
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
    logger.info("GoogleLoginFilter : successfulAuthentication");
    //chain.doFilter(req, res); //include other filters in chain
    if (null != message) {
      tokenService.addAuthenticationWithCookies(res, email, authorities, req.getSession().getId(), accessToken.getExpiration());
      res.sendRedirect("/");
    } else {
      tokenService.addAuthentication(res, email, authorities, accessToken.getExpiration());
    }
  }

  public void setRestTemplate(OAuth2RestTemplate restTemplate2) {
    this.restTemplate = restTemplate2;
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


}
