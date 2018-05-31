package com.rainbow.house.search.service.impl;

import com.rainbow.house.search.entity.RoleDO;
import com.rainbow.house.search.entity.UserDO;
import com.rainbow.house.search.repository.RoleRepository;
import com.rainbow.house.search.repository.UserRepository;
import com.rainbow.house.search.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>功能描述</br>用户服务业务接口实现类</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/30 20:46
 */
@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Override
  public UserDO findByName(String userName) {
    UserDO user = userRepository.findUserByName(userName);
    if (user == null) {
      return null;
    }
    List<RoleDO> roles = roleRepository.findRolesByUserId(user.getId());
    if (roles == null || roles.isEmpty()) {
      throw new DisabledException("权限非法");
    }
    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    roles.forEach(roleDO -> grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + roleDO.getName())));
    user.setAuthorities(grantedAuthorities);
    return user;
  }
}
