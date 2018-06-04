package com.rainbow.house.search.web.controller;

import com.rainbow.house.search.base.LoginUserUtil;
import com.rainbow.house.search.base.RainbowApiResponse;
import com.rainbow.house.search.base.ServiceResult;
import com.rainbow.house.search.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

  @Autowired
  private SmsService smsService;

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

  /**
   * <pre>短信发送</pre>
   *
   * @param telephone 手机号码
   * @return
   */
  @GetMapping(value = "sms/code")
  @ResponseBody
  public RainbowApiResponse smsCode(@RequestParam("telephone") String telephone) {
    /*if (!LoginUserUtil.checkTelephone(telephone)) {
      return RainbowApiResponse.message(HttpStatus.BAD_REQUEST.value(), "请输入正确的手机号");
    }*/
    ServiceResult<String> result = smsService.sendSms(telephone);
    if (result.isSuccess()) {
      return RainbowApiResponse.success("");
    } else {
      return RainbowApiResponse.message(HttpStatus.BAD_REQUEST.value(), result.getMessage());
    }
  }
}
