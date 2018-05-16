package com.simple.social.security;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CompositeFilter;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;

@Configuration
//@EnableOAuth2Sso
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private DataSource dataSource;

//  @Autowired
//  private OAuth2ClientContext oauth2ClientContext;

  @Bean
  public RequestContextListener requestContextListener() {
      return new RequestContextListener();
  }
  
  @Bean
  public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
  }

  @Bean
  @ConfigurationProperties("google")
  public ClientResources google() {
      return new ClientResources();
  }
  
  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers("/h2-console/**",
                               "/resources/**",
                               //"/google/login**",
                               "/privacypolicy",
                               "/swagger-ui.html", //Swagger components
                               "/swagger-resources/**", //Swagger components
                               "/v2/**", //Swagger components
                               "/webjars/**", //Swagger components
                               //"/dist/app/*",
                               "/app/**",
                               "/",
                               "/favicon.ico",
                               //"/users/login/success",
                               //"/users/logout",
                               "/wrapper/**");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
      http.csrf().disable();
      http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
          .authorizeRequests()
          .antMatchers("/login*","/signin/**","/signup/**").permitAll()
          .anyRequest().authenticated()
        //.and()
        //  .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .and()
          .addFilterBefore(loginFilters(), UsernamePasswordAuthenticationFilter.class)
          .addFilterBefore(new JWTAuthFilter(), UsernamePasswordAuthenticationFilter.class);
  }


  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.jdbcAuthentication().dataSource(dataSource)
      .usersByUsernameQuery("SELECT email AS username, password, enabled FROM users WHERE email = ?")
      .authoritiesByUsernameQuery("SELECT user_id, CONCAT('ROLE_',role) AS authority FROM roles WHERE user_id = (SELECT id FROM users WHERE email = ?)")
      .passwordEncoder(passwordEncoder());
  }
  
  private Filter loginFilters() throws Exception {
    
    CompositeFilter filter = new CompositeFilter();
    List<Filter> filters = new ArrayList<>();

    JWTLoginFilter jwtLoginFilter = new JWTLoginFilter("/users/login", authenticationManager());
    filters.add(jwtLoginFilter);

    filters.add(new GoogleLoginFilter("/google/login", authenticationManager()));
    
    filter.setFilters(filters);
    return filter;
  }
  
//  private Filter ssoFilter(ClientResources client, String path) {
//    OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
//    OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
//    filter.setRestTemplate(template);
//    UserInfoTokenServices tokenServices = new UserInfoTokenServices(client.getResource().getUserInfoUri(), client.getClient().getClientId());
//    tokenServices.setRestTemplate(template);
//    filter.setTokenServices(tokenServices);
//    return filter;
//  }
  
}