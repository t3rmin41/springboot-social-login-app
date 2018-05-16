package com.simple.social.mapper;

import java.util.List;
import java.util.Set;
import com.simple.social.domain.RoleBean;
import com.simple.social.domain.UserBean;
import com.simple.social.jpa.RoleDao;

public interface UserMapper {

  UserBean getUserBeanByEmail(String email);
  
  UserBean getUserBeanByEmailAndPassword(String email, String password);

  UserBean convertUserToBeanByUserId(Long id);
  
  UserBean saveUser(UserBean bean);
  
  List<UserBean> getAllUsers();
  
  boolean deleteUserById(Long id);
  
  UserBean updateUser(UserBean bean);

  List<RoleBean> convertUserRolesToRoleBeans(Set<RoleDao> roles);

  void addRoles(Long userId, Set<String> roles);

  void removeRoles(Long userId, Set<String> roles);
  
  Set<String> getRolesByEmail(String email);
  
  Set<String> getNewRolesDifference(Set<String> oldRoles, Set<String> newRoles);
  
  Set<String> getOldRolesDifference(Set<String> oldRoles, Set<String> newRoles);
  
}
