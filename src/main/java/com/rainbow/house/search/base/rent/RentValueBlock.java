package com.rainbow.house.search.base.rent;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * <p>功能描述</br>租赁条件【区间的常用数值定义】</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/1 13:57
 */
@Getter
@Setter
public class RentValueBlock {

  /** 价格区间定义 **/
  public static final Map<String, RentValueBlock> PRICE_BLOCK;
  /** 面积区间定义 **/
  public static final Map<String, RentValueBlock> AREA_BLOCK;

  /** 默认查询所有的 **/
  public static final RentValueBlock ALL = new RentValueBlock("*", -1, -1);

  /**
   * 键
   */
  private String key;
  /**
   * 最小值
   **/
  private int min;
  /**
   * 最大值
   **/
  private int max;

  public RentValueBlock(String key, int min, int max) {
    this.key = key;
    this.min = min;
    this.max = max;
  }

  static {
    /** 预设的价格区间 **/
    PRICE_BLOCK = ImmutableMap.<String, RentValueBlock>builder()
            .put("*-1000", new RentValueBlock("*-1000", -1, 1000))
            .put("1000-3000", new RentValueBlock("1000-3000", 1000, 3000))
            .put("3000-*", new RentValueBlock("3000-*", 3000, -1))
            .build();
    /** 预设的面积区间 **/
    AREA_BLOCK = ImmutableMap.<String, RentValueBlock>builder()
            .put("*-30", new RentValueBlock("*-30", -1, 30))
            .put("30-50", new RentValueBlock("30-50", 30, 50))
            .put("50-*", new RentValueBlock("50-*", 50, -1))
            .build();
  }

  /**
   * <pre>价格区间的匹配</pre>
   *
   * @param key 价格区间键
   * @return
   */
  public static RentValueBlock matchPrice(String key) {
    RentValueBlock block = PRICE_BLOCK.get(key);
    if (block == null) {
      return ALL;
    }
    return block;
  }

  /**
   * <pre>面积区间的匹配</pre>
   *
   * @param key 面积区间键
   * @return
   */
  public static RentValueBlock matchArea(String key) {
    RentValueBlock block = AREA_BLOCK.get(key);
    if (block == null) {
      return ALL;
    }
    return block;
  }
}
