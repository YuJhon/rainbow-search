package com.rainbow.house.search.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.MultipartProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;

/**
 * <p>功能描述</br>文件上传配置</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/30 23:41
 */
@Configuration
@ConditionalOnClass({Servlet.class, StandardServletMultipartResolver.class, MultipartConfigElement.class})
@ConditionalOnProperty(prefix = "spring.http.multipart", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(MultipartProperties.class)
public class WebFileUploadConfig {

  private final MultipartProperties multipartProperties;

  public WebFileUploadConfig(MultipartProperties multipartProperties) {
    this.multipartProperties = multipartProperties;
  }

  /**
   * 上传配置
   */
  @Bean
  @ConditionalOnMissingBean
  public MultipartConfigElement multipartConfigElement() {
    return this.multipartProperties.createMultipartConfig();
  }

  /**
   * 注册解析器
   */
  @Bean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
  @ConditionalOnMissingBean(MultipartResolver.class)
  public StandardServletMultipartResolver multipartResolver() {
    StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
    multipartResolver.setResolveLazily(this.multipartProperties.isResolveLazily());
    return multipartResolver;
  }
}
