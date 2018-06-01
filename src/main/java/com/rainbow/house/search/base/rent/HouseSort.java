package com.rainbow.house.search.base.rent;

import com.google.common.collect.Sets;
import org.springframework.data.domain.Sort;

import java.util.Set;

/**
 * <p>功能描述</br>房间排序</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/1 16:05
 */
public class HouseSort {

  /**
   * 默认的排序字段
   **/
  public static final String DEFAULT_SORT_KEY = "lastUpdateTime";

  /**
   * 到地铁的距离
   **/
  public static final String DISTANCE_TO_SUBWAY_KEY = "distanceToSubway";

  /**
   * 排序字段的集合
   */
  private static final Set<String> SORT_KEYS = Sets.newHashSet(
          DEFAULT_SORT_KEY,
          "createTime",
          "price",
          "area",
          DISTANCE_TO_SUBWAY_KEY
  );

  /**
   * <pre>获取排序信息</pre>
   *
   * @param key          需要排序的字段
   * @param directionKey 排序的方式（ASC/DESC）
   * @return
   */
  public static Sort generateSort(String key, String directionKey) {
    /** 获取排序的字段 **/
    key = getSortKey(key);
    /** 获取排序的方式 **/
    Sort.Direction direction = Sort.Direction.fromStringOrNull(directionKey);
    if (direction == null) {
      direction = Sort.Direction.DESC;
    }
    return new Sort(direction, key);
  }

  /**
   * <pre>过滤排序字段</pre>
   *
   * @param key 原始key
   * @return
   */
  public static String getSortKey(String key) {
    if (!SORT_KEYS.contains(key)) {
      key = DEFAULT_SORT_KEY;
    }
    return key;
  }

}
