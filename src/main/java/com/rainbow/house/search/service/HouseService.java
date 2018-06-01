package com.rainbow.house.search.service;

import com.rainbow.house.search.base.ServiceMultiResult;
import com.rainbow.house.search.base.ServiceResult;
import com.rainbow.house.search.base.enums.HouseSubscribeStatusEnum;
import com.rainbow.house.search.base.rent.RentSearchCondition;
import com.rainbow.house.search.web.dto.HouseDTO;
import com.rainbow.house.search.web.dto.HouseSubscribeDTO;
import com.rainbow.house.search.web.form.DataTableSearch;
import com.rainbow.house.search.web.form.HouseForm;
import javafx.util.Pair;

import java.util.Date;

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

  /**
   * <pre>查询房间信息</pre>
   *
   * @param rentSearchCondition 租赁查询
   * @return
   */
  ServiceMultiResult<HouseDTO> queryHouses(RentSearchCondition rentSearchCondition);

  /**
   * <pre>添加预约记录</pre>
   *
   * @param houseId 房产Id
   * @return
   */
  ServiceResult addSubscribeOrder(Long houseId);

  /**
   * <pre>查询预约列表信息</pre>
   *
   * @param status 状态
   * @param start  开始记录
   * @param size   条数
   * @return
   */
  ServiceMultiResult<Pair<HouseDTO, HouseSubscribeDTO>> querySubscribeList(HouseSubscribeStatusEnum status, int start, int size);

  /**
   * <pre>预约看房时间</pre>
   *
   * @param houseId   房产Id
   * @param orderTime 预约时间
   * @param telephone 电话
   * @param desc      描述
   * @return
   */
  ServiceResult subscribe(Long houseId, Date orderTime, String telephone, String desc);

  /**
   * <pre>取消预约</pre>
   *
   * @param houseId 房产ID
   * @return
   */
  ServiceResult cancelSubscribe(Long houseId);
}
