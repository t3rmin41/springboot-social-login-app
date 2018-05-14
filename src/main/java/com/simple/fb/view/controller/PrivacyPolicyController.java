package com.simple.fb.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PrivacyPolicyController {

  @RequestMapping(value="/privacypolicy", method = RequestMethod.GET)
  public String viewPrivacyPolicy() {
    return "views/privacypolicy";
  }
  
}
