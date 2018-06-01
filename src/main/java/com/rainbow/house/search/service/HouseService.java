package com.rainbow.house.search.service;

import com.rainbow.house.search.base.ServiceMultiResult;
import com.rainbow.house.search.base.ServiceResult;
import com.rainbow.house.search.web.dto.HouseDTO;
import com.rainbow.house.search.web.form.DataTableSearch;
import com.rainbow.house.search.web.form.HouseForm;

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

  /**
   * <pre>新增房产信息</pre>
   *
   * @param houseForm 房产对应的数据
   * @return
   */
  ServiceResult<HouseDTO> save(HouseForm houseForm);

  /**
   * 查询房产信息
   *
   * @param id 房产ID
   * @return
   */
  ServiceResult<HouseDTO> findCompleteOne(Long id);

  /**
   * <pre>更新房产信息</pre>
   *
   * @param houseForm 房产数据
   * @return
   */
  ServiceResult update(HouseForm houseForm);
}
