package com.rainbow.house.search.base.enums;

/**
 * <p>功能描述</br>房屋状态枚举类</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 11:00
 */
public enum HouseStatusEnum {

  NOT_AUDITED(0, "未审核"),
  PASSES(1, "审核通过"),
  RENTED(2, "已出租"),
  DELETED(3, "逻辑删除");

  private int value;
  private String desc;

  HouseStatusEnum(int value, String desc) {
    this.value = value;
    this.desc = desc;
  }

  public int getValue() {
    return value;
  }
}
