package com.rainbow.house.search.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

/**
 * <p>功能描述</br>房间图片值对象</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 9:58
 */
@ToString
@Data
public class HousePictureDTO {

  private Long id;

  @JsonProperty(value = "house_id")
  private Long houseId;

  private String path;

  @JsonProperty(value = "cdn_prefix")
  private String cdnPrefix;

  private Integer width;

  private Integer height;
}
