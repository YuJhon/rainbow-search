package com.rainbow.house.search.base.enums;

import lombok.Getter;

/**
 * <p>功能描述</br>用户状态 0-正常 1-封禁</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/4 17:07
 */
@Getter
public enum UserStatusEnum {
  /**
   * 正常
   **/
  NORMAL(0),
  /**
   * 冻结
   **/
  FROZEN(1);

  UserStatusEnum(int value) {
    this.value = value;
  }

  /**
   * 键值
   **/
  private int value;
}
