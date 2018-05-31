package com.rainbow.house.search.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * <p>功能描述</br>地铁站</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 16:33
 */
@Table(name = "t_subway_station")
@Entity
@Data
public class SubwayStationDO {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * 地铁名称
   */
  @Column(name = "subway_id")
  private Long subwayId;

  /**
   * 地铁站名称
   */
  private String name;

}
