package com.rainbow.house.search.service.impl;

import com.rainbow.house.search.base.ServiceMultiResult;
import com.rainbow.house.search.base.ServiceResult;
import com.rainbow.house.search.entity.SubwayDO;
import com.rainbow.house.search.entity.SupportAddressDO;
import com.rainbow.house.search.repository.SupportAddressRepository;
import com.rainbow.house.search.service.SupportAddressService;
import com.rainbow.house.search.web.dto.SubwayDTO;
import com.rainbow.house.search.web.dto.SubwayStationDTO;
import com.rainbow.house.search.web.dto.SupportAddressDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>功能描述</br>地址服务定义接口实现类</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 16:58
 */
@Service
public class SupportAddressServiceImpl implements SupportAddressService {

  @Autowired
  private SupportAddressRepository supportAddressRepository;

  @Autowired
  private ModelMapper modelMapper;

  @Override
  public Map<SupportAddressDO.Level, SupportAddressDTO> findByCityAndRegion(String cityEnName, String regionEnName) {

    Map<SupportAddressDO.Level, SupportAddressDTO> result = new HashMap<>();
    /** 查询城市信息 **/
    SupportAddressDO city = supportAddressRepository.findByEnNameAndLevel(cityEnName, SupportAddressDO.Level.CITY.getValue());
    /** 查询城市对应的区域信息 **/
    SupportAddressDO region = supportAddressRepository.findByEnNameAndBelongTo(regionEnName, city.getEnName());

    result.put(SupportAddressDO.Level.CITY, modelMapper.map(city, SupportAddressDTO.class));
    result.put(SupportAddressDO.Level.REGION, modelMapper.map(region, SupportAddressDTO.class));
    return result;
  }

  @Override
  public ServiceMultiResult<SupportAddressDTO> findAllCities() {
    String level = SupportAddressDO.Level.CITY.getValue();
    List<SupportAddressDO> cities = supportAddressRepository.findAllByLevel(level);
    List<SupportAddressDTO> cityDTOList = new ArrayList<>();
    cities.forEach(supportAddressDO -> {
      SupportAddressDTO addressDTO = modelMapper.map(supportAddressDO, SupportAddressDTO.class);
      cityDTOList.add(addressDTO);
    });
    return new ServiceMultiResult<>(cityDTOList.size(), cityDTOList);
  }

  @Override
  public ServiceMultiResult<SupportAddressDTO> findAllRegionsByCityName(String cityEnName) {
    if (cityEnName == null || "".equals(cityEnName)) {
      return new ServiceMultiResult<>(0, null);
    }
    String level = SupportAddressDO.Level.REGION.getValue();
    String belongTo = cityEnName;
    List<SupportAddressDTO> results = new ArrayList<>();
    List<SupportAddressDO> regions = supportAddressRepository.findAllByLevelAndBelongTo(level, belongTo);
    regions.forEach(supportAddressDO -> {
      results.add(modelMapper.map(supportAddressDO, SupportAddressDTO.class));
    });
    return new ServiceMultiResult<>(results.size(), results);
  }

  @Override
  public ServiceResult<SupportAddressDTO> findCity(String cityEnName) {
    if (cityEnName == null || "".equals(cityEnName)) {
      return ServiceResult.notFound();
    }
    String level = SupportAddressDO.Level.CITY.getValue();
    SupportAddressDO city = supportAddressRepository.findAllByLevelAndEnName(level, cityEnName);
    if (city == null) {
      return ServiceResult.notFound();
    }
    SupportAddressDTO supportAddressDTO = modelMapper.map(city, SupportAddressDTO.class);
    return ServiceResult.result(supportAddressDTO);
  }
}
