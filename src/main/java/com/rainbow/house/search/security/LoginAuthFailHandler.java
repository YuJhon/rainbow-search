package com.rainbow.house.search.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>功能描述</br>登录验证失败处理器</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/30 23:10
 */
public class LoginAuthFailHandler extends SimpleUrlAuthenticationFailureHandler {

  private final LoginUrlEntryPoint urlEntryPoint;

  /**
   * <pre>构造函数</pre>
   *
   * @param urlEntryPoint
   */
  public LoginAuthFailHandler(LoginUrlEntryPoint urlEntryPoint) {
    this.urlEntryPoint = urlEntryPoint;
  }

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
    String targetUrl = this.urlEntryPoint.determineUrlToUseForThisRequest(request, response, exception);
    targetUrl += "?" + exception.getMessage();
    super.setDefaultFailureUrl(targetUrl);
    super.onAuthenticationFailure(request, response, exception);
  }
}
