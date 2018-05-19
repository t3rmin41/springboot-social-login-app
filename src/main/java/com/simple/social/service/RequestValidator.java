package com.simple.social.service;

import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.simple.social.security.UserNotAllowedException;

public interface RequestValidator {

  boolean validateRequestAgainstUserRoles(UsernamePasswordAuthenticationToken token, List<String> allowedRoles, String path)
  throws UserNotAllowedException;
}
