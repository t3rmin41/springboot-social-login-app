package com.simple.social.filter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.social.ApplicationContextProvider;
import com.simple.social.service.TokenAuthenticationService;
import com.simple.social.util.security.UserNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

public class JWTAuthFilter extends GenericFilterBean {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
  throws IOException, ServletException {
    TokenAuthenticationService tokenService = ApplicationContextProvider.getApplicationContext().getBean(TokenAuthenticationService.class);
    try {
      String username = tokenService.getAuthenticatedUsername((HttpServletRequest)request);
      List<GrantedAuthority> authorities = tokenService.getAuthenticatedUserRoles((HttpServletRequest)request);
      Authentication authentication =  new UsernamePasswordAuthenticationToken(username, null, authorities);
      SecurityContextHolder.getContext().setAuthentication(authentication);
      chain.doFilter(request, response);
    } catch (SignatureException | ExpiredJwtException | IllegalArgumentException | UserNotFoundException e) {
      ((HttpServletResponse) response).setStatus(HttpStatus.FORBIDDEN.value());
      ((HttpServletResponse) response).setContentType("application/json;charset=UTF-8");
      response.getWriter().write(convertExceptionToJson(e, (HttpServletRequest)request));
    }
  }
  
  private String convertExceptionToJson(Exception e, HttpServletRequest req) throws JsonProcessingException {
    Map<String, String> json = new HashMap<String, String>();
    json.put("timestamp", Long.toString(new Date().getTime()));
    json.put("status", "403");
    json.put("error", "Forbidden");
    json.put("exception", e.getClass().getName());
    json.put("message", e.getMessage());
    json.put("path", req.getRequestURI());
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(json);
  }

}
