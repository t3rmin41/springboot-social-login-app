package com.simple.social.security;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CompositeFilter;
import com.simple.social.filter.FacebookLoginFilter;
import com.simple.social.filter.FacebookObtainTokenFilter;
import com.simple.social.filter.GoogleLoginFilter;
import com.simple.social.filter.GoogleObtainTokenFilter;
import com.simple.social.filter.JWTAuthFilter;
import com.simple.social.filter.JWTLoginFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Inject
  private DataSource dataSource;

  @Bean
  public RequestContextListener requestContextListener() {
    return new RequestContextListener();
  }
  
  @Bean
  @Scope(value="session", proxyMode = ScopedProxyMode.TARGET_CLASS)
  public OAuth2AccessToken accessToken() {
    return new DefaultOAuth2AccessToken("");
  }
  
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
  
  @Bean
  public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
  }

  @Bean
  public GoogleIdConfig googleIdConfig() {
    return new GoogleIdConfig();
  }
  
  @Bean
  public FacebookConfig facebookConfig() {
    return new FacebookConfig();
  }

  @Bean
  public GoogleLoginFilter googleLoginFilter() {
    final GoogleLoginFilter googleLoginFilter = new GoogleLoginFilter("/google/login", googleIdConfig());
    return googleLoginFilter;
  }
  
  @Bean
  public GoogleObtainTokenFilter obtainGoogleTokenFilter() {
    final GoogleObtainTokenFilter googleObtainTokenFilter = new GoogleObtainTokenFilter("/google/obtaintoken", googleIdConfig());
    return googleObtainTokenFilter;
  }

  @Bean
  public FacebookLoginFilter facebookLoginFilter() {
    final FacebookLoginFilter facebookLoginFilter = new FacebookLoginFilter("/facebook/login", facebookConfig());
    return facebookLoginFilter;
  }

  @Bean
  public FacebookObtainTokenFilter obtainFacebookTokenFilter() {
    final FacebookObtainTokenFilter facebookObtainTokenFilter = new FacebookObtainTokenFilter("/facebook/obtaintoken", facebookConfig());
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
