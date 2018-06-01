package com.rainbow.house.search.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * <p>功能描述</br>房间图片</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 16:31
 */
@Data
@Entity
@Table(name = "t_house_picture")
public class HousePictureDO {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * 房产Id
   */
  @Column(name = "house_id")
  private Long houseId;
  /**
   * 路径
   */
  private String path;

  /**
   * 图片的cdn路径
   */
  @Column(name = "cdn_prefix")
  private String cdnPrefix;
  /**
   * 图片宽度
   */
  private Integer width;
  /**
   * 图片高度
   */
  private Integer height;
  /**
   * 位置
   */
  private String location;
}
