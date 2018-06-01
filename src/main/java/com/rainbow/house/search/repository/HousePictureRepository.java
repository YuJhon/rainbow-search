package com.rainbow.house.search.repository;

import com.rainbow.house.search.entity.HousePictureDO;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * <p>功能描述</br>房屋图片数据访问层</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 17:24
 */
public interface HousePictureRepository extends CrudRepository<HousePictureDO, Long> {
  /**
   * <pre>通过房间ID查询所有的房产图片</pre>
   *
   * @param houseId 房间ID
   * @return
   */
  List<HousePictureDO> findAllByHouseId(Long houseId);
}
