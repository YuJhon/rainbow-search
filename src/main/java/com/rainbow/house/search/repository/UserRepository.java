package com.rainbow.house.search.repository;

import com.rainbow.house.search.entity.UserDO;
import org.springframework.data.repository.CrudRepository;

/**
 * <p>功能描述</br>用户数据访问层</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/30 11:42
 */
public interface UserRepository extends CrudRepository<UserDO, Long> {

}
