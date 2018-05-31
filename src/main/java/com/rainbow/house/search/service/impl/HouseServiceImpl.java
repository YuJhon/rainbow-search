package com.rainbow.house.search.service.impl;

import com.rainbow.house.search.base.LoginUserUtil;
import com.rainbow.house.search.base.ServiceMultiResult;
import com.rainbow.house.search.base.ServiceResult;
import com.rainbow.house.search.base.enums.HouseStatusEnum;
import com.rainbow.house.search.entity.*;
import com.rainbow.house.search.repository.*;
import com.rainbow.house.search.service.HouseService;
import com.rainbow.house.search.web.dto.HouseDTO;
import com.rainbow.house.search.web.dto.HouseDetailDTO;
import com.rainbow.house.search.web.dto.HousePictureDTO;
import com.rainbow.house.search.web.form.DataTableSearch;
import com.rainbow.house.search.web.form.HouseForm;
import com.rainbow.house.search.web.form.PhotoForm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>功能描述</br>房间业务逻辑接口实现类</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 10:00
 */
@Service
public class HouseServiceImpl implements HouseService {

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private HouseRepository houseRepository;

  @Autowired
  private HouseDetailRepository houseDetailRepository;

  @Autowired
  private HousePictureRepository housePictureRepository;

  @Autowired
  private HouseTagRepository houseTagRepository;

  @Autowired
  private SubwayRepository subwayRepository;

  @Autowired
  private SubwayStationRepository subwayStationRepository;

  @Value("${qiniu.cdn.prefix}")
  private String cdnPrefix;

  @Override
  public ServiceMultiResult<HouseDTO> adminQuery(DataTableSearch searchBody) {
    List<HouseDTO> houseDTOS = new ArrayList<>();

    Sort sort = new Sort(Sort.Direction.fromString(searchBody.getDirection()), searchBody.getOrderBy());
    int page = searchBody.getStart() / searchBody.getLength();
    Pageable pageable = new PageRequest(page, searchBody.getLength(), sort);

    Specification<HouseDO> specification = (root, query, cb) -> {

      Predicate predicate = cb.equal(root.get("adminId"), LoginUserUtil.getLoginUserId());

      predicate = cb.and(predicate, cb.notEqual(root.get("status"), HouseStatusEnum.DELETED.getValue()));
      if (searchBody.getCity() != null) {
        predicate = cb.and(predicate, cb.equal(root.get("cityEnName"), searchBody.getCity()));
      }

      if (searchBody.getStatus() != null) {
        predicate = cb.and(predicate, cb.equal(root.get("status"), searchBody.getStatus()));
      }

      if (searchBody.getCreateTimeMin() != null) {
        predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createTime"), searchBody.getCreateTimeMin()));
      }

      if (searchBody.getCreateTimeMax() != null) {
        predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createTime"), searchBody.getCreateTimeMax()));
      }

      if (searchBody.getTitle() != null) {
        predicate = cb.and(predicate, cb.like(root.get("title"), "%" + searchBody.getTitle() + "%"));
      }

      return predicate;
    };
    Page<HouseDO> houses = houseRepository.findAll(specification, pageable);
    houses.forEach(houseDO -> {
      HouseDTO houseDTO = modelMapper.map(houseDO, HouseDTO.class);
      houseDTO.setCover(this.cdnPrefix + houseDO.getCover());
      houseDTOS.add(houseDTO);
    });
    return new ServiceMultiResult<>(houses.getTotalElements(), houseDTOS);
  }

