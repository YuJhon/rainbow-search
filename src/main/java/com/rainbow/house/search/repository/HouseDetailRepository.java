package com.rainbow.house.search.repository;

import com.rainbow.house.search.entity.HouseDetailDO;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * <p>功能描述</br>房产详细信息数据访问层</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 17:25
 */
public interface HouseDetailRepository extends CrudRepository<HouseDetailDO, Long> {
  /**
   * <pre>通过房间ID查询房间线下信息</pre>
   *
   * @param houseId 房间ID
   * @return
   */
  HouseDetailDO findByHouseId(Long houseId);

  /**
   * <pre>通过房产Id列表查询房产详细信息</pre>
   *
   * @param houseIds 房产Id列表
   * @return
   */
  List<HouseDetailDO> findAllByHouseIdIn(List<Long> houseIds);
}
