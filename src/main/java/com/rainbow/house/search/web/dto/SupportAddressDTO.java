package com.rainbow.house.search.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

/**
 * <p>功能描述</br>地址值对象</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 16:55
 */
@Data
@ToString
public class SupportAddressDTO {

  private Long id;
  @JsonProperty(value = "belong_to")
  private String belongTo;

  @JsonProperty(value = "en_name")
  private String enName;

  @JsonProperty(value = "cn_name")
  private String cnName;

  private String level;

  private Double baiduMapLongitude;

  private Double baiduMapLatitude;

}
