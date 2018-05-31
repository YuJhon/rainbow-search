package com.rainbow.house.search.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * <p>功能描述</br>首页控制器</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/30 13:57
 */
@Controller
public class HomeController {

  /**
   * <pre>首页检查</pre>
   *
   * @param model
   * @return
   */
  @GetMapping(value = {"/", "/index"})
  public String index(Model model) {
    return "index";
  }

  @GetMapping("/404")
  public String notFoundPage() {
    return "404";
  }

  @GetMapping("/403")
  public String accessError() {
    return "403";
  }

  @GetMapping("/500")
  public String internalError() {
    return "500";
  }

  @GetMapping("/logout/page")
  public String logoutPage() {
    return "logout";
  }

}
