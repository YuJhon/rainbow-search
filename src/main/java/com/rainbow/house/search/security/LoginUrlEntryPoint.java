package com.rainbow.house.search.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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

  private static final String API_FREFIX = "/api";
  private static final String API_CODE_403 = "{\"code\": 403}";
  private static final String CONTENT_TYPE = "application/json;charset=UTF-8";

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

  /**
   * <pre>如果是Api接口 返回json数据 否则按照一般流程处理</pre>
   *
   * @param request
   * @param response
   * @param authException
   * @throws IOException
   * @throws ServletException
   */
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
                       AuthenticationException authException) throws IOException, ServletException {
    String uri = request.getRequestURI();
    if (uri.startsWith(API_FREFIX)) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.setContentType(CONTENT_TYPE);

      PrintWriter pw = response.getWriter();
      pw.write(API_CODE_403);
      pw.close();
    } else {
      super.commence(request, response, authException);
    }

  }
}
