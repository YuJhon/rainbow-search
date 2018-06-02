package com.rainbow.house.search.repository;

import com.rainbow.house.search.entity.HouseDO;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * <p>功能描述</br>房屋的数据访问层</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 10:07
 */
public interface HouseRepository extends PagingAndSortingRepository<HouseDO, Long>, JpaSpecificationExecutor<HouseDO> {

  /**
   * <pre>更新房屋状态</pre>
   *
   * @param id     房产Id
   * @param status 房屋状态
   */
  @Modifying
  @Query("update t_house as house set house.status = :status where house.id = :id")
  void updateStatus(@Param(value = "id") Long id, @Param(value = "status") int status);

  /**
   * <pre>更新房产封面</pre>
   *
   * @param id    房产Id
   * @param cover 封面
   */
  @Modifying
  @Query("update t_house as house set house.cover = :cover where house.id = :id")
  void updateCover(@Param(value = "id") Long id, @Param(value = "cover") String cover);

  /**
   * <pre>更新看房次数</pre>
   *
   * @param houseId 房产Id
   */
  @Modifying
  @Query("update t_house as house set house.watchTimes = house.watchTimes + 1 where house.id = :id")
  void updateWatchTimes(@Param(value = "id") Long houseId);
}
