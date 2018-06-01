package com.rainbow.house.search.repository;

import com.rainbow.house.search.entity.HouseSubscribeDO;
import org.springframework.data.repository.CrudRepository;

/**
 * <p>功能描述</br>房产预约记录数据访问层</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/1 15:48
 */
public interface HouseSubscribeRepository extends CrudRepository<HouseSubscribeDO, Long> {
  /**
   * <pre>通过房间Id和订阅用户查询预约记录</pre>
   *
   * @param houseId     房间ID
   * @param loginUserId 用户ID
   * @return
   */
  HouseSubscribeDO findByHouseIdAndUserId(Long houseId, Long loginUserId);
}
