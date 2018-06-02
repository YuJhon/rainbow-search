package com.rainbow.house.search.config;

import ch.qos.logback.core.net.LoginAuthenticator;
import com.rainbow.house.search.security.AuthFilter;
import com.rainbow.house.search.security.AuthProvider;
import com.rainbow.house.search.security.LoginAuthFailHandler;
import com.rainbow.house.search.security.LoginUrlEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * <p>功能描述</br>全局安全配置</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/30 14:57
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  /**
   * <pre>HTTP权限控制</pre>
   *
   * @param http
   * @throws Exception
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.addFilterBefore(authFilter(), UsernamePasswordAuthenticationFilter.class);

    http.authorizeRequests()
            .antMatchers("/static/**").permitAll()
            .antMatchers("/admin/login").permitAll()
            .antMatchers("/user/login").permitAll()
            .antMatchers("/admin/**").hasRole("ADMIN")
            .antMatchers("/user/**").hasAnyRole("ADMIN", "USER")
            .antMatchers("/api/user/**").hasAnyRole("ADMIN", "USER")
            .and()
            .formLogin()
            .loginProcessingUrl("/login")
            .failureHandler(authFailHandler())
            .and()
            .logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/logout/page")
            .deleteCookies("JSESSIONID")
            .invalidateHttpSession(true)
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(urlEntryPoint())
            .accessDeniedPage("/403");

    http.csrf().disable();
    http.headers().frameOptions().sameOrigin();
  }


  @Bean
  public AuthProvider authProvider() {
    return new AuthProvider();
  }

  /**
   * 自定义认证策略
   *
   * @param authenticationManagerBuilder
   */
  @Autowired
  public void configGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) {
    authenticationManagerBuilder.authenticationProvider(authProvider());
  }

  /**
   * <pre></pre>
   *
   * @return
   */
  @Bean
  public LoginUrlEntryPoint urlEntryPoint() {
    return new LoginUrlEntryPoint("/user/login");
  }

  /**
   * <pre>登录验证失败处理</pre>
   *
   * @return
   */
  @Bean
  public LoginAuthFailHandler authFailHandler() {
    return new LoginAuthFailHandler(urlEntryPoint());
  }


  @Bean
  public AuthenticationManager authenticationManager() {
    AuthenticationManager authenticationManager = null;
    try {
      authenticationManager = super.authenticationManager();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return authenticationManager;
  }

  @Bean
  public AuthFilter authFilter() {
    AuthFilter authFilter = new AuthFilter();
    authFilter.setAuthenticationManager(authenticationManager());
    authFilter.setAuthenticationFailureHandler(authFailHandler());
    return authFilter;
  }

}
