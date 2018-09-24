package com.simple.social.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import com.simple.social.config.ActiveMqConfig;
import com.simple.social.config.ApplicationConfig;
import com.simple.social.config.ApplicationInitializerConfig;
import com.simple.social.config.JpaConfig;
import com.simple.social.config.MvcConfig;
import com.simple.social.config.SwaggerConfig;
import com.simple.social.security.SecurityConfig;

@SpringBootApplication
@Import({ApplicationConfig.class, JpaConfig.class, MvcConfig.class, ActiveMqConfig.class, SecurityConfig.class, SwaggerConfig.class, ApplicationInitializerConfig.class})
public class SimpleApp { // extends SpringBootServletInitializer {

  private static Logger log = LoggerFactory.getLogger(SimpleApp.class);

//  //for traditional .war deployment need to extend SpringBootServletInitializer
//  @Override
//  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//      return application.sources(SimpleApp.class);
//  }
  
  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(SimpleApp.class);
    springApplication.setBannerMode(Banner.Mode.OFF);
    ApplicationContext context = springApplication.run(args);
    log.warn("Context : " + context.getId());
  }
  
}