package com.simple.social;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import com.simple.social.config.ActiveMqConfig;
import com.simple.social.config.ApplicationConfig;
import com.simple.social.config.JpaConfig;
import com.simple.social.config.MvcConfig;
import com.simple.social.security.FacebookConfig;
import com.simple.social.security.GoogleIdConfig;
import com.simple.social.security.SecurityConfig;

@SpringBootApplication
@Import({ApplicationConfig.class, JpaConfig.class, MvcConfig.class, SecurityConfig.class, GoogleIdConfig.class, FacebookConfig.class, ActiveMqConfig.class})
public class SimpleApp { // extends SpringBootServletInitializer {

  private static Logger log = LoggerFactory.getLogger(SimpleApp.class);

  //for traditional .war deployment need to extend SpringBootServletInitializer
  //@Override
  //protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
  //    return application.sources(BookingApp.class);
  //}
  
  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(SimpleApp.class);
    springApplication.setBannerMode(Banner.Mode.OFF);
    ApplicationContext context = springApplication.run(args);
    log.warn("Context : " + context.getId());
  }
  
}