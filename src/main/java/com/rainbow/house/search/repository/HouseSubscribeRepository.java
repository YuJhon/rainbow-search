package com.rainbow.house.search.repository;

import com.rainbow.house.search.entity.HouseSubscribeDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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

  /**
   * <pre>分页查询记录</pre>
   *
   * @param adminId  用户Id
   * @param status   状态
   * @param pageable 分页参数
   * @return
   */
  Page<HouseSubscribeDO> findAllByAdminIdAndStatus(Long adminId, int status, Pageable pageable);

  /**
   * <pre>通过房间Id和房产主id查询对应的预约记录</pre>
   *
   * @param houseId 房产Id
   * @param adminId 房源主人ID
   * @return
   */
  HouseSubscribeDO findByHouseIdAndAdminId(Long houseId, Long adminId);

  /**
   * <pre>更新预约状态</pre>
   *
   * @param id     预约记录Id
   * @param status 更新状态
   */
  @Modifying
  @Query("update HouseSubscribeDO as subscribe set subscribe.status = :status where subscribe.id = :id")
  void updateStatus(Long id, int status);
}
