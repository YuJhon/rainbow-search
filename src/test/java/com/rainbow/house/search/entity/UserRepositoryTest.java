package com.rainbow.house.search.entity;

import com.rainbow.house.search.RainbowSearchApplicationTests;
import com.rainbow.house.search.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>功能描述</br>用户测试类</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/30 11:50
 */
public class UserRepositoryTest extends RainbowSearchApplicationTests {

  @Autowired
  private UserRepository userRepository;

  @Test
  public void queryUserTest() {
    UserDO user = userRepository.findOne(1L);
    Assert.assertEquals(user.getName(),"jhonrain");
  }

}
