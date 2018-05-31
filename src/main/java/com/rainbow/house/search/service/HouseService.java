package com.rainbow.house.search.service;

import com.rainbow.house.search.base.ServiceMultiResult;
import com.rainbow.house.search.web.dto.HouseDTO;
import com.rainbow.house.search.web.form.DataTableSearch;

/**
 * <p>功能描述</br>房间业务逻辑接口定义</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 10:00
 */
public interface HouseService {

  /**
   * <pre>查询房产信息</pre>
   *
   * @param searchBody 过滤条件
   * @return
   */
  ServiceMultiResult<HouseDTO> adminQuery(DataTableSearch searchBody);
}
