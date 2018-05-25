package com.simple.social.service;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import com.simple.social.util.security.UserNotFoundException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenAuthenticationService {

  private static String SECRET = "ThisIsASecret";
  private static String TOKEN_PREFIX = "Bearer";
  private static String TOKEN_SEPARATOR = " ";
  private static String HEADER_STRING = "Authorization";

  public void addAuthentication(HttpServletResponse res, String email, Collection<? extends GrantedAuthority> authorities, Date expirationDate) {
    Claims claims = Jwts.claims().setSubject(email);
    claims.put("roles", authorities.stream().map(s -> s.toString()).collect(Collectors.toList()));
    String JWT = Jwts.builder()
        .setClaims(claims)
        .setExpiration(expirationDate)
        .signWith(SignatureAlgorithm.HS512, SECRET)
        .compact();
    res.addHeader(HEADER_STRING, TOKEN_PREFIX + TOKEN_SEPARATOR + JWT);
  }
  
  public void addAuthenticationWithCookies(HttpServletResponse res, String email, Collection<? extends GrantedAuthority> authorities, String messageId, Date expirationDate) {
    Claims claims = Jwts.claims().setSubject(email);
    claims.put("roles", authorities.stream().map(s -> s.toString()).collect(Collectors.toList()));
    String JWT = Jwts.builder()
        .setClaims(claims)
        .setExpiration(expirationDate)
        .signWith(SignatureAlgorithm.HS512, SECRET)
        .compact();
    res.addHeader(HEADER_STRING, TOKEN_PREFIX + TOKEN_SEPARATOR + JWT);
    res.addCookie(new Cookie("GoogleAuthenticated", "true"));
    res.addCookie(new Cookie("Bearer", JWT));
    res.addCookie(new Cookie("MessageId", messageId));
  }

  public String getAuthenticatedUsername(HttpServletRequest request)  throws UserNotFoundException {
    String token = request.getHeader(HEADER_STRING);
    String email = null;
    if (token != null) {
      // parse the token.
      email = Jwts.parser()
          .setSigningKey(SECRET)
          .parseClaimsJws(token.replace(TOKEN_PREFIX + TOKEN_SEPARATOR, ""))
          .getBody()
          .getSubject();
    }
    if (null == email) {
      throw new UserNotFoundException("User or token not found");
    }
    return email;
  }

  @SuppressWarnings("unchecked")
  public List<GrantedAuthority> getAuthenticatedUserRoles(HttpServletRequest request) throws UserNotFoundException {
    List<GrantedAuthority> roles = new LinkedList<GrantedAuthority>();
    String token = request.getHeader(HEADER_STRING);
    Jws<Claims> claims = Jwts.parser()
        .setSigningKey(SECRET)
        .parseClaimsJws(token.replace(TOKEN_PREFIX + TOKEN_SEPARATOR, ""));
    List<String> rolesAsString = (List<String>) claims.getBody().get("roles");
    rolesAsString.stream().forEach(rS -> {
      roles.add(new SimpleGrantedAuthority(rS));
    });
    return roles;
  }

}
