package com.rainbow.house.search.service.impl;

import com.rainbow.house.search.entity.SupportAddressDO;
import com.rainbow.house.search.repository.SupportAddressRepository;
import com.rainbow.house.search.service.SupportAddressService;
import com.rainbow.house.search.web.dto.SupportAddressDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
}
