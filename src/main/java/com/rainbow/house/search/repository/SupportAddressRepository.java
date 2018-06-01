package com.rainbow.house.search.repository;

import com.rainbow.house.search.entity.SupportAddressDO;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * <p>功能描述</br>地址的数据访问层</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 17:06
 */
public interface SupportAddressRepository extends CrudRepository<SupportAddressDO, Long> {

  /**
   * <pre>查询城市</pre>
   *
   * @param cityEnName 城市名称
   * @param level      行政区等级
   * @return
   */
  SupportAddressDO findByEnNameAndLevel(String cityEnName, String level);

  /**
   * <pre>查询城市中的区域</pre>
   *
   * @param regionEnName 区域名称
   * @param belongTo     所属城市
   * @return
   */
  SupportAddressDO findByEnNameAndBelongTo(String regionEnName, String belongTo);

  /**
   * <pre>通过登记查询所有的城市</pre>
   *
   * @param level
   * @return
   */
  List<SupportAddressDO> findAllByLevel(String level);

  /**
   * <pre>通过等级和所属城市获取所有地址</pre>
   *
   * @param level    等级
   * @param belongTo 所属区域
   * @return
   */
  List<SupportAddressDO> findAllByLevelAndBelongTo(String level, String belongTo);
}
