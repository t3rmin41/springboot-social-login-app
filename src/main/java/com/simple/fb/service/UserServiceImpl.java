package com.simple.fb.service;

import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.simple.fb.domain.UserBean;
import com.simple.fb.mapper.UserMapper;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
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
  public UserBean getUserById(Long id) {
    return userMapper.convertUserToBeanByUserId(id);
  }

  @Override
  public UserBean saveUser(UserBean bean) {
    return userMapper.saveUser(bean);
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
