package com.rainbow.house.search.service;

import com.rainbow.house.search.base.ServiceResult;

/**
 * <p>功能描述</br>短信服务接口定义</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/2 10:29
 */
public interface SmsService {
  /**
   * <pre>获取验证码</pre>
   *
   * @param telephone 手机号
   * @return
   */
  String getSmsCode(String telephone);

  /**
   * <pre>发送验证码：发送验证码到指定手机 并 缓存验证码 10分钟 及 请求间隔时间1分钟</pre>
   *
   * @param telephone 手机号
   * @return
   */
  ServiceResult<String> sendSms(String telephone);

  /**
   * <pre>移除指定手机号的验证码缓存</pre>
   *
   * @param telephone 手机号
   */
  void remove(String telephone);

}
