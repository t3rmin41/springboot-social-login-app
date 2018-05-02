package com.simple.fb.service;

import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.simple.fb.security.UserNotAllowedException;

public interface RequestValidator {

  boolean validateRequestAgainstUserRoles(UsernamePasswordAuthenticationToken token, List<String> allowedRoles, String path)
  throws UserNotAllowedException;
}
