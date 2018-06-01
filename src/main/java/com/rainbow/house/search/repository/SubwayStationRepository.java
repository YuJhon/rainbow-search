package com.rainbow.house.search.repository;

import com.rainbow.house.search.entity.SubwayStationDO;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * <p>功能描述</br>地铁站数据访问层</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 17:22
 */
public interface SubwayStationRepository extends CrudRepository<SubwayStationDO, Long> {
  /**
   * <pre>通过地铁线ID查询地铁站</pre>
   *
   * @param subwayId 地铁线ID
   * @return
   */
  List<SubwayStationDO> findAllBySubwayId(Long subwayId);
}
