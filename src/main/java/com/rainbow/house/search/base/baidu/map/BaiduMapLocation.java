package com.rainbow.house.search.base.baidu.map;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>功能描述</br>百度位置信息</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/3 11:52
 */
@Data
public class BaiduMapLocation {

  @JsonProperty("lon")
  private double longitude;

  @JsonProperty("lat")
  private double latitude;
}
