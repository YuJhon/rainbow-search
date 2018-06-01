package com.rainbow.house.search.web.controller.address;

import com.rainbow.house.search.base.RainbowApiResponse;
import com.rainbow.house.search.base.ServiceMultiResult;
import com.rainbow.house.search.service.SubwayService;
import com.rainbow.house.search.service.SupportAddressService;
import com.rainbow.house.search.web.dto.SubwayDTO;
import com.rainbow.house.search.web.dto.SubwayStationDTO;
import com.rainbow.house.search.web.dto.SupportAddressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>功能描述</br>地址控制器</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/1 10:32
 */
@RestController
@RequestMapping("address")
public class AddressController {

  @Autowired
  private SupportAddressService supportAddressService;

  @Autowired
  private SubwayService subwayService;

  /**
   * <pre>获取支持的城市列表信息</pre>
   *
   * @return
   */
  @GetMapping("/support/cities")
  public RainbowApiResponse getSupportCities() {
    ServiceMultiResult<SupportAddressDTO> result = supportAddressService.findAllCities();
    if (result.getResultSize() == 0) {
      return RainbowApiResponse.status(RainbowApiResponse.RespStatus.NOT_FOUND);
    }
    return RainbowApiResponse.success(result.getResults());
  }

  /**
   * <pre>获取城市下面支持的区域信息</pre>
   *
   * @param cityEnName 城市名称
   * @return
   */
  @GetMapping("/support/regions")
  public RainbowApiResponse getSupportRegions(@RequestParam("city_name") String cityEnName) {
    ServiceMultiResult<SupportAddressDTO> result = supportAddressService.findAllRegionsByCityName(cityEnName);
    if (result.getResults() == null || result.getTotal() < 1) {
      return RainbowApiResponse.status(RainbowApiResponse.RespStatus.NOT_FOUND);
    }
    return RainbowApiResponse.success(result.getResults());
  }

  /**
   * <pre>查询所属城市下的地铁线</pre>
   *
   * @param cityEnName 城市名称
   * @return
   */
  @GetMapping("/support/subway/line")
  public RainbowApiResponse getSupportSubwayLines(@RequestParam("city_name") String cityEnName) {
    List<SubwayDTO> subwayList = subwayService.findAllSubwayByCity(cityEnName);
    if (subwayList == null || subwayList.isEmpty()) {
      return RainbowApiResponse.status(RainbowApiResponse.RespStatus.NOT_FOUND);
    }
    return RainbowApiResponse.success(subwayList);
  }

  /**
   * <pre>获取地铁站信息</pre>
   *
   * @param subwayId 地铁线ID
   * @return
   */
  @GetMapping("/support/subway/station")
  public RainbowApiResponse getSupportSubwayStations(@RequestParam("subway_id") Long subwayId) {
    List<SubwayStationDTO> subwayStations = subwayService.findAllSubwayStationBySubwayId(subwayId);
    if (subwayStations == null || subwayStations.isEmpty()) {
      return RainbowApiResponse.status(RainbowApiResponse.RespStatus.NOT_FOUND);
    }
    return RainbowApiResponse.success(subwayStations);
  }
}
