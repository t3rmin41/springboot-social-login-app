package com.simple.social.rest.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.simple.social.domain.RoleBean;
import com.simple.social.domain.UserBean;
import com.simple.social.enums.RoleType;
import com.simple.social.enums.UserType;
import com.simple.social.service.UserService;

@Controller
@RequestMapping(value = "/users", produces = APPLICATION_JSON_UTF8_VALUE)
public class UserController {

  @Inject
  private UserService users;

  @RequestMapping(value = "/login/success", method = RequestMethod.POST)
  public @ResponseBody UserBean loginSuccessfull(HttpSession session, Principal principal) {
    return getUserBeanBySessionAndPrincipal(session, principal);
  }
  
  @RequestMapping(value = "/logout", method = RequestMethod.POST)
  public @ResponseBody UserBean logout(HttpSession session, Principal principal) {
    return getUserBeanBySessionAndPrincipal(session, principal);
  }

  @RequestMapping(value = "/roles", method = RequestMethod.GET)
  public @ResponseBody List<RoleBean> getUserRoleMap() {
      List<RoleBean> roleList = new LinkedList<RoleBean>();
      for (RoleType role : RoleType.values()) {
        roleList.add(new RoleBean().setCode(role.toString()).setTitle(role.getTitle()));
      }
      return roleList;
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public @ResponseBody UserBean getUserById(UsernamePasswordAuthenticationToken token, @PathVariable("id") Long id) {
    return users.getUserById(id);
  }
  
  @RequestMapping(value = "/info", method = RequestMethod.GET)
  public @ResponseBody Principal getUserInfo(UsernamePasswordAuthenticationToken token, Principal principal) {
    return principal;
  }
  
  @RequestMapping(value = "/all", method = RequestMethod.GET)
  public @ResponseBody List<UserBean> getUsers(UsernamePasswordAuthenticationToken token, Principal principal) {
    return users.getAllUsers();
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE)
  public @ResponseBody UserBean saveUser(UsernamePasswordAuthenticationToken token, @RequestBody UserBean bean) {
    return users.saveUser(bean);
  }

  @RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = APPLICATION_JSON_UTF8_VALUE)
  public @ResponseBody UserBean updateUser(UsernamePasswordAuthenticationToken token, @RequestBody UserBean bean) {
    return users.updateUser(bean);
  }

  @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
  public @ResponseBody boolean deleteUser(UsernamePasswordAuthenticationToken token, @PathVariable("id") Long id) {
    return users.deleteUserById(id);
  }
  
  private UserBean getUserBeanBySessionAndPrincipal(HttpSession session, Principal principal) {
    UserType userType = UserType.APP;
    if (null != session.getAttribute("googleAccessToken")) {
      userType = UserType.GOOGLE;
    } 
    if (null != session.getAttribute("fbAccessToken")) {
      userType = UserType.FB;
    }
    UserBean bean = users.getUserByEmailAndType(principal.getName(), userType);
    return bean;
  }
  
}
