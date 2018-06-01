package com.rainbow.house.search.service.impl;

import com.google.common.collect.Maps;
import com.rainbow.house.search.base.LoginUserUtil;
import com.rainbow.house.search.base.ServiceMultiResult;
import com.rainbow.house.search.base.ServiceResult;
import com.rainbow.house.search.base.enums.HouseStatusEnum;
import com.rainbow.house.search.base.rent.HouseSort;
import com.rainbow.house.search.base.rent.RentSearchCondition;
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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
  private HouseSubscribeRepository houseSubscribeRepository;

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
    return new ServiceResult<>(true, null, houseDTO);
  }

  @Override
  public ServiceResult<HouseDTO> findCompleteOne(Long id) {
    /** 1.查询房间信息 **/
    HouseDO house = houseRepository.findOne(id);
    if (house == null) {
      return ServiceResult.notFound();
    }
    Long houseId = house.getId();
    /** 2.查询房间详细信息 **/
    HouseDetailDO detailDO = houseDetailRepository.findByHouseId(houseId);
    HouseDetailDTO detailDTO = modelMapper.map(detailDO, HouseDetailDTO.class);
    /** 3.查询房间的图片 **/
    List<HousePictureDO> housePictures = housePictureRepository.findAllByHouseId(houseId);
    List<HousePictureDTO> housePictureDTOS = new ArrayList<>();
    housePictures.forEach(housePictureDO -> {
      housePictureDTOS.add(modelMapper.map(housePictureDO, HousePictureDTO.class));
    });
    /** 4.查询房间的标签**/
    List<HouseTagDO> houseTags = houseTagRepository.findAllByHouseId(houseId);
    List<String> tagList = new ArrayList<>();
    houseTags.forEach(houseTagDO -> {
      tagList.add(houseTagDO.getName());
    });

    HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
    houseDTO.setTags(tagList);
    houseDTO.setPictures(housePictureDTOS);
    houseDTO.setHouseDetail(detailDTO);

    /** 4.获取当前登陆用户的信息 **/
    if (LoginUserUtil.getLoginUserId() > 0) {
      HouseSubscribeDO houseSubscribe = houseSubscribeRepository.findByHouseIdAndUserId(houseId, LoginUserUtil.getLoginUserId());
      if (houseSubscribe != null) {
        houseDTO.setSubscribeStatus(houseSubscribe.getStatus());
      }
    }

    return ServiceResult.result(houseDTO);
  }

  @Override
  @Transactional
  public ServiceResult update(HouseForm houseForm) {
    // TODO

    return null;
  }

  @Override
  public ServiceMultiResult<HouseDTO> queryHouses(RentSearchCondition rentSearchCondition) {
    // TODO
    return simpleQuery(rentSearchCondition);
  }

  /**
   * <pre>数据库简单查询</pre>
   *
   * @param rentSearchCondition 查询条件
   * @return
   */
  private ServiceMultiResult<HouseDTO> simpleQuery(RentSearchCondition rentSearchCondition) {
    List<HouseDTO> houseDTOS = new ArrayList<>();
    Sort sort = HouseSort.generateSort(rentSearchCondition.getOrderBy(), rentSearchCondition.getOrderDirection());
    int page = rentSearchCondition.getStart() / rentSearchCondition.getSize();

    Pageable pageable = new PageRequest(page, rentSearchCondition.getSize(), sort);

    Specification specification = (root, query, cb) -> {
      Predicate predicate = cb.equal(root.get("status"), HouseStatusEnum.PASSES.getValue());

      predicate = cb.and(predicate, cb.equal(root.get("cityEnName"), rentSearchCondition.getCityEnName()));

      if (HouseSort.DISTANCE_TO_SUBWAY_KEY.equals(rentSearchCondition.getOrderBy())) {
        predicate = cb.and(predicate, cb.gt(root.get(HouseSort.DISTANCE_TO_SUBWAY_KEY), -1));
      }
      return predicate;
    };
    Page<HouseDO> houses = houseRepository.findAll(specification, pageable);
    List<Long> houseIds = new ArrayList<>();
    Map<Long, HouseDTO> idToHouseMap = Maps.newHashMap();
    houses.forEach(houseDO -> {
      HouseDTO houseDTO = modelMapper.map(houseDO, HouseDTO.class);
      houseDTO.setCover(this.cdnPrefix + houseDO.getCover());
      houseDTOS.add(houseDTO);

      houseIds.add(houseDO.getId());
      idToHouseMap.put(houseDO.getId(), houseDTO);
    });
    /** 房产信息的封装 **/
    wrapperHouseList(houseIds, idToHouseMap);
    return new ServiceMultiResult<>(houses.getTotalElements(), houseDTOS);
  }

  /**
   * <pre>房产信息的包装</pre>
   *
   * @param houseIds     房产Id
   * @param idToHouseMap 房产ID和房产的对应关系
   */
  private void wrapperHouseList(List<Long> houseIds, Map<Long, HouseDTO> idToHouseMap) {
    /** 房产详细信息 **/
    List<HouseDetailDO> details = houseDetailRepository.findAllByHouseIdIn(houseIds);
    details.forEach(houseDetailDO -> {
      HouseDTO houseDTO = idToHouseMap.get(houseDetailDO.getHouseId());
      HouseDetailDTO detailDTO = modelMapper.map(houseDetailDO, HouseDetailDTO.class);
      houseDTO.setHouseDetail(detailDTO);
    });
    /** 房产标签 **/
    List<HouseTagDO> houseTags = houseTagRepository.findAllbyHouseIdIn(houseIds);
    houseTags.forEach(houseTagDO -> {
      HouseDTO houseDTO = idToHouseMap.get(houseTagDO.getHouseId());
      houseDTO.getTags().add(houseTagDO.getName());
    });
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
