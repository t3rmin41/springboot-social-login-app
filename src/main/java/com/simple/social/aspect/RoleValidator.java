package com.simple.social.aspect;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.simple.social.service.RequestValidator;
import com.simple.social.util.security.UserNotAllowedException;

@Aspect
@Component
@SuppressWarnings("unused")
public class RoleValidator implements RequestValidator {

  private List<String> allowedManageRoles = new LinkedList<String>(Arrays.asList(new String[]{"ROLE_ADMIN", "ROLE_MANAGER"}));
  private List<String> allowedModifyUserRoles = new LinkedList<String>(Arrays.asList(new String[]{"ROLE_ADMIN"}));
  
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

  @Pointcut("execution(* com.simple.social.rest.controller.UserController..*(..))")
  public void allUserControllerMethods() {}
  
  @Pointcut("execution(* com.simple.social.rest.controller.UserController.loginSuccessfull(..)) || "+
            "execution(* com.simple.social.rest.controller.UserController.logout(..)) || "+
            "execution(* com.simple.social.rest.controller.UserController.getUserRoleMap(..))"
     )
  public void allowedUserControllerMethods() {}
  
  @Before("allUserControllerMethods() && !allowedUserControllerMethods()")
  public void checkUserRolesBeforeUser(JoinPoint joinPoint) {
    UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) joinPoint.getArgs()[0];
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    validateRequestAgainstUserRoles(token, allowedModifyUserRoles, request.getRequestURI());
  }
  
}
