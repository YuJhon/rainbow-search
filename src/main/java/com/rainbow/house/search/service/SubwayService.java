package com.rainbow.house.search.service;

import com.rainbow.house.search.base.ServiceResult;
import com.rainbow.house.search.web.dto.SubwayDTO;
import com.rainbow.house.search.web.dto.SubwayStationDTO;

import java.util.List;

/**
 * <p>功能描述</br>地铁线服务类</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/1 10:52
 */
public interface SubwayService {
  /**
   * <pre>查询城市下属的地铁线</pre>
   *
   * @param cityEnName 城市名称
   * @return
   */
  List<SubwayDTO> findAllSubwayByCity(String cityEnName);

  /**
   * <pre>查询地铁线下的所有地铁站</pre>
   *
   * @param subwayId 地铁线ID
   * @return
   */
  List<SubwayStationDTO> findAllSubwayStationBySubwayId(Long subwayId);

  /**
   * <pre>查询地铁线</pre>
   *
   * @param subwayLineId 地铁线ID
   * @return
   */
  ServiceResult<SubwayDTO> findSubway(Long subwayLineId);

  /**
   * <pre>查询地铁站</pre>
   *
   * @param subwayStationId 地铁站ID
   * @return
   */
  ServiceResult<SubwayStationDTO> findSubwayStation(Long subwayStationId);

}
