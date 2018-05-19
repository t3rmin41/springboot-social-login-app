package com.simple.social.mapper;

import java.io.Serializable;
import java.util.List;
import com.simple.social.errorhandling.ErrorField;
import com.simple.social.errorhandling.WrongBeanFormatException;

public interface BeanValidator {

  List<ErrorField> validateBean(Serializable bean) throws WrongBeanFormatException;
  
}
