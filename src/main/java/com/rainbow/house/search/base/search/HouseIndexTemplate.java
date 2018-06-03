package com.rainbow.house.search.base.search;

import com.rainbow.house.search.base.baidu.map.BaiduMapLocation;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * <p>功能描述</br>索引模板（对应Es中的索引结构）</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/2 13:33
 */
@Data
public class HouseIndexTemplate {

  private Long houseId;

  private String title;

  private int price;

  private int area;

  private Date createTime;

  private Date lastUpdateTime;

  private String cityEnName;

  private String regionEnName;

  private int direction;

  private int distanceToSubway;

  private String subwayLineName;

  private String subwayStationName;

  private String street;

  private String district;

  private String description;

  private String layoutDesc;

  private String traffic;

  private String roundService;

  private int rentWay;

  private List<String> tags;

  private List<HouseSuggest> suggest;

  private BaiduMapLocation location;
}
