package com.rainbow.house.search.web.controller.error;

import com.rainbow.house.search.base.RainbowApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * <p>功能描述</br>（全局配置）异常处理控制器</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/30 14:02
 */
@Controller
public class AppErrorController implements ErrorController {
  /**
   * 错误路径
   */
  private static final String ERROR_PATH = "/error";

  private ErrorAttributes errorAttributes;

  @Override
  public String getErrorPath() {
    return ERROR_PATH;
  }

  @Autowired
  public AppErrorController(ErrorAttributes errorAttributes) {
    this.errorAttributes = errorAttributes;
  }

  /**
   * <pre>Web页面错误处理</pre>
   *
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(value = ERROR_PATH, produces = "text/html")
  public String errorPageHandler(HttpServletRequest request, HttpServletResponse response) {
    int status = response.getStatus();
    switch (status) {
      case 403:
        return "403";
      case 404:
        return "404";
      case 500:
        return "500";
      default:
        return "index";
    }
  }

  /**
   * <pre>错误的Api处理（返回结果）除Web页面外的错误处理，比如Json/XML等</pre>
   *
   * @param request 请求对象
   * @return
   */
  @RequestMapping(value = ERROR_PATH)
  @ResponseBody
  public RainbowApiResponse errorApiHandler(HttpServletRequest request) {
    RequestAttributes requestAttributes = new ServletRequestAttributes(request);
    Map<String, Object> attr = this.errorAttributes.getErrorAttributes(requestAttributes, false);
    int status = getStatus(request);
    return RainbowApiResponse.message(status, String.valueOf(attr.getOrDefault("message", "error")));
  }

  /**
   * <pre>获取状态</pre>
   *
   * @param request 请求对象
   * @return
   */
  private int getStatus(HttpServletRequest request) {
    Integer status = (Integer) request.getAttribute("javax.servlet.error.status_code");
    if (status != null) {
      return status;
    }
    return 500;
  }
}
