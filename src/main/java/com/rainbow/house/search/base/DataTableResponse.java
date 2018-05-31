package com.rainbow.house.search.base;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>功能描述</br>DataTable响应结果的封装</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 9:23
 */
@Getter
@Setter
public class DataTableResponse extends RainbowApiResponse {

  private int draw;

  private long recordsTotal;

  private long recordsFiltered;


  public DataTableResponse(RainbowApiResponse.RespStatus status) {
    this(status.getCode(), status.getStandardMessage(), null);
  }

  public DataTableResponse(int code, String message, Object data) {
    super(code, message, data);
  }
}
