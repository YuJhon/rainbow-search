package com.rainbow.house.search.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * <p>功能描述</br>房屋标签</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 16:30
 */
@Entity
@Data
@Table(name = "t_house_tag")
public class HouseTagDO {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * 房产主键
   */
  @Column(name = "house_id")
  private Long houseId;

  /**
   * 标签名称
   */
  private String name;

}
