package com.rainbow.house.search.service.impl;

import com.rainbow.house.search.base.LoginUserUtil;
import com.rainbow.house.search.base.ServiceMultiResult;
import com.rainbow.house.search.base.ServiceResult;
import com.rainbow.house.search.base.enums.HouseStatusEnum;
import com.rainbow.house.search.entity.HouseDO;
import com.rainbow.house.search.repository.HouseRepository;
import com.rainbow.house.search.service.HouseService;
import com.rainbow.house.search.web.dto.HouseDTO;
import com.rainbow.house.search.web.form.DataTableSearch;
import com.rainbow.house.search.web.form.HouseForm;
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
      HouseDTO houseDTO = modelMapper.map(houseDO,HouseDTO.class);
      houseDTO.setCover(this.cdnPrefix + houseDO.getCover());
      houseDTOS.add(houseDTO);
    });
    return new ServiceMultiResult<>(houses.getTotalElements(), houseDTOS);
  }

  @Override
  public ServiceResult<HouseDTO> save(HouseForm houseForm) {

    return null;
  }
}
