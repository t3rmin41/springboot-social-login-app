package com.simple.social.service;

import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import com.simple.social.util.security.UserNotAllowedException;

@Service
public class RequestValidatorImpl implements RequestValidator {

  @Override
  public boolean validateRequestAgainstUserRoles(UsernamePasswordAuthenticationToken token, List<String> allowedRoles, String path)
  throws UserNotAllowedException {
    for (String role : allowedRoles) {
      if (token.getAuthorities().contains(new SimpleGrantedAuthority(role))) {
        return true;
      }
    }
    throw new UserNotAllowedException("Action is not allowed").setPath(path);
  }


}
