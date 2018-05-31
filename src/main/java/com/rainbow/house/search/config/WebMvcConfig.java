package com.rainbow.house.search.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

/**
 * <p>功能描述</br>webmvc的配置</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/30 13:39
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter implements ApplicationContextAware {

  @Value("${spring.thymeleaf.cache}")
  private boolean thymeleafCacheEnable = true;

  private ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  /**
   * <pre>静态资源配置</pre>
   *
   * @param registry
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/");
  }

  /**
   * 模板资源解释器
   *
   * @return
   */
  @Bean
  @ConfigurationProperties(prefix = "spring.thymeleaf")
  public SpringResourceTemplateResolver templateResolver() {
    SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
    templateResolver.setApplicationContext(this.applicationContext);
    templateResolver.setCharacterEncoding("UTF-8");
    templateResolver.setCacheable(this.thymeleafCacheEnable);
    return templateResolver;
  }

  /**
   * Thymeleaf标准方言解释器
   *
   * @return
   */
  @Bean
  public SpringTemplateEngine templateEngine() {
    SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    templateEngine.setTemplateResolver(templateResolver());
    /** 支持Spring EL表达式 **/
    templateEngine.setEnableSpringELCompiler(true);
    /** 支持SpringSecurity方言 **/
    SpringSecurityDialect securityDialect = new SpringSecurityDialect();
    templateEngine.addDialect(securityDialect);
    return templateEngine;
  }

  /**
   * Thymeleaf视图解释器
   *
   * @return
   */
  @Bean
  public ThymeleafViewResolver viewResolver() {
    ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
    viewResolver.setTemplateEngine(templateEngine());
    return viewResolver;
  }

  /**
   * <pre>ModelMapper</pre>
   *
   * @return
   */
  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }
}
