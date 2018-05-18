package com.simple.social.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import com.simple.social.enums.UserType;
import com.simple.social.jpa.RoleDao;
import com.simple.social.jpa.UserDao;

@Repository
public class UserRepositoryImpl implements UserRepository {

  private static Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);
  
  @PersistenceContext
  private EntityManager em;
  
  @Autowired
  private PasswordEncoder passwordEncoder;
  
  @Override
  @Transactional
  public UserDao getUserByEmail(String email) {
    UserDao userDao = null;
    try {
      String q = "SELECT u FROM UserDao u LEFT JOIN FETCH u.roles WHERE u.email = :pemail AND u.type = 'APP'";
      TypedQuery<UserDao> query = em.createQuery(q, UserDao.class);
      query.setParameter("pemail", email);
      userDao = query.getSingleResult();
    } catch (NoResultException e) {
      log.warn("No app user found with email="+email);
    }
    return userDao;
  }

  @Override
  @Transactional
  public UserDao getUserByEmailAndType(String email, UserType type) {
    UserDao userDao = null;
    try {
      String q = "SELECT u FROM UserDao u LEFT JOIN FETCH u.roles WHERE u.email = :pemail AND u.type = :ptype";
      TypedQuery<UserDao> query = em.createQuery(q, UserDao.class);
      query.setParameter("pemail", email);
      query.setParameter("ptype", type);
      userDao = query.getSingleResult();
    } catch (NoResultException e) {
      log.warn("No app user found with email="+email+" and type="+type);
    }
    return userDao;
  }
  
  @Override
  @Transactional
  public UserDao getUserByEmailAndPassword(String email, String password) {
    String q = "SELECT u FROM UserDao u WHERE u.email = :pemail AND u.password = :ppassword AND u.type = 'APP'";
    TypedQuery<UserDao> query = em.createQuery(q, UserDao.class);
    query.setParameter("pemail", email);
    query.setParameter("ppassword", password);
    List<UserDao> users = query.getResultList();
    if (1 == users.size()) {
        return users.get(0);
    } else {
        return null;
    }
  }

  @Override
  @Transactional
  public UserDao getUserById(Long id) {
    String q = "SELECT u FROM UserDao u LEFT JOIN FETCH u.roles WHERE u.id = :pid AND u.type = 'APP'";
    TypedQuery<UserDao> query = em.createQuery(q, UserDao.class);
    query.setParameter("pid", id);
    return query.getSingleResult();
  }

  @Override
  @Transactional
  public UserDao saveUser(UserDao jpa) {
    jpa.setPassword(passwordEncoder.encode(jpa.getPassword()));
    return em.merge(jpa);
  }

  @Override
  @Transactional
  public void assignRoles(Set<RoleDao> roles) {
    for (RoleDao role : roles) {
      em.merge(role);
    }
  }

  @Override
  @Transactional
  public void removeRoles(Set<RoleDao> roles) {
    for (RoleDao role : roles) {
      String q = "DELETE FROM RoleDao r WHERE r.role = :role AND r.user = :user AND r.active = 1";
      Query query = em.createQuery(q);
      query.setParameter("role", role.getRole());
      query.setParameter("user", role.getUser());
      query.executeUpdate();
    }
  }

  @Override
  @Transactional
  public List<UserDao> getAllUsers() {
    String q = "SELECT u FROM UserDao u WHERE u.enabled = true";
    TypedQuery<UserDao> query = em.createQuery(q, UserDao.class);
    return query.getResultList();
  }

  @Override
  @Transactional
  public boolean deleteUser(UserDao jpa) {
    boolean result = false;
    try {
      em.remove(jpa);
      result = true;
    } catch (IllegalArgumentException e) {
      log.error(e.getMessage());
    } catch (TransactionRequiredException e) {
      log.error(e.getMessage());
    }
    return result;
  }

  @Override
  @Transactional
  public UserDao updateUser(UserDao jpa, boolean isPasswordChanged) {
    if (isPasswordChanged) {
      jpa.setPassword(passwordEncoder.encode(jpa.getPassword()));
    } else {
      jpa.setPassword(jpa.getPassword());
    }
    UserDao updated = em.merge(jpa);
    return getUserById(updated.getId());
  }

  @Override
  @Transactional
  public Set<RoleDao> getUserRolesByNames(UserDao jpa, Set<String> rolesNames) {
    String q = "SELECT r FROM RoleDao r WHERE r.role IN :roles AND r.user = :user AND r.active = 1";
    TypedQuery<RoleDao> query = em.createQuery(q, RoleDao.class);
    query.setParameter("roles", rolesNames);
    query.setParameter("user", jpa);
    List<RoleDao> resultList = query.getResultList();
    Set<RoleDao> roles = new HashSet<RoleDao>();
    roles.addAll(resultList);
    return roles;
  }

  @Override
  @Transactional
  public Set<RoleDao> getUserRolesByEmail(String email) {
    String q = "SELECT r FROM RoleDao r WHERE r.user.email = :pemail AND r.active = 1";
    TypedQuery<RoleDao> query = em.createQuery(q, RoleDao.class);
    query.setParameter("pemail", email);
    List<RoleDao> resultList = query.getResultList();
    Set<RoleDao> roles = new HashSet<RoleDao>();
    roles.addAll(resultList);
    return roles;
  }
  
}
