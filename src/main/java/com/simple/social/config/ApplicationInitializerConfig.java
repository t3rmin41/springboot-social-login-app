package com.simple.social.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;

@Configuration
public class ApplicationInitializerConfig implements WebApplicationInitializer {

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    ServletRegistration.Dynamic h2Servlet = servletContext.addServlet("h2Servlet", new org.h2.server.web.WebServlet());
    h2Servlet.setLoadOnStartup(2);
    h2Servlet.addMapping("/h2-console/*");
    
//    ServletRegistration.Dynamic swaggerServlet = servletContext.addServlet("h2Servlet", new org.h2.server.web.WebServlet());
//    h2Servlet.setLoadOnStartup(3);
//    h2Servlet.addMapping("/h2-console/*");
  }

}
