package com.rainbow.house.search.security;

import com.rainbow.house.search.base.LoginUserUtil;
import com.rainbow.house.search.entity.UserDO;
import com.rainbow.house.search.service.SmsService;
import com.rainbow.house.search.service.UserService;
import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * <p>功能描述</br>认证过滤器</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/02 10:00
 */
public class AuthFilter extends UsernamePasswordAuthenticationFilter {

  @Autowired
  private UserService userService;

  @Autowired
  private SmsService smsService;

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
          throws AuthenticationException {
    /** 先获取是否存在用户名 **/
    String name = obtainUsername(request);
    if (!Strings.isNullOrEmpty(name)) {
      request.setAttribute("username", name);
      return super.attemptAuthentication(request, response);
    }

    /** 没有的话，判断手机号是否存在，如果存在就动态注册 **/
    String telephone = request.getParameter("telephone");
    if (Strings.isNullOrEmpty(telephone) || !LoginUserUtil.checkTelephone(telephone)) {
      throw new BadCredentialsException("Wrong telephone number");
    }

    UserDO user = userService.findUserByTelephone(telephone);
    String inputCode = request.getParameter("smsCode");
    String sessionCode = smsService.getSmsCode(telephone);
    if (Objects.equals(inputCode, sessionCode)) {
      /** 如果用户第一次用手机登录 则自动注册该用户 **/
      if (user == null) {
        user = userService.addUserByPhone(telephone);
      }
      return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    } else {
      throw new BadCredentialsException("smsCodeError");
    }
  }
}
