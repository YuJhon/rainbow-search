package com.rainbow.house.search.service;

import com.rainbow.house.search.base.ServiceResult;
import com.rainbow.house.search.entity.UserDO;
import com.rainbow.house.search.web.dto.UserDTO;

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

  /**
   * <pre>通过ID查询记录</pre>
   *
   * @param id
   * @return
   */
  ServiceResult<UserDTO> findById(Long id);

  /**
   * <pre>更新用户信息</pre>
   *
   * @param profile 更新项
   * @param value   对应的值
   * @return
   */
  ServiceResult modifyUserProfile(String profile, String value);

  /**
   * <pre>通过手机号查询对应的用户</pre>
   *
   * @param telephone 手机号码
   * @return
   */
  UserDO findUserByTelephone(String telephone);

  /**
   * <pre>通过手机号添加用户信息</pre>
   *
   * @param telephone 手机号
   * @return
   */
  UserDO addUserByPhone(String telephone);
}
