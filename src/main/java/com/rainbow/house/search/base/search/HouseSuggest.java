package com.rainbow.house.search.base.search;

import lombok.Data;

/**
 * <p>功能描述</br>房产信息搜索建议</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/2 18:37
 */
@Data
public class HouseSuggest {

  private String input;

  private int weight = 10;


}
