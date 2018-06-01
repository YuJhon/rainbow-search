package com.rainbow.house.search.web.controller.user;

import com.rainbow.house.search.base.LoginUserUtil;
import com.rainbow.house.search.base.RainbowApiResponse;
import com.rainbow.house.search.base.ServiceMultiResult;
import com.rainbow.house.search.base.ServiceResult;
import com.rainbow.house.search.base.enums.HouseSubscribeStatusEnum;
import com.rainbow.house.search.service.HouseService;
import com.rainbow.house.search.service.UserService;
import com.rainbow.house.search.web.dto.HouseDTO;
import com.rainbow.house.search.web.dto.HouseSubscribeDTO;
import javafx.util.Pair;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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

  @Autowired
  private UserService userService;

  @Autowired
  private HouseService houseService;

  @GetMapping("/user/login")
  public String loginPage() {
    return "user/login";
  }

  @GetMapping("/user/center")
  public String centerPage() {
    return "user/center";
  }

  /**
   * <pre>更新用户信息</pre>
   *
   * @param profile
   * @param value
   * @return
   */
  @PostMapping("api/user/info")
  @ResponseBody
  public RainbowApiResponse updateUserInfo(@RequestParam(value = "profile") String profile,
                                           @RequestParam(value = "value") String value) {
    if (value.isEmpty()) {
      return RainbowApiResponse.status(RainbowApiResponse.RespStatus.BAD_REQUEST);
    }

    if ("email".equals(profile) && !LoginUserUtil.checkEmail(value)) {
      return RainbowApiResponse.message(HttpStatus.SC_BAD_REQUEST, "不支持的邮箱格式！");
    }

    ServiceResult result = userService.modifyUserProfile(profile, value);
    if (result.isSuccess()) {
      return RainbowApiResponse.success("");
    } else {
      return RainbowApiResponse.message(HttpStatus.SC_BAD_REQUEST, result.getMessage());
    }
  }

  /**
   * <pre>预约房间</pre>
   *
   * @param houseId 房产Id
   * @return
   */
  @PostMapping("api/user/house/subscribe")
  @ResponseBody
  public RainbowApiResponse subscribeHouse(@RequestParam(value = "house_id") Long houseId) {
    ServiceResult result = houseService.addSubscribeOrder(houseId);
    if (result.isSuccess()) {
      return RainbowApiResponse.success("");
    } else {
      return RainbowApiResponse.message(HttpStatus.SC_BAD_REQUEST, result.getMessage());
    }
  }

  /**
   * <pre>查看预约房间列表</pre>
   *
   * @param start  开始
   * @param size   查询条数
   * @param status 预约状态
   * @return
   */
  @GetMapping("api/user/house/subscribe/list")
  @ResponseBody
  public RainbowApiResponse subscribeList(
          @RequestParam(value = "start", defaultValue = "0") int start,
          @RequestParam(value = "size", defaultValue = "3") int size,
          @RequestParam(value = "status") int status) {

    ServiceMultiResult<Pair<HouseDTO, HouseSubscribeDTO>> result = houseService.querySubscribeList(HouseSubscribeStatusEnum.of(status), start, size);
    if (result.getResultSize() == 0) {
      return RainbowApiResponse.success(result.getResults());
    }
    RainbowApiResponse response = RainbowApiResponse.success(result.getResults());
    response.setMore(result.getTotal() > (start + size));
    return response;
  }

  /**
   * <pre>预约记录</pre>
   *
   * @param houseId   房产Id
   * @param orderTime 预约时间
   * @param desc      描述
   * @param telephone 预约电话
   * @return
   */
  @PostMapping("api/user/house/subscribe/date")
  @ResponseBody
  public RainbowApiResponse subscribeDate(@RequestParam(value = "houseId") Long houseId,
                                          @RequestParam(value = "orderTime") @DateTimeFormat(pattern = "yyyy-MM-dd") Date orderTime,
                                          @RequestParam(value = "desc", required = false) String desc,
                                          @RequestParam(value = "telephone") String telephone) {
    if (orderTime == null) {
      return RainbowApiResponse.message(HttpStatus.SC_BAD_REQUEST, "请选择预约时间");
    }
    if (!LoginUserUtil.checkTelephone(telephone)) {
      return RainbowApiResponse.message(HttpStatus.SC_BAD_REQUEST, "手机格式不正确");
    }
    ServiceResult result = houseService.subscribe(houseId, orderTime, telephone, desc);
    if (result.isSuccess()) {
      return RainbowApiResponse.status(RainbowApiResponse.RespStatus.SUCCESS);
    } else {
      return RainbowApiResponse.message(HttpStatus.SC_BAD_REQUEST, result.getMessage());
    }
  }

  /**
   * <pre>取消预约</pre>
   *
   * @param houseId 房产ID
   * @return
   */
  @DeleteMapping("api/user/house/subscribe")
  @ResponseBody
  public RainbowApiResponse cancleSubscribe(@RequestParam(value = "houseId") Long houseId) {
    ServiceResult result = houseService.cancelSubscribe(houseId);
    if (result.isSuccess()) {
      return RainbowApiResponse.status(RainbowApiResponse.RespStatus.SUCCESS);
    } else {
      return RainbowApiResponse.message(HttpStatus.SC_BAD_REQUEST, result.getMessage());
    }
  }
}
