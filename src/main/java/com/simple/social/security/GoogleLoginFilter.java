package com.simple.social.security;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.social.ApplicationContextProvider;
import com.simple.social.domain.UserBean;
import com.simple.social.service.UserService;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;

public class GoogleLoginFilter extends AbstractAuthenticationProcessingFilter {

  private static Logger logger = LoggerFactory.getLogger(GoogleLoginFilter.class);
  
  @Autowired
  private UserService userService;
  
  @Autowired
  private OAuth2RestOperations restTemplate;
  
  @Value("${spring.google.client.clientId}")
  private String clientId;

  @Value("${spring.google.resource.issuer}")
  private String issuer;

  @Value("${spring.google.resource.jwkUrl}")
  private String jwkUrl;
  
  protected GoogleLoginFilter(String url) {
    super(new AntPathRequestMatcher(url));
    setAuthenticationManager(new NoopAuthenticationManager());
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
  throws AuthenticationException, IOException, ServletException {
    OAuth2AccessToken accessToken;
    try {
        accessToken = restTemplate.getAccessToken();
    } catch (final OAuth2Exception e) {
        throw new BadCredentialsException("Could not obtain access token", e);
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

  public void verifyClaims(Map claims) {
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

  public void setRestTemplate(OAuth2RestTemplate restTemplate2) {
    restTemplate = restTemplate2;
  }
  
  @Override
  protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth)
  throws IOException, ServletException {
    TokenAuthenticationService tokenService = ApplicationContextProvider.getApplicationContext().getBean(TokenAuthenticationService.class);
    String email = null;
    try {
      Field emailField = auth.getPrincipal().getClass().getDeclaredField("username");
      emailField.setAccessible(true);
      email = (String) emailField.get(auth.getPrincipal());
      UserBean userBean = userService.getUserByEmail(email);
      userBean.getRoles();
    } catch (NoSuchFieldException | IllegalAccessException e) {
      logger.error("{}", e);
    }
    tokenService.addAuthentication(res, email, auth.getAuthorities());
  }
  
}
