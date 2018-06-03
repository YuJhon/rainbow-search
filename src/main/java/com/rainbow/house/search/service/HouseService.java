package com.rainbow.house.search.service;

import com.rainbow.house.search.base.ServiceMultiResult;
import com.rainbow.house.search.base.ServiceResult;
import com.rainbow.house.search.base.enums.HouseSubscribeStatusEnum;
import com.rainbow.house.search.base.rent.RentSearchCondition;
import com.rainbow.house.search.base.search.MapSearch;
import com.rainbow.house.search.web.dto.HouseDTO;
import com.rainbow.house.search.web.dto.HouseSubscribeDTO;
import com.rainbow.house.search.web.form.DataTableSearch;
import com.rainbow.house.search.web.form.HouseForm;
import org.springframework.data.util.Pair;

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

  /**
   * <pre>移除房产图片</pre>
   *
   * @param id 图片ID
   * @return
   */
  ServiceResult removePhoto(Long id);

  /**
   * <pre>审核操作</pre>
   *
   * @param id     房产ID
   * @param status 房产状态
   * @return
   */
  ServiceResult updateStatus(Long id, int status);

  /**
   * <pre>更新封面</pre>
   *
   * @param coverId  封面图片ID
   * @param targetId 更新的Id
   * @return
   */
  ServiceResult updateCover(Long coverId, Long targetId);

  /**
   * <pre>添加标签</pre>
   *
   * @param houseId 房间Id
   * @param tag     标签
   * @return
   */
  ServiceResult addTag(Long houseId, String tag);

  /**
   * <pre>移除标签</pre>
   *
   * @param houseId 房产Id
   * @param tag     标签
   * @return
   */
  ServiceResult removeTag(Long houseId, String tag);

  /**
   * <pre>完成预约</pre>
   *
   * @param houseId 房产ID
   * @return
   */
  ServiceResult finishSubscribe(Long houseId);

  /**
   * <pre>查找预约列表</pre>
   *
   * @param start 开始记录
   * @param size  查询记录数
   * @return
   */
  ServiceMultiResult<Pair<HouseDTO, HouseSubscribeDTO>> findSubscribeList(int start, int size);

  /**
   * <pre>全地图查询</pre>
   *
   * @param mapSearch 查询条件
   * @return
   */
  ServiceMultiResult<HouseDTO> wholeMapQuery(MapSearch mapSearch);

  /**
   * <pre>精确范围数据查询</pre>
   *
   * @param mapSearch 查询条件
   * @return
   */
  ServiceMultiResult<HouseDTO> boundMapQuery(MapSearch mapSearch);
}
