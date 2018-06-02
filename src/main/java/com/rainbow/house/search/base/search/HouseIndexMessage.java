package com.rainbow.house.search.base.search;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>功能描述</br></p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/2 15:47
 */
@Getter
@Setter
@ToString
public class HouseIndexMessage {

  public static final String INDEX = "index";
  public static final String REMOVE = "remove";

  public static final int MAX_RETRY = 3;

  private Long houseId;
  private String operation;
  private int retry = 0;

  /**
   * 默认构造器 防止jackson序列化失败
   */
  public HouseIndexMessage() {
  }

  /**
   * <pre>有参数的构造器</pre>
   *
   * @param houseId
   * @param operation
   * @param retry
   */
  public HouseIndexMessage(Long houseId, String operation, int retry) {
    this.houseId = houseId;
    this.operation = operation;
    this.retry = retry;
  }
}
