package com.rainbow.house.search.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>功能描述</br>基于角色的登陆入口控制器</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/30 23:00
 */
public class LoginUrlEntryPoint extends LoginUrlAuthenticationEntryPoint {

  private PathMatcher pathMatcher = new AntPathMatcher();
  private final Map<String, String> authEntryPointMap;

  /**
   * <pre>构造器</pre>
   *
   * @param loginFormUrl
   */
  public LoginUrlEntryPoint(String loginFormUrl) {
    super(loginFormUrl);
    authEntryPointMap = new HashMap<>();
    authEntryPointMap.put("/user/**", "/user/login");
    authEntryPointMap.put("/admin/**", "/admin/login");
  }

  /**
   * <pre>根据请求跳转到指定的页面，父类是默认使用loginFormUrl</pre>
   *
   * @param request   请求对象
   * @param response  响应对象
   * @param exception 异常
   * @return
   */
  @Override
  protected String determineUrlToUseForThisRequest(HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   AuthenticationException exception) {
    String uri = request.getRequestURI().replace(request.getContextPath(), "");
    for (Map.Entry<String, String> authEntry : this.authEntryPointMap.entrySet()) {
      if (this.pathMatcher.match(authEntry.getKey(), uri)) {
        return authEntry.getValue();
      }
    }
    return super.determineUrlToUseForThisRequest(request, response, exception);
  }
}
