package com.simple.fb.service;

import java.util.List;
import java.util.Set;
import com.simple.fb.domain.UserBean;

public interface UserService {

  UserBean getUserByEmailAndPassword(String email, String password);
  
  UserBean getUserByEmail(String email);
  
  UserBean getUserById(Long id);
  
  UserBean saveUser(UserBean bean);
  
  List<UserBean> getAllUsers();
  
  boolean deleteUserById(Long id);
  
  UserBean updateUser(UserBean bean);

  Set<String> getRolesByEmail(String email);
  
}
