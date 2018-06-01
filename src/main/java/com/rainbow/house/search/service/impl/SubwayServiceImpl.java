package com.rainbow.house.search.service.impl;

import com.rainbow.house.search.base.ServiceResult;
import com.rainbow.house.search.entity.SubwayDO;
import com.rainbow.house.search.entity.SubwayStationDO;
import com.rainbow.house.search.repository.SubwayRepository;
import com.rainbow.house.search.repository.SubwayStationRepository;
import com.rainbow.house.search.service.SubwayService;
import com.rainbow.house.search.web.dto.SubwayDTO;
import com.rainbow.house.search.web.dto.SubwayStationDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>功能描述</br>地铁线业务逻辑接口实现类</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/1 10:58
 */
@Service
public class SubwayServiceImpl implements SubwayService {

  @Autowired
  private SubwayRepository subwayRepository;

  @Autowired
  private SubwayStationRepository subwayStationRepository;

  @Autowired
  private ModelMapper modelMapper;

  @Override
  public List<SubwayDTO> findAllSubwayByCity(String cityEnName) {
    List<SubwayDO> subwayList = subwayRepository.findAllByCityEnName(cityEnName);
    List<SubwayDTO> result = new ArrayList<>();
    subwayList.forEach(subwayDO -> {
      result.add(modelMapper.map(subwayDO, SubwayDTO.class));
    });
    return result;
  }

  @Override
  public List<SubwayStationDTO> findAllSubwayStationBySubwayId(Long subwayId) {
    List<SubwayStationDO> subwayStationList = subwayStationRepository.findAllBySubwayId(subwayId);
    List<SubwayStationDTO> result = new ArrayList<>();
    subwayStationList.forEach(subwayStationDO -> {
      result.add(modelMapper.map(subwayStationDO, SubwayStationDTO.class));
    });
    return result;
  }

  @Override
  public ServiceResult<SubwayDTO> findSubway(Long subwayLineId) {
    SubwayDO subwayDO = subwayRepository.findOne(subwayLineId);
    if (subwayDO == null) {
      return ServiceResult.notFound();
    }
    SubwayDTO subwayDTO = modelMapper.map(subwayDO, SubwayDTO.class);
    return ServiceResult.result(subwayDTO);
  }

  @Override
  public ServiceResult<SubwayStationDTO> findSubwayStation(Long subwayStationId) {
    SubwayStationDO subwayStation = subwayStationRepository.findOne(subwayStationId);
    if (subwayStation == null) {
      return ServiceResult.notFound();
    }
    SubwayStationDTO subwayStationDTO = modelMapper.map(subwayStation, SubwayStationDTO.class);
    return ServiceResult.result(subwayStationDTO);
  }
}
