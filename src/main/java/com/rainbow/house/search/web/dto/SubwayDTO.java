package com.rainbow.house.search.web.dto;

import lombok.Data;
import lombok.ToString;

/**
 * <p>功能描述</br>地铁线值转换对象</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/1 11:03
 */
@Data
@ToString
public class SubwayDTO {
  private Long id;
  private String name;
  private String cityEnName;
}
