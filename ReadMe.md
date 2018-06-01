### 基于ElasticSearch全局搜索项目实战

#### 1.需求说明
> 待补充

#### 2.数据库设计
（略）

#### 3.项目工程结构
![项目结构](./doc/photos/001.Project%20Struct.png)


#### 4.开发细节
* 视图解析器的配置
```java
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
```

* 返回信息的统一处理
>TODO
    
* 文件上传
    * 002.DispatcherServlet中的解释
    ![002.DispatcherServlet中的解释](./doc/photos/002.DispatcherServlet-MultipartResover%20Description.png)
    * 003.MultipartResolver流程梳理
    ![003.MultipartResolver流程梳理](./doc/photos/003.MultipartResolver流程梳理.png)
    * 004.文件上传代码和配置
    ![004.文件上传代码和配置](./doc/photos/004.文件上传代码和配置.png)

* 分布式session实现
    * 1.maven 依赖
    ```xml
    <!-- redis session依赖 -->
    <dependency>
        <groupId>org.springframework.session</groupId>
        <artifactId>spring-session</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    ```
    * 2.配置信息
    ```properties
    #session(存储方式)
    spring.session.store-type=redis
    # redis config
    spring.redis.database=0
    spring.redis.host=127.0.0.1
    spring.redis.port=6379
    spring.redis.pool.min-idle=1
    spring.redis.timeout=3000
    ```
    * 3.代码实现
    ```java
    package com.rainbow.house.search.config;
    
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.data.redis.connection.RedisConnectionFactory;
    import org.springframework.data.redis.core.RedisTemplate;
    import org.springframework.data.redis.core.StringRedisTemplate;
    import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
    
    /**
     * <p>功能描述</br></p>
     *
     * @author jiangy19
     * @version v1.0
     * @projectName rainbow-search
     * @date 2018/6/1 9:07
     */
    @Configuration
    @EnableRedisHttpSession(maxInactiveIntervalInSeconds = 86400)
    public class RedisSessionConfig {
    
      @Bean
      public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
      }
    }

    ```

* ES的安装和配置
> TODO

#### 5.单元测试
* 测试基类的配置
```java
package com.rainbow.house.search;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(value = "test")
public class RainbowSearchApplicationTests {

}

```
* 测试类（用户测试类）
```java
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
```

#### 6.结束语

