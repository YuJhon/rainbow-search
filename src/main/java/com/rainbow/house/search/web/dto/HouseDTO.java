package com.rainbow.house.search.web.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>功能描述</br>房间值对象</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 9:57
 */
@ToString
@Data
public class HouseDTO implements Serializable {

  private Long id;

  private String title;

  private Integer price;

  private Integer area;

  private Integer direction;

  private Integer room;

  private Integer parlour;

  private Integer bathroom;

  private Integer floor;

  private Integer adminId;

  private String district;

  private Integer totalFloor;

  private Integer watchTimes;

  private Integer buildYear;

  private Integer status;

  private Date createTime;

  private Date lastUpdateTime;

  private String cityEnName;

  private String regionEnName;

  private String street;

  private String cover;

  private Integer distanceToSubway;

  private HouseDetailDTO houseDetail;

  private List<String> tags;

  private List<HousePictureDTO> pictures;

  private Integer subscribeStatus;
}
