package com.rainbow.house.search.web.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * <p>功能描述</br>用户登录控制器</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/30 23:21
 */
@Controller
public class UserController {

  @GetMapping("/user/login")
  public String loginPage() {
    return "user/login";
  }

  @GetMapping("/user/center")
  public String centerPage() {
    return "user/center";
  }
}
