package com.simple.social.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import com.simple.social.ApplicationContextProvider;

@Configuration
@ComponentScan(basePackages = {"com.simple.social.view.controller", "com.simple.social.rest.controller", "com.simple.social.domain",
    "com.simple.social.service", "com.simple.social.mapper", "com.simple.social.repository",
    "com.simple.social.http.filter",
    "com.simple.social.errorhandling", "com.simple.social.security"
})
public class ApplicationConfig {

  @Bean
  public ApplicationContextProvider applicationContextProvider() {
    return new ApplicationContextProvider();
  }
  
//  @Bean
//  public ServletRegistrationBean h2servletRegistration(){
//      ServletRegistrationBean registrationBean = new ServletRegistrationBean(new WebServlet());
//      registrationBean.addUrlMappings("/h2-console/*");
//      return registrationBean;
//  }
  
}
