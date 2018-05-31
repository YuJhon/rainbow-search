package com.rainbow.house.search.web.form;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>功能描述</br>表格的筛选条件</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 9:32
 */
@Getter
@Setter
public class DataTableSearch {
  /**
   * Datatables要求回显字段
   */
  private int draw;

  /**
   * Datatables规定分页字段
   */
  private int start;

  private int length;

  private Integer status;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date createTimeMin;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date createTimeMax;

  private String city;

  private String title;

  private String direction;

  private String orderBy;
}
