package com.rainbow.house.search.repository;

import com.rainbow.house.search.entity.HouseDO;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * <p>功能描述</br>房屋的数据访问层</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 10:07
 */
public interface HouseRepository extends PagingAndSortingRepository<HouseDO, Long>, JpaSpecificationExecutor<HouseDO> {

}
