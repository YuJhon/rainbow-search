package com.rainbow.house.search.config;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

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

    http.authorizeRequests()
            .antMatchers("/static/**").permitAll()
            .antMatchers("/admin/login").permitAll()
            .antMatchers("/user/login").permitAll()
            .antMatchers("/admin/**").hasRole("ADMIN")
            .antMatchers("/user/**").hasAnyRole("ADMIN","USER")
            .antMatchers("/api/user/**").hasAnyRole("ADMIN","USER")
            .and()
            .formLogin()
            .loginProcessingUrl("/login")
            .and()
            .logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/logout/page")
            .deleteCookies("JSESSIONID")
            .invalidateHttpSession(true);

    http.csrf().disable();
    http.headers().frameOptions().sameOrigin();
  }




}
