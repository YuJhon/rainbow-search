package com.rainbow.house.search.service;

import com.rainbow.house.search.entity.SupportAddressDO;
import com.rainbow.house.search.web.dto.SupportAddressDTO;

import java.util.Map;

/**
 * <p>功能描述</br>地址服务定义接口</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 16:58
 */
public interface SupportAddressService {
  /**
   * <pre>通过城市和区域查询地址</pre>
   *
   * @param cityEnName   中文城市名称
   * @param regionEnName 区域的英文的缩写
   * @return
   */
  Map<SupportAddressDO.Level, SupportAddressDTO> findByCityAndRegion(String cityEnName, String regionEnName);
}
