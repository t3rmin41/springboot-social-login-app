package com.simple.social.test.mapper;

import static org.junit.Assert.assertEquals;
import java.util.HashSet;
import java.util.Set;
import org.assertj.core.util.Sets;
import org.junit.Test;
import com.simple.social.mapper.UserMapper;
import com.simple.social.mapper.UserMapperImpl;

public class UserMapperTest {

  private UserMapper mapper = new UserMapperImpl();
  
  @Test
  public void rolesDifferenceShouldReturnNewRole() {
    Set<String> oldRoles = Sets.newLinkedHashSet("ADMIN","USER");
    Set<String> newRoles = Sets.newLinkedHashSet("ADMIN","GUEST");
    Set<String> newRoleSet = new HashSet<String>();
    newRoleSet.add("GUEST");
    assertEquals(newRoleSet, mapper.getNewRolesDifference(oldRoles, newRoles));
  }
  
  @Test
  public void rolesDifferenceShouldReturnOldRole() {
    Set<String> oldRoles = Sets.newLinkedHashSet("ADMIN","USER");
    Set<String> newRoles = Sets.newLinkedHashSet("ADMIN","GUEST");
    Set<String> oldRoleSet = new HashSet<String>();
    oldRoleSet.add("USER");
    assertEquals(oldRoleSet, mapper.getOldRolesDifference(oldRoles, newRoles));
  }
  
  @Test
  public void rolesDifferenceShouldReturnNewRoles() {
    Set<String> oldRoles = Sets.newLinkedHashSet("ADMIN","MANAGER");
    Set<String> newRoles = Sets.newLinkedHashSet("MANAGER","ADMIN","GUEST","USER");
    Set<String> newRoleSet = new HashSet<String>();
    newRoleSet.add("GUEST");
    newRoleSet.add("USER");
    assertEquals(newRoleSet, mapper.getNewRolesDifference(oldRoles, newRoles));
  }
  
  @Test
  public void rolesDifferenceShouldReturnOnlyNewRoles() {
    Set<String> oldRoles = Sets.newLinkedHashSet("ADMIN","MANAGER");
    Set<String> newRoles = Sets.newLinkedHashSet("GUEST","USER");
    Set<String> newRoleSet = new HashSet<String>();
    newRoleSet.add("GUEST");
    newRoleSet.add("USER");
    assertEquals(newRoleSet, mapper.getNewRolesDifference(oldRoles, newRoles));
  }
  
  @Test
  public void rolesDifferenceShouldReturnNoNewRoles() {
    Set<String> oldRoles = Sets.newLinkedHashSet("MANAGER","ADMIN","GUEST","USER");
    Set<String> newRoles = Sets.newLinkedHashSet("ADMIN","MANAGER");
    Set<String> newRoleSet = new HashSet<String>();
    assertEquals(newRoleSet, mapper.getNewRolesDifference(oldRoles, newRoles));
  }
  
  @Test
  public void rolesDifferenceShouldReturnNoOldRoles() {
    Set<String> oldRoles = Sets.newLinkedHashSet("ADMIN","MANAGER");
    Set<String> newRoles = Sets.newLinkedHashSet("MANAGER","ADMIN","GUEST","USER");
    Set<String> oldRoleSet = new HashSet<String>();
    assertEquals(oldRoleSet, mapper.getOldRolesDifference(oldRoles, newRoles));
  }
  
  @Test
  public void rolesDifferenceShouldReturnOldRoles() {
    Set<String> oldRoles = Sets.newLinkedHashSet("MANAGER","GUEST","ADMIN","USER");
    Set<String> newRoles = Sets.newLinkedHashSet("ADMIN","MANAGER");
    Set<String> oldRoleSet = new HashSet<String>();
    oldRoleSet.add("GUEST");
    oldRoleSet.add("USER");
    assertEquals(oldRoleSet, mapper.getOldRolesDifference(oldRoles, newRoles));
  }
  
  @Test
  public void rolesDifferenceShouldReturnOnlyOldRoles() {
    Set<String> oldRoles = Sets.newLinkedHashSet("ADMIN","MANAGER");
    Set<String> newRoles = Sets.newLinkedHashSet("GUEST","USER");
    Set<String> oldRoleSet = new HashSet<String>();
    oldRoleSet.add("ADMIN");
    oldRoleSet.add("MANAGER");
    assertEquals(oldRoleSet, mapper.getOldRolesDifference(oldRoles, newRoles));
  }
  
}