  @Override
  public ServiceResult<HouseDTO> save(HouseForm houseForm) {
    HouseDetailDO houseDetail = new HouseDetailDO();
    /** 地铁的校验 **/
    ServiceResult<HouseDTO> subwayValidateResult = wrapperDetailInfo(houseDetail, houseForm);
    if (subwayValidateResult != null) {
      return subwayValidateResult;
    }
    HouseDO house = new HouseDO();
    modelMapper.map(houseForm, house);

    Date now = new Date();
    house.setCreateTime(now);
    house.setLastUpdateTime(now);
    house.setAdminId(LoginUserUtil.getLoginUserId());
    house = houseRepository.save(house);

    houseDetail.setHouseId(house.getId());
    houseDetail = houseDetailRepository.save(houseDetail);
    /** 生成图片对象 **/
    List<HousePictureDO> pictures = generateHousePictures(houseForm, house.getId());
    Iterable<HousePictureDO> housePictures = housePictureRepository.save(pictures);

    HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
    HouseDetailDTO houseDetailDTO = modelMapper.map(houseDetail, HouseDetailDTO.class);

    houseDTO.setHouseDetail(houseDetailDTO);

    List<HousePictureDTO> pictureDTOS = new ArrayList<>();
    housePictures.forEach(housePicture -> pictureDTOS.add(modelMapper.map(housePicture, HousePictureDTO.class)));
    houseDTO.setPictures(pictureDTOS);
    houseDTO.setCover(this.cdnPrefix + houseDTO.getCover());

    List<String> tags = houseForm.getTags();
    if (tags != null && !tags.isEmpty()) {
      List<HouseTagDO> houseTags = new ArrayList<>();
      for (String tag : tags) {
        houseTags.add(new HouseTagDO(house.getId(), tag));
      }
      houseTagRepository.save(houseTags);
      houseDTO.setTags(tags);
    }
    return new ServiceResult<HouseDTO>(true, null, houseDTO);
  }

  /**
   * <pre>组织房间图片信息</pre>
   *
   * @param houseForm 房间数据
   * @param houseId   房产ID
   * @return
   */
  private List<HousePictureDO> generateHousePictures(HouseForm houseForm, Long houseId) {
    List<HousePictureDO> pictures = new ArrayList<>();
    if (houseForm.getPhotos() != null || houseForm.getPhotos().isEmpty()) {
      return pictures;
    }

    for (PhotoForm photoForm : houseForm.getPhotos()) {
      HousePictureDO housePicture = new HousePictureDO();
      housePicture.setHouseId(houseId);
      housePicture.setCdnPrefix(cdnPrefix);
      housePicture.setPath(photoForm.getPath());
      housePicture.setWidth(photoForm.getWidth());
      housePicture.setHeight(photoForm.getHeight());
      pictures.add(housePicture);
    }
    return pictures;
  }

  /**
   * <pre>房屋信息中的地铁信息的校验</pre>
   *
   * @param houseDetail 房产详细信息
   * @param houseForm   新增房源的信息
   * @return
   */
  private ServiceResult<HouseDTO> wrapperDetailInfo(HouseDetailDO houseDetail, HouseForm houseForm) {
    SubwayDO subway = subwayRepository.findOne(houseForm.getSubwayLineId());
    if (subway == null) {
      return new ServiceResult<>(false, "Not valid subway line!");
    }
    SubwayStationDO subwayStation = subwayStationRepository.findOne(houseForm.getSubwayStationId());
    if (subwayStation == null || !subway.getId().equals(subwayStation.getSubwayId())) {
      return new ServiceResult<>(false, "Not valid subway station!");
    }
    houseDetail.setSubwayLineId(subway.getId());
    houseDetail.setSubwayLineName(subway.getName());
    houseDetail.setSubwayStationId(subwayStation.getId());
    houseDetail.setSubwayStationName(subwayStation.getName());

    houseDetail.setDescription(houseForm.getDescription());
    houseDetail.setDetailAddress(houseForm.getDetailAddress());
    houseDetail.setLayoutDesc(houseForm.getLayoutDesc());
    houseDetail.setRentWay(houseForm.getRentWay());
    houseDetail.setRoundService(houseForm.getRoundService());
    houseDetail.setTraffic(houseForm.getTraffic());
    return null;
  }
}
