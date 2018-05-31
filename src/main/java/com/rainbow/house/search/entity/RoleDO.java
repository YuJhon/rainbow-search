package com.rainbow.house.search.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * <p>功能描述</br></p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/30 22:03
 */
@Data
@Table(name = "t_role")
@Entity
public class RoleDO {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * 用户ID
   */
  @Column(name = "user_id")
  private Long userId;

  /**
   * 用户名
   */
  private String name;
}
