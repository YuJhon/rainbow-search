package com.rainbow.house.search.web.dto;

import lombok.Data;

/**
 * <p>功能描述</br>用户信息值对象</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/1 19:54
 */
@Data
public class UserDTO {
  /**
   * 用户ID
   */
  private Long id;
  /**
   * 用户姓名
   */
  private String name;
  /**
   * 用户头像信息
   */
  private String avatar;
  /**
   * 电话号码
   */
  private String phoneNumber;
  /**
   * 最后一次登录时间
   */
  private String lastLoginTime;
}
