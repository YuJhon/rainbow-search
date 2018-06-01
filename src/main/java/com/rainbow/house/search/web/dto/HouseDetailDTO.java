package com.rainbow.house.search.web.dto;

import lombok.Data;
import lombok.ToString;

/**
 * <p>功能描述</br>房间详细信息值对象</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 9:58
 */
@ToString
@Data
public class HouseDetailDTO {

  private String description;

  private String layoutDesc;

  private String traffic;

  private String roundService;

  private Integer rentWay;

  private Long adminId;

  private String address;

  private Long subwayLineId;

  private Long subwayStationId;

  private String subwayLineName;

  private String subwayStationName;
}
