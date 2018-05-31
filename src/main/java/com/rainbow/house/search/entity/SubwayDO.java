package com.rainbow.house.search.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * <p>功能描述</br>地铁</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 16:32
 */
@Entity
@Data
@Table(name = "t_subway")
public class SubwayDO {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * 地铁名称
   */
  private String name;

  /**
   * 城市名称英文缩写
   */
  @Column(name = "city_en_name")
  private String cityEnName;
}
