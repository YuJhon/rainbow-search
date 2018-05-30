package com.rainbow.house.search.base;

import lombok.Data;
import lombok.Getter;

/**
 * <p>功能描述</br>返回对象</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/30 14:23
 */
@Data
public class RainbowApiResponse {

  /**
   * 返回码
   */
  private int code;
  /**
   * 返回信息
   */
  private String message;
  /**
   * 返回数据
   */
  private Object data;
  /**
   * 是否有更多信息
   */
  private boolean more;

  /**
   * <pre>构造函数01</pre>
   *
   * @param code    返回码
   * @param message 返回消息
   * @param data    返回数据
   */
  public RainbowApiResponse(int code, String message, Object data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  /**
   * <pre>构造函数02</pre>
   */
  public RainbowApiResponse() {
    this.code = RespStatus.SUCCESS.getCode();
    this.message = RespStatus.SUCCESS.getStandardMessage();
  }

  /**
   * <pre>返回成功结果封装</pre>
   *
   * @param data 返回写到的信息
   * @return
   */
  public static RainbowApiResponse success(Object data) {
    return new RainbowApiResponse(RespStatus.SUCCESS.getCode(), RespStatus.SUCCESS.getStandardMessage(), data);
  }

  /**
   * <pre>返回结果</pre>
   *
   * @param respStatus 返回的状态
   * @return
   */
  public static RainbowApiResponse status(RespStatus respStatus) {
    return new RainbowApiResponse(respStatus.getCode(), respStatus.getStandardMessage(), null);
  }

  /**
   * <pre>消息的封装</pre>
   *
   * @param code    返回码
   * @param message 返回消息
   * @return
   */
  public static RainbowApiResponse message(int code, String message) {
    return new RainbowApiResponse(code, message, null);
  }


  /**
   * 状态
   */
  @Getter
  public enum RespStatus {

    SUCCESS(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Unknown Internal Error"),
    NOT_VALID_PARAM(40005, "Not valid Params"),
    NOT_SUPPORTED_OPERATION(40006, "Operation not supported"),
    NOT_LOGIN(50000, "Not Login");

    /**
     * 状态码
     */
    private int code;
    /**
     * 标准消息
     */
    private String standardMessage;

    /**
     * <pre>构造器</pre>
     *
     * @param code            状态码
     * @param standardMessage 标准消息
     */
    RespStatus(int code, String standardMessage) {
      this.code = code;
      this.standardMessage = standardMessage;
    }
  }
}
