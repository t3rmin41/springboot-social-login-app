package com.simple.social.service;

import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import com.simple.social.domain.UserBean;
import com.simple.social.enums.UserType;
import com.simple.social.mapper.UserMapper;

@Service
public class UserServiceImpl implements UserService {

  @Inject
  private UserMapper userMapper;

  @Override
  public UserBean getUserByEmailAndPassword(String email, String password) {
    return userMapper.getUserBeanByEmailAndPassword(email, password);
  }

  @Override
  public UserBean getUserByEmail(String email) {
    return userMapper.getUserBeanByEmail(email);
  }

  @Override
  public UserBean getUserByEmailAndType(String email, UserType type) {
    return userMapper.getUserBeanByEmailAndType(email, type);
  }
  
  @Override
  public UserBean getUserById(Long id) {
    return userMapper.convertUserToBeanByUserId(id);
  }

  @Override
  public UserBean saveUser(UserBean bean) {
    return userMapper.saveUser(bean);
  }

  @Override
  public UserBean saveUserFromSocial(UserBean bean, UserType type) {
    return userMapper.saveUserFromSocial(bean, type);
  }
  
  @Override
  public List<UserBean> getAllUsers() {
    return userMapper.getAllUsers();
  }

  @Override
  public boolean deleteUserById(Long id) {
    return userMapper.deleteUserById(id);
  }

  @Override
  public UserBean updateUser(UserBean bean) {
    return userMapper.updateUser(bean);
  }

  @Override
  public Set<String> getRolesByEmail(String email) {
    return userMapper.getRolesByEmail(email);
  }

}
