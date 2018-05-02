package com.simple.fb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import com.simple.fb.app.ApplicationContextProvider;

@Configuration
@ComponentScan(basePackages = {"com.simple.fb.view.controller", "com.simple.fb.rest.controller", "com.simple.fb.domain",
    "com.simple.fb.service", "com.simple.fb.mapper", "com.simple.fb.repository",
    "com.simple.fb.errorhandling", "com.simple.fb.security"
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
