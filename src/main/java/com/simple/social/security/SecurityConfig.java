package com.simple.social.security;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CompositeFilter;
import com.simple.social.filter.FacebookLoginFilter;
import com.simple.social.filter.FacebookObtainTokenFilter;
import com.simple.social.filter.GoogleLoginFilter;
import com.simple.social.filter.GoogleObtainTokenFilter;
import com.simple.social.filter.JWTAuthFilter;
import com.simple.social.filter.JWTLoginFilter;

@Configuration
@EnableOAuth2Client
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private DataSource dataSource;

  @Autowired
  private GoogleIdConfig googleIdConfig;
  
  @Autowired
  private FacebookConfig facebookConfig;
  
  @Autowired
  private OAuth2ClientContext oauth2ClientContext;
  
  @Bean
  public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
  }

  @Bean
  public RequestContextListener requestContextListener() {
      return new RequestContextListener();
  }

//  @Bean
//  public FacebookOAuth2ClientContext facebookOAuth2ClientContext() {
//    return new FacebookOAuth2ClientContext();
//  }
//  
//  @Bean
//  public GoogleOAuth2ClientContext GoogleOAuth2ClientContext() {
//    return new GoogleOAuth2ClientContext();
//  }
  
//  @Bean
//  public GoogleIdConfig googleIdConfig() {
//    return new GoogleIdConfig();
//  }
//  
//  @Bean
//  public FacebookConfig facebookConfig() {
//    return new FacebookConfig();
//  }
  
  @Bean
  public OAuth2RestTemplate googleOAuth2RestTemplate() {
    return new OAuth2RestTemplate(googleIdConfig.googleOpenId(), oauth2ClientContext);
  }
  
  @Bean
  public OAuth2RestTemplate facebookOAuth2RestTemplate() {
    return new OAuth2RestTemplate(facebookConfig.facebookConfig(), oauth2ClientContext);
  }
  
  @Bean
  public GoogleLoginFilter googleLoginFilter() {
    final GoogleLoginFilter googleLoginFilter = new GoogleLoginFilter("/google/login");
    googleLoginFilter.setRestTemplate(googleOAuth2RestTemplate());
    return googleLoginFilter;
  }
  
  @Bean
  public GoogleObtainTokenFilter obtainGoogleTokenFilter() {
    final GoogleObtainTokenFilter googleObtainTokenFilter = new GoogleObtainTokenFilter("/google/obtaintoken");
    googleObtainTokenFilter.setRestTemplate(googleOAuth2RestTemplate());
    return googleObtainTokenFilter;
  }

  @Bean
  public FacebookLoginFilter facebookLoginFilter() {
    final FacebookLoginFilter facebookLoginFilter = new FacebookLoginFilter("/facebook/login");
    facebookLoginFilter.setRestTemplate(facebookOAuth2RestTemplate());
    return facebookLoginFilter;
  }
  
  @Bean
  public FacebookObtainTokenFilter obtainFacebookTokenFilter() {
    final FacebookObtainTokenFilter facebookObtainTokenFilter = new FacebookObtainTokenFilter("/facebook/obtaintoken");
    facebookObtainTokenFilter.setRestTemplate(facebookOAuth2RestTemplate());
    return facebookObtainTokenFilter;
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers("/h2-console/**",
                               "/resources/**",
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
      http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
      http
          .authorizeRequests()
          .antMatchers("/login*", "/signin/**", "/signup/**").permitAll()
          .anyRequest().authenticated()
        .and()
          .addFilterBefore(loginFilters(), UsernamePasswordAuthenticationFilter.class)
          .addFilterBefore(new JWTAuthFilter(), UsernamePasswordAuthenticationFilter.class)
          .addFilterAfter(new OAuth2ClientContextFilter(), AbstractPreAuthenticatedProcessingFilter.class)
          .addFilterAfter(googleLoginFilter(), OAuth2ClientContextFilter.class)
          .addFilterAfter(obtainGoogleTokenFilter(), OAuth2ClientContextFilter.class)
          .addFilterAfter(facebookLoginFilter(), OAuth2ClientContextFilter.class)
          .addFilterAfter(obtainFacebookTokenFilter(), OAuth2ClientContextFilter.class)
          ;
  }


  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.jdbcAuthentication().dataSource(dataSource)
      .usersByUsernameQuery("SELECT email AS username, password, enabled FROM users WHERE email = ? AND type = 'APP'")
      .authoritiesByUsernameQuery("SELECT user_id, CONCAT('ROLE_',role) AS authority FROM roles WHERE user_id = (SELECT id FROM users WHERE email = ? AND type = 'APP')")
      .passwordEncoder(passwordEncoder());
  }
  
  private Filter loginFilters() throws Exception {
    
    CompositeFilter filter = new CompositeFilter();
    List<Filter> filters = new ArrayList<>();

    JWTLoginFilter jwtLoginFilter = new JWTLoginFilter("/users/login", authenticationManager());
    filters.add(jwtLoginFilter);

    filter.setFilters(filters);
    return filter;
  }

}
