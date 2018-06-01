package com.rainbow.house.search.base.rent;

import lombok.Data;

/**
 * <p>功能描述</br>租房的查询条件</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/1 14:17
 */
@Data
public class RentSearchCondition {

  /**
   * 默认的租赁方式（不限）
   **/
  private static final int DEFAULT_RENT_VAL = -1;
  /**
   * 租赁方式的最大值边界
   **/
  private static final int MAX_LIMIT_RENT_VAL = 2;
  /**
   * 租赁方式的最小值边界
   **/
  private static final int MIN_LIMIT_RENT_VAL = -2;
  /**
   * 默认每页显示的记录数
   **/
  private static final int DEFAULT_PAGE_SIZE = 5;
  /**
   * 每页显示的最大记录数
   **/
  private static final int MAX_PAGE_SIZE = 100;

  /**
   * 城市名称
   **/
  private String cityEnName;
  /**
   * 区域名称
   **/
  private String regionEnName;
  /**
   * 价格区域
   **/
  private String priceBlock;
  /**
   * 面积区间
   **/
  private String areaBlock;
  /**
   * 房间数
   **/
  private int room;
  /**
   * 朝向
   **/
  private int direction;
  /**
   * 关键词
   **/
  private String keywords;
  /**
   * 租赁方式
   **/
  private int rentWay = -1;
  /**
   * 排序字段
   **/
  private String orderBy = "lastUpdateTime";
  /**
   * 排序方式
   **/
  private String orderDirection = "desc";

  /**
   * 记录开始序号
   **/
  private int start = 0;
  /**
   * 每页显示的条数
   **/
  private int size = DEFAULT_PAGE_SIZE;

  /**
   * @return
   */
  public int getStart() {
    return start > 0 ? start : 0;
  }

  /**
   * <pre>获取每页的大小</pre>
   *
   * @return
   */
  public int getSize() {
    if (this.size < 1) {
      return DEFAULT_PAGE_SIZE;
    } else if (this.size > MAX_PAGE_SIZE) {
      return MAX_PAGE_SIZE;
    } else {
      return this.size;
    }
  }

  /**
   * <pre>获取租赁方式</pre>
   *
   * @return
   */
  public int getRentWay() {
    if (rentWay > MIN_LIMIT_RENT_VAL && rentWay < MAX_LIMIT_RENT_VAL) {
      return rentWay;
    } else {
      return -1;
    }
  }

}
