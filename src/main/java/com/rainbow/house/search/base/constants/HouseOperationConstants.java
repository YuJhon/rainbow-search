package com.rainbow.house.search.base.constants;

/**
 * <p>功能描述</br>房屋操作状态常量定义</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 11:06
 */
public class HouseOperationConstants {

  /**
   * 通过审核
   **/
  public static final int PASS = 1;

  /**
   * 下架，重新审核
   **/
  public static final int PULL_OUT = 2;

  /**
   * 逻辑删除
   **/
  public static final int DELETE = 3;

  /**
   * 出租
   **/
  public static final int RENT = 4;
}
