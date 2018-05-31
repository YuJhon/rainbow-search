package com.rainbow.house.search.base;

import com.rainbow.house.search.entity.UserDO;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.regex.Pattern;

/**
 * <p>功能描述</br>登录用户的工具类</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/31 12:03
 */
public class LoginUserUtil {

  private static final String PHONE_REGEX = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\\\d{8}$";
  private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

  private static final String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\\\.[a-zA-Z0-9_-]+)+$";
  private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

  /**
   * <pre>获取用户</pre>
   *
   * @return
   */
  public static UserDO load() {
    Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principle != null && principle instanceof UserDO) {
      return (UserDO) principle;
    }
    return null;
  }

  /**
   * <pre>获取登录用户的ID</pre>
   *
   * @return
   */
  public static Long getLoginUserId() {
    UserDO user = load();
    if (user == null) {
      return -1L;
    }
    return user.getId();
  }

  /**
   * <pre>验证手机号码</pre>
   *
   * @param target
   * @return
   */
  public static boolean checkTelephone(String target) {
    return PHONE_PATTERN.matcher(target).matches();
  }

  /**
   * <pre>验证邮箱</pre>
   *
   * @param target
   * @return
   */
  public static boolean checkEmail(String target) {
    return EMAIL_PATTERN.matcher(target).matches();
  }
}
