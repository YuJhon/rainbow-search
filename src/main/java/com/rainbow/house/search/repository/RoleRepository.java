package com.rainbow.house.search.repository;

import com.rainbow.house.search.entity.RoleDO;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * <p>功能描述</br>用户角色数据访问层</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/30 22:07
 */
public interface RoleRepository extends CrudRepository<RoleDO, Long> {
  /**
   * <pre>通过用户Id查询所有角色信息</pre>
   *
   * @param userId 用户ID
   * @return
   */
  List<RoleDO> findRolesByUserId(Long userId);
}
