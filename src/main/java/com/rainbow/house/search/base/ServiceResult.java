package com.rainbow.house.search.base;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>功能描述</br>Service返回的结果</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 9:08
 */
@Getter
@Setter
public class ServiceResult<T> {

  /**
   * 是否成功
   */
  private boolean success;
  /**
   * 消息
   */
  private String message;

  /**
   * 返回结果
   */
  private T result;


  public ServiceResult(boolean success) {
    this.success = success;
  }

  public ServiceResult(boolean success, String message) {
    this.success = success;
    this.message = message;
  }

  public ServiceResult(boolean success, String message, T result) {
    this.success = success;
    this.message = message;
    this.result = result;
  }

  public static <T> ServiceResult<T> success() {
    return new ServiceResult<>(true);
  }

  public static <T> ServiceResult<T> result(T result) {
    ServiceResult<T> serviceResult = new ServiceResult<>(true);
    serviceResult.setResult(result);
    return serviceResult;
  }

  public static <T> ServiceResult<T> notFound() {
    return new ServiceResult<>(false, Message.NOT_FOUND.getValue());
  }

  public enum Message {
    NOT_FOUND("Not Found Resource!"),
    NOT_LOGIN("User not login!");

    private String value;

    Message(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }
}
