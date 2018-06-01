package com.rainbow.house.search.repository;

import com.rainbow.house.search.entity.SubwayDO;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * <p>功能描述</br>地铁线数据访问层</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 17:21
 */
public interface SubwayRepository extends CrudRepository<SubwayDO, Long> {
  /**
   * <pre>通过城市查询所有的地铁线</pre>
   *
   * @param cityEnName 城市名称
   * @return
   */
  List<SubwayDO> findAllByCityEnName(String cityEnName);
}
