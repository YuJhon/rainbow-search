package com.rainbow.house.search.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * <p>功能描述</br>用户实体类</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/30 11:30
 */
@Data
@Entity
@Table(name = "t_user")
public class UserDO {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long Id;

  /**
   * 姓名
   */
  private String name;

  /**
   * 邮件
   */
  private String email;

  /**
   * 头像
   */
  private String avatar;

  /**
   * 电话号码
   */
  @Column(name = "phone_number")
  private String phoneNumber;

  /**
   * 密码
   */
  private String password;

  /**
   * 状态
   */
  private Integer status;

  /**
   * 创建时间
   */
  @Column(name = "create_time")
  private Date createTime;

  /**
   * 最后登录时间
   */
  @Column(name = "last_login_time")
  private Date lastLoginTime;

  /**
   * 最后更新时间
   */
  @Column(name = "last_update_time")
  private Date lastUpdateTime;

}
