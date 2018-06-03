package com.rainbow.house.search.service;

import com.rainbow.house.search.base.ServiceMultiResult;
import com.rainbow.house.search.base.ServiceResult;
import com.rainbow.house.search.base.rent.RentSearchCondition;
import com.rainbow.house.search.base.search.HouseBucketDTO;
import com.rainbow.house.search.base.search.MapSearch;

import java.util.List;

/**
 * <p>功能描述</br>ES接口定义</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/2 13:54
 */
public interface EsSearchService {
  /**
   * <pre>索引目标房源</pre>
   *
   * @param houseId 房产ID
   * @return
   */
  void indexVersionOne(Long houseId);

  /**
   * <pre>索引目标（通过消息队列来实现）</pre>
   *
   * @param houseId 房产Id
   * @param retry   重试次数
   */
  void indexVersionTwo(Long houseId, int retry);

  /**
   * <pre>移除房源索引</pre>
   *
   * @param houseId 房产ID
   * @return
   */
  void removeVersionOne(Long houseId);

  /**
   * <pre>房产的查询</pre>
   *
   * @param rentSearchCondition 房产的查询
   * @return
   */
  ServiceMultiResult<Long> query(RentSearchCondition rentSearchCondition);

  /**
   * <pre>搜索建议</pre>
   *
   * @param prefix 建议
   * @return
   */
  ServiceResult<List<String>> suggest(String prefix);

  /**
   * <pre>通过小区来聚合查询</pre>
   *
   * @param cityEnName   城市名称
   * @param regionEnName 区域名称
   * @param district     区域
   * @return
   */
  ServiceResult<Long> aggregateDistrictHouse(String cityEnName, String regionEnName, String district);

  /**
   * <pre>地图找房</pre>
   *
   * @param cityEnName 城市名称
   * @return
   */
  ServiceMultiResult<HouseBucketDTO> mapAggregate(String cityEnName);

  /**
   * <pre>地图找房</pre>
   *
   * @param mapSearch    查询条件
   * @param isWholeQuery 是否全地图查找
   * @return
   */
  ServiceMultiResult<Long> mapQuery(MapSearch mapSearch, boolean isWholeQuery);
}
