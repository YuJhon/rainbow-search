package com.rainbow.house.search.base;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * <p>功能描述</br>多结果返回实体的封装</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 8:59
 */
@Getter
@Setter
public class ServiceMultiResult<T> {

  /**
   * 总的记录数
   */
  private long total;
  /**
   * 记录列表
   */
  private List<T> results;

  /**
   * <pre>构造器</pre>
   *
   * @param total   总记录数
   * @param results 返回结果集
   */
  public ServiceMultiResult(long total, List<T> results) {
    this.total = total;
    this.results = results;
  }

  public int getResultSize() {
    if (this.results == null) {
      return 0;
    }
    return this.results.size();
  }
}
