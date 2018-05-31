package com.rainbow.house.search.service;

import com.rainbow.house.search.entity.UserDO;

/**
 * <p>功能描述</br>用户服务接口定义</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/30 20:46
 */
public interface UserService {
  /**
   * <pre>通过用户名查询用户</pre>
   *
   * @param userName 用户名
   * @return
   */
  UserDO findByName(String userName);
}
