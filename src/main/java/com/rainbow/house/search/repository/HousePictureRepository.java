package com.rainbow.house.search.repository;

import com.rainbow.house.search.entity.HousePictureDO;
import org.springframework.data.repository.CrudRepository;

/**
 * <p>功能描述</br>房屋图片数据访问层</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 17:24
 */
public interface HousePictureRepository extends CrudRepository<HousePictureDO, Long> {
}
