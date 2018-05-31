package com.rainbow.house.search.security;

import com.rainbow.house.search.entity.UserDO;
import com.rainbow.house.search.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * <p>功能描述</br>自定义认证实现</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/30 21:59
 */
public class AuthProvider implements AuthenticationProvider {

  @Autowired
  private UserService userService;

  private final Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String userName = authentication.getName();
    String inputPassword = (String) authentication.getCredentials();

    UserDO user = userService.findByName(userName);
    if (user == null) {
      throw new AuthenticationCredentialsNotFoundException("authError");
    }
    if (this.passwordEncoder.isPasswordValid(user.getPassword(), inputPassword, user.getId())) {
      return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }
    throw new BadCredentialsException("AuthError");
  }

  @Override
  public boolean supports(Class<?> aClass) {
    return true;
  }
}
