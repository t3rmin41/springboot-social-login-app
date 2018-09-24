package com.simple.social.config;

import java.io.File;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.simple.social.app.ApplicationContextProvider;

@Configuration
@ComponentScan(basePackages = {
    "com.simple.social.view.controller",
    "com.simple.social.rest.controller",
    "com.simple.social.domain",
    "com.simple.social.service",
    "com.simple.social.mapper",
    "com.simple.social.repository",
    "com.simple.social.http.filter",
    "com.simple.social.errorhandling",
    //"com.simple.social.security",
    "com.simple.social.aspect"
})
public class ApplicationConfig {

  @Bean
  public ApplicationContextProvider applicationContextProvider() {
    return new ApplicationContextProvider();
  }
  
  @Bean
  public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
  }
  
  @Bean
  public ServletWebServerFactory servletContainer() {
    TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
    tomcat.setDocumentRoot(new File("src/main/webapp"));
    return tomcat;
  }
  
//  @Bean
//  public ServletRegistrationBean h2servletRegistration(){
//      ServletRegistrationBean registrationBean = new ServletRegistrationBean(new WebServlet());
//      registrationBean.addUrlMappings("/h2-console/*");
//      return registrationBean;
//  }
  
}
