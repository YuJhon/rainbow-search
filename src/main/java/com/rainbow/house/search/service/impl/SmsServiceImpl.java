package com.rainbow.house.search.service.impl;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.rainbow.house.search.base.ServiceResult;
import com.rainbow.house.search.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * <p>功能描述</br>短信验证码接口实现类</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/2 10:35
 */
@Service
@Slf4j
public class SmsServiceImpl implements SmsService, InitializingBean {

  @Value("${aliyun.sms.accessKey}")
  private String accessKey;

  @Value("${aliyun.sms.accessKeySecret}")
  private String secertKey;

  @Value("${aliyun.sms.template.code}")
  private String templateCode;

  private IAcsClient acsClient;

  private final static String SMS_CODE_CONTENT_PREFIX = "SMS::CODE::CONTENT";

  private static final String[] NUMS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
  private static final Random random = new Random();

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @Override
  public String getSmsCode(String telephone) {
    return this.redisTemplate.opsForValue().get(SMS_CODE_CONTENT_PREFIX + telephone);
  }

  @Override
  public ServiceResult<String> sendSms(String telephone) {
    String gapKey = "SMS::CODE::INTERVAL::" + telephone;
    String result = redisTemplate.opsForValue().get(gapKey);
    if (result != null) {
      return new ServiceResult<>(false, "请求次数太频繁");
    }

    String code = generateRandomSmsCode();
    log.info("code is {}",code);
    String templateParam = String.format("{\"code\": \"%s\"}", code);

    /** 组装请求对象 **/
    SendSmsRequest request = new SendSmsRequest();

    /** 使用post提交 **/
    request.setMethod(MethodType.POST);
    request.setPhoneNumbers(telephone);
    request.setTemplateParam(templateParam);
    request.setTemplateCode(templateCode);
    request.setSignName("寻屋");

    /*
    boolean success = false;
    try {
      SendSmsResponse response = acsClient.getAcsResponse(request);
      if ("OK".equals(response.getCode())) {
        success = true;
      } else {
        // TODO log this question
      }
    } catch (ClientException e) {
      e.printStackTrace();
    }
    */
    boolean success = true;
    if (success) {
      redisTemplate.opsForValue().set(gapKey, code, 60, TimeUnit.SECONDS);
      redisTemplate.opsForValue().set(SMS_CODE_CONTENT_PREFIX + telephone, code, 10, TimeUnit.MINUTES);
      return ServiceResult.result(code);
    } else {
      return new ServiceResult<>(false, "服务忙，请稍后重试");
    }
  }

  @Override
  public void remove(String telephone) {
    this.redisTemplate.delete(SMS_CODE_CONTENT_PREFIX + telephone);
  }

  /**
   * <pre>6位验证码生成器</pre>
   *
   * @return
   */
  private static String generateRandomSmsCode() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 6; i++) {
      int index = random.nextInt(10);
      sb.append(NUMS[index]);
    }
    return sb.toString();
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
    System.setProperty("sun.net.client.defaultReadTimeout", "10000");

    IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKey, secertKey);

    String product = "Dysmsapi";
    String domain = "dysmsapi.aliyuncs.com";

    DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
    this.acsClient = new DefaultAcsClient(profile);
  }
}
