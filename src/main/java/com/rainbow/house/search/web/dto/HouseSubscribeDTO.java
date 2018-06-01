package com.rainbow.house.search.web.dto;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * <p>功能描述</br>预约看房值对象</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 9:58
 */
@ToString
@Data
public class HouseSubscribeDTO {

  private Long id;

  private Long houseId;

  private Long userId;

  private Long adminId;

  /** 预约状态 1-加入待看清单 2-已预约看房时间 3-看房完成 **/
  private Integer status;

  private Date createTime;

  private Date lastUpdateTime;

  private Date orderTime;

  private String telephone;

  private String desc;
}
