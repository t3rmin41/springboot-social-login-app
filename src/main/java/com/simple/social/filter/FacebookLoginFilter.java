package com.simple.social.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.jms.Message;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
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
import com.simple.social.util.security.FacebookIdUserDetails;
import com.simple.social.util.security.NoopAuthenticationManager;
import com.simple.social.util.security.UserNotFoundException;

public class FacebookLoginFilter extends AbstractAuthenticationProcessingFilter {

  //https://github.com/ozgengunay/FBSpringSocialRESTAuth/blob/master/server/src/main/java/com/ozgen/server/security/oauth/FacebookTokenAuthenticationFilter.java
  
  private static final Logger logger = LoggerFactory.getLogger(FacebookLoginFilter.class);
  
  private OAuth2RestTemplate restTemplate;
  
  @Autowired
  private UserService userService;

  @Autowired
  private JmsTemplate jmsTemplate;

  @Value("${spring.facebook.resource.userInfoUri}")
  private String userInfoUri;
  
  private static final String FACEBOOK_FIELDS = "fields=id,email,first_name,last_name,picture";
  
  public FacebookLoginFilter(String url) {
    super(new AntPathRequestMatcher(url));
    setAuthenticationManager(new NoopAuthenticationManager());
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
  throws AuthenticationException, IOException, ServletException {
    //logger.info("FacebookLoginFilter : attemptAuthentication");
    OAuth2AccessToken accessToken = null;
    try {
        accessToken = restTemplate.getAccessToken();
    } catch (final OAuth2Exception | UserRedirectRequiredException e) {
      response.addHeader("FacebookLoginRequired", "true");
      throw new BadCredentialsException("Could not obtain access token", e);
    }
    try {
      String json = restTemplate.getForObject(userInfoUri+"?"+FACEBOOK_FIELDS+"&access_token="+accessToken.getValue(), String.class);
      final Map<String, String> authInfo = new ObjectMapper().readValue(json, Map.class);
      final FacebookIdUserDetails fbUser = new FacebookIdUserDetails(authInfo, accessToken);
      return new UsernamePasswordAuthenticationToken(fbUser, null, fbUser.getAuthorities());
    } catch (final Exception e) {
      throw new BadCredentialsException("Could not obtain user details from token", e);
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
  
  public void setRestTemplate(OAuth2RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
  
}
