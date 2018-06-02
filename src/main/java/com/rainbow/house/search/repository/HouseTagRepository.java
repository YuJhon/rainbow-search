package com.rainbow.house.search.repository;

import com.rainbow.house.search.entity.HouseTagDO;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * <p>功能描述</br>房产标签数据访问层接口</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 17:23
 */
public interface HouseTagRepository extends CrudRepository<HouseTagDO, Long> {
  /**
   * <pre>通过房间Id查询房间标签</pre>
   *
   * @param houseId 房间ID
   * @return
   */
  List<HouseTagDO> findAllByHouseId(Long houseId);

  /**
   * <pre>通过房产Id批量查询房产标签</pre>
   *
   * @param houseIds 房产Id列表
   * @return
   */
  List<HouseTagDO> findAllByHouseIdIn(List<Long> houseIds);

  /**
   * <pre>通过标签名和房产id查找房屋标签</pre>
   *
   * @param tag     标签名称
   * @param houseId 房产ID
   * @return
   */
  HouseTagDO findByNameAndHouseId(String tag, Long houseId);
}
