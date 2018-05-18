package com.simple.social.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SocialLoginController {

  @RequestMapping(value="/google/login", method = RequestMethod.GET)
  public String googleLogin() {
    return "index";
  }

}
