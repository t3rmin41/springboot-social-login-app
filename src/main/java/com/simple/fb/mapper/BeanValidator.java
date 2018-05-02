package com.simple.fb.mapper;

import java.io.Serializable;
import java.util.List;
import com.simple.fb.errorhandling.ErrorField;
import com.simple.fb.errorhandling.WrongBeanFormatException;

public interface BeanValidator {

  List<ErrorField> validateBean(Serializable bean) throws WrongBeanFormatException;
  
}
