package com.rainbow.house.search.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * <p>功能描述</br>房间详细信息</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 16:31
 */
@Data
@Entity
@Table(name = "t_house_detail")
public class HouseDetailDO {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "house_id")
  private Long houseId;

  private String description;

  @Column(name = "layout_desc")
  private String layoutDesc;

  private String traffic;

  @Column(name = "round_service")
  private String roundService;

  @Column(name = "rent_way")
  private Integer rentWay;

  @Column(name = "address")
  private String detailAddress;

  @Column(name = "subway_line_id")
  private Long subwayLineId;

  @Column(name = "subway_station_id")
  private Long subwayStationId;

  @Column(name = "subway_line_name")
  private String subwayLineName;

  @Column(name = "subway_station_name")
  private String subwayStationName;
}
