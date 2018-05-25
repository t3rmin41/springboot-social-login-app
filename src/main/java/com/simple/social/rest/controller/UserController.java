package com.simple.social.rest.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import java.security.Principal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.simple.social.service.RequestValidator;
import com.simple.social.service.UserService;

@Controller
@RequestMapping(value = "/users", produces = APPLICATION_JSON_UTF8_VALUE)
public class UserController {

  private List<String> allowedRoles = new LinkedList<String>(Arrays.asList(new String[]{"ROLE_ADMIN"}));
  
  @Autowired
  private UserService users;

  @Autowired
  private RequestValidator requestValidator;
  
  @RequestMapping(value = "/login/success", method = RequestMethod.POST)
  public @ResponseBody UserBean loginSuccessfull(HttpSession session, Principal principal) {
    UserBean bean = users.getUserByEmail(principal.getName());
    return bean;
  }
  
  @RequestMapping(value = "/logout", method = RequestMethod.POST)
  public @ResponseBody UserBean logout(HttpSession session, Principal principal) {
    UserBean bean = users.getUserByEmail(principal.getName());
    return bean;
  }

  @RequestMapping(value = "/info", method = RequestMethod.GET)
  public @ResponseBody Principal getUserInfo(Principal principal) {
    return principal;
  }
  
  @RequestMapping(value = "/roles", method = RequestMethod.GET)
  public @ResponseBody List<RoleBean> getUserRoleMap() {
      List<RoleBean> roleList = new LinkedList<RoleBean>();
      for (RoleType role : RoleType.values()) {
        roleList.add(new RoleBean().setCode(role.toString()).setTitle(role.getTitle()));
      }
      return roleList;
  }

  @RequestMapping(value = "/all", method = RequestMethod.GET)
  public @ResponseBody List<UserBean> getUsers(Principal principal, UsernamePasswordAuthenticationToken token) {
    requestValidator.validateRequestAgainstUserRoles(token, allowedRoles, "GET /users/all");
    return users.getAllUsers();
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE)
  public @ResponseBody UserBean saveUser(@RequestBody UserBean bean, UsernamePasswordAuthenticationToken token) {
    requestValidator.validateRequestAgainstUserRoles(token, allowedRoles, "POST /users/save");
    return users.saveUser(bean);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public @ResponseBody UserBean getUserById(@PathVariable("id") Long id, UsernamePasswordAuthenticationToken token) {
    List<String> allowed = new LinkedList<String>();
    allowed.addAll(allowedRoles); allowed.add("ROLE_CUSTOMER"); allowed.add("ROLE_MANAGER");
    requestValidator.validateRequestAgainstUserRoles(token, allowed, "GET /users/id");
    return users.getUserById(id);
  }

  @RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = APPLICATION_JSON_UTF8_VALUE)
  public @ResponseBody UserBean updateUser(@RequestBody UserBean bean, UsernamePasswordAuthenticationToken token) {
    requestValidator.validateRequestAgainstUserRoles(token, allowedRoles, "PUT /users/update");
    return users.updateUser(bean);
  }

  @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
  public @ResponseBody boolean deleteUser(@PathVariable("id") Long id, UsernamePasswordAuthenticationToken token) {
    requestValidator.validateRequestAgainstUserRoles(token, allowedRoles, "DELETE /users/delete");
    return users.deleteUserById(id);
  }
  
}
