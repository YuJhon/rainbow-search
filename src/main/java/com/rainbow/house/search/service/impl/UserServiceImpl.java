package com.rainbow.house.search.service.impl;

import com.google.common.collect.Lists;
import com.rainbow.house.search.base.LoginUserUtil;
import com.rainbow.house.search.base.ServiceResult;
import com.rainbow.house.search.entity.RoleDO;
import com.rainbow.house.search.entity.UserDO;
import com.rainbow.house.search.repository.RoleRepository;
import com.rainbow.house.search.repository.UserRepository;
import com.rainbow.house.search.service.UserService;
import com.rainbow.house.search.web.dto.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.Date;
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

  @Autowired
  private ModelMapper modelMapper;

  private final Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();

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

  @Override
  public ServiceResult<UserDTO> findById(Long id) {
    UserDO user = userRepository.findOne(id);
    UserDTO userDTO = modelMapper.map(user, UserDTO.class);
    return ServiceResult.result(userDTO);
  }

  @Override
  @Transactional
  public ServiceResult modifyUserProfile(String profile, String value) {
    Long userId = LoginUserUtil.getLoginUserId();
    if (profile == null || profile.isEmpty()) {
      return new ServiceResult(false, "属性不可以为空");
    }
    switch (profile) {
      case "name":
        userRepository.updateUsername(userId, value);
        break;
      case "email":
        userRepository.updateEmail(userId, value);
        break;
      case "password":
        userRepository.updatePassword(userId, this.passwordEncoder.encodePassword(value, userId));
        break;
      default:
        return new ServiceResult(false, "不支持的属性");
    }
    return ServiceResult.success();
  }

  @Override
  public UserDO findUserByTelephone(String telephone) {
    UserDO user = userRepository.findUserByTelephone(telephone);
    if (user == null) {
      return null;
    }
    List<RoleDO> roles = roleRepository.findRolesByUserId(user.getId());
    if (roles == null || roles.isEmpty()) {
      throw new DisabledException("权限非法");
    }

    List<GrantedAuthority> authorities = new ArrayList<>();
    roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
    user.setAuthorities(authorities);
    return user;
  }

  @Override
  public UserDO addUserByPhone(String telephone) {
    UserDO user = new UserDO();
    user.setPhoneNumber(telephone);
    user.setName(telephone.substring(0, 3) + "****" + telephone.substring(7, telephone.length()));
    Date now = new Date();
    user.setCreateTime(now);
    user.setLastLoginTime(now);
    user.setLastUpdateTime(now);
    user = userRepository.save(user);

    RoleDO role = new RoleDO();
    role.setName("USER");
    role.setUserId(user.getId());
    roleRepository.save(role);
    user.setAuthorities(Lists.newArrayList(new SimpleGrantedAuthority("ROLE_USER")));
    return user;
  }
}
