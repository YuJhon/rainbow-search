package com.rainbow.house.search.service.impl;

import com.rainbow.house.search.base.ServiceResult;
import com.rainbow.house.search.service.SmsService;

/**
 * <p>功能描述</br>短信验证码接口实现类</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/2 10:35
 */
public class SmsServiceImpl implements SmsService {

  @Override
  public String getSmsCode(String telephone) {
    return null;
  }

  @Override
  public ServiceResult<String> sendSms(String telephone) {
    return null;
  }

  @Override
  public void remove(String telephone) {

  }
}
