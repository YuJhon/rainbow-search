package com.rainbow.house.search.base.search;

import lombok.Data;

/**
 * <p>功能描述</br>地图找房</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/3 11:27
 */
@Data
public class MapSearch {

  private String cityEnName;

  /**
   * 地图缩放级别
   */
  private int level = 12;
  private String orderBy = "lastUpdateTime";
  private String orderDirection = "desc";
  /**
   * 左上角
   */
  private Double leftLongitude;
  private Double leftLatitude;

  /**
   * 右下角
   */
  private Double rightLongitude;
  private Double rightLatitude;

  private int start = 0;
  private int size = 5;
}
