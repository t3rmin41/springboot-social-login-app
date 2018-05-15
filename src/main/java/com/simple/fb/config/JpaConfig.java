package com.simple.fb.config;

import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class JpaConfig {

  @Value("${spring.profiles.active}")
  private String activeProfile;
  
  @Bean(name = "dataSource")
  public DriverManagerDataSource dataSource() {
    DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
    if ("prod".equals(activeProfile)) {
      driverManagerDataSource.setDriverClassName("org.postgresql.Driver");
      driverManagerDataSource.setUrl("jdbc:postgresql://ec2-79-125-12-48.eu-west-1.compute.amazonaws.com:5432/d1rqn52vosj28b");
      driverManagerDataSource.setUsername("oipeycrmnbyqhv"); // using account credentials
      driverManagerDataSource.setPassword("b21acd74489bc4fd1b8a31ff10f47d77c03e702c96eeed53faebe45bc337441f");
    } else if ("test".equals(activeProfile)) {
      driverManagerDataSource.setDriverClassName("org.h2.Driver");
      driverManagerDataSource.setUrl("jdbc:h2:./db/test/bin;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE");
      //driverManagerDataSource.setPassword("t3sts3cr3t");
    } else if ("dev".equals(activeProfile)) {
      driverManagerDataSource.setDriverClassName("org.h2.Driver");
      driverManagerDataSource.setUrl("jdbc:h2:./db/dev/bin;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE");
      driverManagerDataSource.setUsername("sa");
      //driverManagerDataSource.setPassword("d3vs3cr3t");
    }
    return driverManagerDataSource;
  }
  
  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory emf){
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(emf);
    return transactionManager;
  }

  @Bean
  public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
    return new PersistenceExceptionTranslationPostProcessor();
  }
  
  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource());
    em.setPackagesToScan(new String[] { "com.simple.fb.repository", "com.simple.fb.jpa" });
    JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);
    em.setJpaProperties(additionalProperties());
    return em;
  }
  
  Properties additionalProperties() {
    Properties properties = new Properties();
    properties.setProperty("hibernate.hbm2ddl.auto", "update");
    if ("prod".equals(activeProfile)) {
      properties.setProperty("hibernate.show_sql", "false");
      properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL92Dialect");
    } else if ("test".equals(activeProfile)) {
      properties.setProperty("hibernate.show_sql", "true");
      properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
      properties.setProperty("h2.console.settings.web-allow-others", "true");
    } else if ("dev".equals(activeProfile)) {
      properties.setProperty("hibernate.show_sql", "true");
      properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
      properties.setProperty("h2.console.settings.web-allow-others", "true");
    }
    return properties;
 }
  
}
