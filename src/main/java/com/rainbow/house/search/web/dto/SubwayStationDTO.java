package com.rainbow.house.search.web.dto;

import lombok.Data;

/**
 * <p>功能描述</br>地铁站值转换对象</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/1 11:03
 */
@Data
public class SubwayStationDTO {
  private Long id;
  private Long subwayId;
  private String name;
}
