package com.rainbow.house.search.repository;

import com.rainbow.house.search.entity.UserDO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * <p>功能描述</br>用户数据访问层</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/30 11:42
 */
public interface UserRepository extends CrudRepository<UserDO, Long> {

  /**
   * <pre>通过用户名查询用户</pre>
   *
   * @param userName 用户名
   * @return
   */
  UserDO findUserByName(String userName);

  /**
   * <pre>更新用户姓名</pre>
   *
   * @param id   用户id
   * @param name 用户姓名
   */
  @Modifying
  @Query("update UserDO as user set user.name = :name where id = :id")
  void updateUsername(@Param(value = "id") Long id, @Param(value = "name") String name);

  /**
   * <pre>更新邮箱</pre>
   *
   * @param id    用户id
   * @param email 用户邮箱
   */
  @Modifying
  @Query("update UserDO as user set user.email = :email where id = :id")
  void updateEmail(@Param(value = "id") Long id, @Param(value = "email") String email);

  /**
   * <pre>更新密码</pre>
   *
   * @param id       用户Id
   * @param password 用户密码
   */
  @Modifying
  @Query("update UserDO as user set user.password = :password where id = :id")
  void updatePassword(@Param(value = "id") Long id, @Param(value = "password") String password);

  /**
   * <pre>通过手机号查询对应的用户信息</pre>
   *
   * @param telephone 手机号码
   * @return
   */
  UserDO findUserByPhoneNumber(String telephone);
}
