package com.rainbow.house.search.base.search;

import lombok.Data;

/**
 * <p>功能描述</br>聚合数据载体</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/3 11:00
 */
@Data
public class HouseBucketDTO {

  /**
   * 聚合bucket的key
   */
  private String key;

  /**
   * 聚合结果
   */
  private long count;

  /**
   * <pre>构造函数</pre>
   *
   * @param keyAsString
   * @param docCount
   */
  public HouseBucketDTO(String keyAsString, long docCount) {
    this.key = keyAsString;
    this.count = docCount;
  }
}
