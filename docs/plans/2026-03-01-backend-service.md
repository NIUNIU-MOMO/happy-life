# 幸福生活后端服务 Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 从零搭建《幸福生活》小程序的后端服务，提供用户认证、帖子 CRUD、文件上传凭证、收藏互动等 API。

**Architecture:** 基于 Spring Boot 3 + MyBatis-Plus 的单体应用。数据库使用 PostgreSQL + PostGIS 支持地理位置查询。认证采用 JWT 无状态方案。文件直传阿里云 OSS（后端只签发凭证）。全局统一 Result 响应 + GlobalExceptionHandler 异常处理。

**Tech Stack:** Java 17, Spring Boot 3.2.x, MyBatis-Plus 3.5.x, PostgreSQL 15+ (PostGIS), JWT (jjwt), Knife4j (OpenAPI 3), Lombok, Hutool, Fastjson2

---

### Task 1: 初始化 Spring Boot 项目结构

**Files:**
- Create: `pom.xml`
- Create: `src/main/java/com/happylife/HappyLifeApplication.java`
- Create: `src/main/resources/application.yml`
- Create: `src/main/resources/application-dev.yml`
- Create: `.gitignore`

**Step 1: 创建 pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>

    <groupId>com.happylife</groupId>
    <artifactId>happy-life-server</artifactId>
    <version>1.0.0</version>
    <name>happy-life-server</name>
    <description>幸福生活小程序后端服务</description>

    <properties>
        <java.version>17</java.version>
        <mybatis-plus.version>3.5.6</mybatis-plus.version>
        <knife4j.version>4.4.0</knife4j.version>
        <jjwt.version>0.12.5</jjwt.version>
        <hutool.version>5.8.26</hutool.version>
        <fastjson2.version>2.0.47</fastjson2.version>
    </properties>

    <dependencies>
        <!-- Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- MyBatis-Plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>

        <!-- PostgreSQL -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>

        <!-- Knife4j (Swagger) -->
        <dependency>
            <groupId>com.github.xiaoymin</groupId>
            <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
            <version>${knife4j.version}</version>
        </dependency>

        <!-- Hutool -->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>${hutool.version}</version>
        </dependency>

        <!-- Fastjson2 -->
        <dependency>
            <groupId>com.alibaba.fastjson2</groupId>
            <artifactId>fastjson2</artifactId>
            <version>${fastjson2.version}</version>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

**Step 2: 创建启动类**

```java
package com.happylife;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 10:00
 * @Version 1.0
 * @Description 幸福生活小程序后端服务启动类
 */
@SpringBootApplication
public class HappyLifeApplication {

    public static void main(String[] args) {
        SpringApplication.run(HappyLifeApplication.class, args);
    }
}
```

**Step 3: 创建 application.yml**

```yaml
server:
  port: 8080

spring:
  profiles:
    active: dev
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  global-config:
    db-config:
      logic-delete-field: isDeleted
      logic-delete-value: true
      logic-not-delete-value: false
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

**Step 4: 创建 application-dev.yml**

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/happy_life
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver

wechat:
  app-id: wx622e7fcada414776
  app-secret: YOUR_SECRET

jwt:
  secret: happy-life-jwt-secret-key-2026-very-long-string-for-security
  expiration: 604800000

aliyun:
  oss:
    endpoint: oss-cn-hangzhou.aliyuncs.com
    access-key-id: YOUR_AK
    access-key-secret: YOUR_SK
    bucket-name: happy-life-media
```

**Step 5: 创建 .gitignore**

```
target/
*.class
*.jar
*.log
.idea/
*.iml
.DS_Store
application-prod.yml
```

**Step 6: 验证**

Run: `cd /Users/niuniu/project/happy-life-server && mvn clean compile -q`
Expected: BUILD SUCCESS

---

### Task 2: 核心基础设施 (Result + Exception + Config)

**Files:**
- Create: `src/main/java/com/happylife/common/Result.java`
- Create: `src/main/java/com/happylife/common/ServiceException.java`
- Create: `src/main/java/com/happylife/common/GlobalExceptionHandler.java`
- Create: `src/main/java/com/happylife/config/MybatisPlusConfig.java`
- Create: `src/main/java/com/happylife/config/CorsConfig.java`
- Create: `src/main/java/com/happylife/config/Knife4jConfig.java`

**Step 1: 创建统一响应类 Result.java**

```java
package com.happylife.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 10:10
 * @Version 1.0
 * @Description 统一响应结果封装
 */
@Data
@Accessors(chain = true)
@Schema(title = "统一响应结果")
public class Result<T> {

    @Schema(title = "状态码：200 成功，500 失败，401 未认证")
    private Integer code;

    @Schema(title = "响应消息")
    private String msg;

    @Schema(title = "响应数据")
    private T data;

    public static <T> Result<T> ok(T data) {
        return new Result<T>().setCode(200).setMsg("success").setData(data);
    }

    public static <T> Result<T> ok() {
        return new Result<T>().setCode(200).setMsg("success");
    }

    public static <T> Result<T> fail(String msg) {
        return new Result<T>().setCode(500).setMsg(msg);
    }

    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<T>().setCode(code).setMsg(msg);
    }
}
```

**Step 2: 创建自定义业务异常 ServiceException.java**

```java
package com.happylife.common;

import lombok.Getter;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 10:15
 * @Version 1.0
 * @Description 自定义业务异常
 */
@Getter
public class ServiceException extends RuntimeException {

    private final Integer code;

    public ServiceException(String msg) {
        super(msg);
        this.code = 500;
    }

    public ServiceException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }
}
```

**Step 3: 创建全局异常处理器 GlobalExceptionHandler.java**

```java
package com.happylife.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 10:20
 * @Version 1.0
 * @Description 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public Result<?> handleServiceException(ServiceException e) {
        log.warn("业务异常：{}", e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("参数校验异常：{}", msg);
        return Result.fail(400, msg);
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常：", e);
        return Result.fail("系统内部错误");
    }
}
```

**Step 4: 创建 MyBatis-Plus 配置 MybatisPlusConfig.java**

```java
package com.happylife.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 10:25
 * @Version 1.0
 * @Description MyBatis-Plus 分页插件配置
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
        return interceptor;
    }
}
```

**Step 5: 创建跨域配置 CorsConfig.java**

```java
package com.happylife.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 10:30
 * @Version 1.0
 * @Description 跨域配置
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

**Step 6: 创建 Knife4j 配置 Knife4jConfig.java**

```java
package com.happylife.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 10:35
 * @Version 1.0
 * @Description Knife4j API 文档配置
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("幸福生活 API")
                        .version("1.0.0")
                        .description("幸福生活小程序后端接口文档"));
    }
}
```

**Step 7: 验证**

Run: `mvn clean compile -q`
Expected: BUILD SUCCESS

---

### Task 3: 实体类与数据库初始化脚本

**Files:**
- Create: `src/main/java/com/happylife/entity/User.java`
- Create: `src/main/java/com/happylife/entity/Post.java`
- Create: `src/main/java/com/happylife/entity/Favorite.java`
- Create: `src/main/resources/db/init.sql`

**Step 1: 创建用户实体 User.java**

```java
package com.happylife.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 11:00
 * @Version 1.0
 * @Description 用户实体
 */
@Data
@Accessors(chain = true)
@TableName("t_user")
@Schema(title = "用户")
public class User {

    @TableId(type = IdType.AUTO)
    @Schema(title = "用户 ID")
    private Long id;

    @Schema(title = "微信 OpenID")
    private String openid;

    @Schema(title = "昵称")
    private String nickname;

    @Schema(title = "头像 URL")
    private String avatarUrl;

    @Schema(title = "创建时间")
    private LocalDateTime createTime;

    @Schema(title = "更新时间")
    private LocalDateTime updateTime;
}
```

**Step 2: 创建帖子实体 Post.java**

```java
package com.happylife.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 11:05
 * @Version 1.0
 * @Description 帖子实体
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_post", autoResultMap = true)
@Schema(title = "帖子")
public class Post {

    @TableId(type = IdType.AUTO)
    @Schema(title = "帖子 ID")
    private Long id;

    @Schema(title = "用户 ID")
    private Long userId;

    @Schema(title = "标题")
    private String title;

    @Schema(title = "描述")
    private String description;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Schema(title = "图片列表")
    private List<String> images;

    @Schema(title = "视频 URL")
    private String videoUrl;

    @Schema(title = "位置名称")
    private String locationName;

    @Schema(title = "纬度")
    private Double latitude;

    @Schema(title = "经度")
    private Double longitude;

    @Schema(title = "帖子日期")
    private LocalDate postDate;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Schema(title = "标签列表")
    private List<String> tags;

    @Schema(title = "浏览次数")
    private Integer viewCount;

    @Schema(title = "收藏次数")
    private Integer likeCount;

    @Schema(title = "创建时间")
    private LocalDateTime createTime;

    @Schema(title = "更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(title = "是否删除")
    private Boolean isDeleted;
}
```

**Step 3: 创建收藏实体 Favorite.java**

```java
package com.happylife.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 11:10
 * @Version 1.0
 * @Description 收藏实体
 */
@Data
@Accessors(chain = true)
@TableName("t_favorite")
@Schema(title = "收藏")
public class Favorite {

    @TableId(type = IdType.AUTO)
    @Schema(title = "收藏 ID")
    private Long id;

    @Schema(title = "用户 ID")
    private Long userId;

    @Schema(title = "帖子 ID")
    private Long postId;

    @Schema(title = "创建时间")
    private LocalDateTime createTime;
}
```

**Step 4: 创建数据库初始化脚本 db/init.sql**

```sql
CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE IF NOT EXISTS t_user (
    id BIGSERIAL PRIMARY KEY,
    openid VARCHAR(64) NOT NULL UNIQUE,
    nickname VARCHAR(64),
    avatar_url VARCHAR(255),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE t_user IS '用户表';

CREATE TABLE IF NOT EXISTS t_post (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(128) NOT NULL,
    description TEXT,
    images JSONB DEFAULT '[]',
    video_url VARCHAR(255),
    location_name VARCHAR(128),
    location_point GEOGRAPHY(POINT, 4326),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    post_date DATE,
    tags JSONB DEFAULT '[]',
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);
COMMENT ON TABLE t_post IS '帖子表';

CREATE INDEX IF NOT EXISTS idx_post_user ON t_post(user_id);
CREATE INDEX IF NOT EXISTS idx_post_location ON t_post USING GIST(location_point);
CREATE INDEX IF NOT EXISTS idx_post_date ON t_post(post_date DESC);

CREATE TABLE IF NOT EXISTS t_favorite (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, post_id)
);
COMMENT ON TABLE t_favorite IS '收藏表';
```

**Step 5: 验证**

Run: `mvn clean compile -q`
Expected: BUILD SUCCESS

---

### Task 4: JWT 工具类与认证拦截器

**Files:**
- Create: `src/main/java/com/happylife/util/JwtUtil.java`
- Create: `src/main/java/com/happylife/config/JwtProperties.java`
- Create: `src/main/java/com/happylife/interceptor/AuthInterceptor.java`
- Create: `src/main/java/com/happylife/config/WebMvcConfig.java`
- Create: `src/main/java/com/happylife/util/UserContext.java`

**Step 1: 创建 JWT 配置属性 JwtProperties.java**

```java
package com.happylife.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 11:30
 * @Version 1.0
 * @Description JWT 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;
    private Long expiration;
}
```

**Step 2: 创建 JWT 工具类 JwtUtil.java**

```java
package com.happylife.util;

import com.happylife.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 11:35
 * @Version 1.0
 * @Description JWT 令牌工具类
 */
@Component
public class JwtUtil {

    @jakarta.annotation.Resource
    private JwtProperties jwtProperties;

    /**
     * 生成 JWT 令牌
     *
     * @param claims 载荷数据
     * @return JWT 字符串
     */
    public String generateToken(Map<String, Object> claims) {
        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(key)
                .compact();
    }

    /**
     * 解析 JWT 令牌
     *
     * @param token JWT 字符串
     * @return 载荷数据
     */
    public Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
```

**Step 3: 创建用户上下文 UserContext.java**

```java
package com.happylife.util;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 11:40
 * @Version 1.0
 * @Description 基于 ThreadLocal 的用户上下文
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static void clear() {
        USER_ID.remove();
    }
}
```

**Step 4: 创建认证拦截器 AuthInterceptor.java**

```java
package com.happylife.interceptor;

import com.happylife.common.ServiceException;
import com.happylife.util.JwtUtil;
import com.happylife.util.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 11:45
 * @Version 1.0
 * @Description JWT 认证拦截器
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");
        if (null == token || !token.startsWith("Bearer ")) {
            throw new ServiceException(401, "未登录");
        }
        try {
            Claims claims = jwtUtil.parseToken(token.substring(7));
            Long userId = claims.get("userId", Long.class);
            UserContext.setUserId(userId);
            return true;
        } catch (Exception e) {
            log.warn("Token 解析失败：{}", e.getMessage());
            throw new ServiceException(401, "登录已过期");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
```

**Step 5: 创建 WebMvc 配置 WebMvcConfig.java**

```java
package com.happylife.config;

import com.happylife.interceptor.AuthInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 11:50
 * @Version 1.0
 * @Description WebMvc 配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/**",
                        "/doc.html",
                        "/webjars/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**"
                );
    }
}
```

**Step 6: 验证**

Run: `mvn clean compile -q`
Expected: BUILD SUCCESS

---

### Task 5: 用户认证模块 (AuthController + Service)

**Files:**
- Create: `src/main/java/com/happylife/mapper/UserMapper.java`
- Create: `src/main/java/com/happylife/service/UserService.java`
- Create: `src/main/java/com/happylife/service/impl/UserServiceImpl.java`
- Create: `src/main/java/com/happylife/controller/AuthController.java`
- Create: `src/main/java/com/happylife/dto/LoginParam.java`
- Create: `src/main/java/com/happylife/vo/LoginVo.java`
- Create: `src/main/java/com/happylife/config/WechatProperties.java`

**Step 1: 创建微信配置属性 WechatProperties.java**

```java
package com.happylife.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 12:00
 * @Version 1.0
 * @Description 微信小程序配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class WechatProperties {

    private String appId;
    private String appSecret;
}
```

**Step 2: 创建 LoginParam.java**

```java
package com.happylife.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 12:05
 * @Version 1.0
 * @Description 登录请求参数
 */
@Data
@Schema(title = "登录请求参数")
public class LoginParam {

    @NotBlank(message = "登录凭证不能为空")
    @Schema(title = "微信登录凭证 code")
    private String code;

    @Schema(title = "用户昵称")
    private String nickName;

    @Schema(title = "用户头像")
    private String avatarUrl;
}
```

**Step 3: 创建 LoginVo.java**

```java
package com.happylife.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 12:10
 * @Version 1.0
 * @Description 登录响应
 */
@Data
@Accessors(chain = true)
@Schema(title = "登录响应")
public class LoginVo {

    @Schema(title = "JWT Token")
    private String token;

    @Schema(title = "用户 ID")
    private Long userId;

    @Schema(title = "昵称")
    private String nickname;

    @Schema(title = "头像")
    private String avatarUrl;
}
```

**Step 4: 创建 UserMapper.java**

```java
package com.happylife.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.happylife.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 12:15
 * @Version 1.0
 * @Description 用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```

**Step 5: 创建 UserService.java**

```java
package com.happylife.service;

import com.happylife.dto.LoginParam;
import com.happylife.vo.LoginVo;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 12:20
 * @Version 1.0
 * @Description 用户服务接口
 */
public interface UserService {

    /**
     * 微信登录
     *
     * @param param 登录参数
     * @return 登录响应
     */
    LoginVo login(LoginParam param);
}
```

**Step 6: 创建 UserServiceImpl.java**

```java
package com.happylife.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.happylife.common.ServiceException;
import com.happylife.config.WechatProperties;
import com.happylife.dto.LoginParam;
import com.happylife.entity.User;
import com.happylife.mapper.UserMapper;
import com.happylife.service.UserService;
import com.happylife.util.JwtUtil;
import com.happylife.vo.LoginVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 12:25
 * @Version 1.0
 * @Description 用户服务实现
 */
@Slf4j
@Service("userService")
public class UserServiceImpl implements UserService {

    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Resource
    private UserMapper userMapper;

    @Resource
    private WechatProperties wechatProperties;

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public LoginVo login(LoginParam param) {
        String openid = getOpenid(param.getCode());
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));

        if (null == user) {
            user = new User()
                    .setOpenid(openid)
                    .setNickname(param.getNickName())
                    .setAvatarUrl(param.getAvatarUrl());
            userMapper.insert(user);
        }

        Map<String, Object> claims = new HashMap<>(2);
        claims.put("userId", user.getId());
        claims.put("openid", openid);
        String token = jwtUtil.generateToken(claims);

        return new LoginVo()
                .setToken(token)
                .setUserId(user.getId())
                .setNickname(user.getNickname())
                .setAvatarUrl(user.getAvatarUrl());
    }

    // =================================================================================================================

    /**
     * 调用微信接口获取 openid
     *
     * @param code 微信登录凭证
     * @return openid
     */
    private String getOpenid(String code) {
        String url = WX_LOGIN_URL
                + "?appid=" + wechatProperties.getAppId()
                + "&secret=" + wechatProperties.getAppSecret()
                + "&js_code=" + code
                + "&grant_type=authorization_code";
        try {
            String result = HttpUtil.get(url);
            JSONObject json = JSONObject.parseObject(result);
            String openid = json.getString("openid");
            if (null == openid) {
                log.error("微信登录失败：{}", result);
                throw new ServiceException("微信登录失败");
            }
            return openid;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用微信接口异常：", e);
            throw new ServiceException("微信登录异常");
        }
    }
}
```

**Step 7: 创建 AuthController.java**

```java
package com.happylife.controller;

import com.happylife.common.Result;
import com.happylife.dto.LoginParam;
import com.happylife.service.UserService;
import com.happylife.vo.LoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 12:30
 * @Version 1.0
 * @Description 认证控制器
 */
@Tag(name = "认证模块")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private UserService userService;

    @Operation(summary = "微信登录")
    @PostMapping("/login")
    public Result<LoginVo> login(@Valid @RequestBody LoginParam param) {
        return Result.ok(userService.login(param));
    }
}
```

**Step 8: 验证**

Run: `mvn clean compile -q`
Expected: BUILD SUCCESS

---

### Task 6: 帖子模块 (PostController + Service + Mapper)

**Files:**
- Create: `src/main/java/com/happylife/mapper/PostMapper.java`
- Create: `src/main/resources/mapper/PostMapper.xml`
- Create: `src/main/java/com/happylife/dto/PostCreateParam.java`
- Create: `src/main/java/com/happylife/dto/PostQueryParam.java`
- Create: `src/main/java/com/happylife/vo/PostVo.java`
- Create: `src/main/java/com/happylife/vo/PostDetailVo.java`
- Create: `src/main/java/com/happylife/service/PostService.java`
- Create: `src/main/java/com/happylife/service/impl/PostServiceImpl.java`
- Create: `src/main/java/com/happylife/controller/PostController.java`

**Step 1: 创建 PostCreateParam.java**

```java
package com.happylife.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 13:00
 * @Version 1.0
 * @Description 发布帖子请求参数
 */
@Data
@Schema(title = "发布帖子请求参数")
public class PostCreateParam {

    @NotBlank(message = "标题不能为空")
    @Schema(title = "标题")
    private String title;

    @Schema(title = "描述")
    private String description;

    @Schema(title = "图片列表")
    private List<String> images;

    @Schema(title = "视频 URL")
    private String videoUrl;

    @Schema(title = "位置名称")
    private String locationName;

    @Schema(title = "纬度")
    private Double latitude;

    @Schema(title = "经度")
    private Double longitude;

    @Schema(title = "帖子日期 (YYYY-MM-DD)")
    private String date;

    @Schema(title = "标签列表")
    private List<String> tags;
}
```

**Step 2: 创建 PostQueryParam.java**

```java
package com.happylife.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 13:05
 * @Version 1.0
 * @Description 帖子查询参数
 */
@Data
@Schema(title = "帖子查询参数")
public class PostQueryParam {

    @Schema(title = "页码")
    private Integer page = 1;

    @Schema(title = "每页条数")
    private Integer size = 10;

    @Schema(title = "查询类型：timeline / map")
    private String type = "timeline";

    @Schema(title = "最小纬度 (地图模式)")
    private Double minLat;

    @Schema(title = "最大纬度 (地图模式)")
    private Double maxLat;

    @Schema(title = "最小经度 (地图模式)")
    private Double minLng;

    @Schema(title = "最大经度 (地图模式)")
    private Double maxLng;
}
```

**Step 3: 创建 PostVo.java**

```java
package com.happylife.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 13:10
 * @Version 1.0
 * @Description 帖子列表视图对象
 */
@Data
@Accessors(chain = true)
@Schema(title = "帖子列表")
public class PostVo {

    @Schema(title = "帖子 ID")
    private Long id;

    @Schema(title = "标题")
    private String title;

    @Schema(title = "描述")
    private String description;

    @Schema(title = "封面图")
    private String coverImage;

    @Schema(title = "位置名称")
    private String locationName;

    @Schema(title = "纬度")
    private Double latitude;

    @Schema(title = "经度")
    private Double longitude;

    @Schema(title = "帖子日期")
    private String postDate;

    @Schema(title = "标签列表")
    private List<String> tags;

    @Schema(title = "收藏数")
    private Integer likeCount;

    @Schema(title = "是否已收藏")
    private Boolean isFavorited;
}
```

**Step 4: 创建 PostDetailVo.java**

```java
package com.happylife.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 13:15
 * @Version 1.0
 * @Description 帖子详情视图对象
 */
@Data
@Accessors(chain = true)
@Schema(title = "帖子详情")
public class PostDetailVo {

    @Schema(title = "帖子 ID")
    private Long id;

    @Schema(title = "标题")
    private String title;

    @Schema(title = "描述")
    private String description;

    @Schema(title = "图片列表")
    private List<String> images;

    @Schema(title = "视频 URL")
    private String videoUrl;

    @Schema(title = "位置名称")
    private String locationName;

    @Schema(title = "纬度")
    private Double latitude;

    @Schema(title = "经度")
    private Double longitude;

    @Schema(title = "帖子日期")
    private String postDate;

    @Schema(title = "标签列表")
    private List<String> tags;

    @Schema(title = "浏览次数")
    private Integer viewCount;

    @Schema(title = "收藏次数")
    private Integer likeCount;

    @Schema(title = "是否已收藏")
    private Boolean isFavorited;

    @Schema(title = "创建时间")
    private String createTime;
}
```

**Step 5: 创建 PostMapper.java**

```java
package com.happylife.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.happylife.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 13:20
 * @Version 1.0
 * @Description 帖子 Mapper
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {

    /**
     * 插入帖子并设置 location_point
     *
     * @param post 帖子实体
     * @return 影响行数
     */
    int insertWithPoint(@Param("post") Post post);

    /**
     * 地图区域查询
     *
     * @param minLng 最小经度
     * @param minLat 最小纬度
     * @param maxLng 最大经度
     * @param maxLat 最大纬度
     * @return 帖子列表
     */
    List<Post> selectByMapBounds(@Param("minLng") Double minLng,
                                 @Param("minLat") Double minLat,
                                 @Param("maxLng") Double maxLng,
                                 @Param("maxLat") Double maxLat);
}
```

**Step 6: 创建 PostMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.happylife.mapper.PostMapper">

    <insert id="insertWithPoint" useGeneratedKeys="true" keyProperty="post.id">
        INSERT INTO t_post (user_id, title, description, images, video_url,
                            location_name, location_point, latitude, longitude,
                            post_date, tags, view_count, like_count)
        VALUES (#{post.userId}, #{post.title}, #{post.description},
                #{post.images, typeHandler=com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler}::jsonb,
                #{post.videoUrl},
                #{post.locationName},
                ST_SetSRID(ST_MakePoint(#{post.longitude}, #{post.latitude}), 4326)::geography,
                #{post.latitude}, #{post.longitude},
                #{post.postDate},
                #{post.tags, typeHandler=com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler}::jsonb,
                0, 0)
    </insert>

    <select id="selectByMapBounds" resultType="com.happylife.entity.Post">
        SELECT id, user_id, title, description, images, video_url,
               location_name, latitude, longitude, post_date, tags,
               view_count, like_count, create_time, update_time, is_deleted
        FROM t_post
        WHERE is_deleted = false
        AND ST_Contains(
            ST_MakeEnvelope(#{minLng}, #{minLat}, #{maxLng}, #{maxLat}, 4326)::geography::geometry,
            location_point::geometry
        )
        ORDER BY post_date DESC
        LIMIT 100
    </select>

</mapper>
```

**Step 7: 创建 PostService.java**

```java
package com.happylife.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.happylife.dto.PostCreateParam;
import com.happylife.dto.PostQueryParam;
import com.happylife.vo.PostDetailVo;
import com.happylife.vo.PostVo;

import java.util.List;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 13:30
 * @Version 1.0
 * @Description 帖子服务接口
 */
public interface PostService {

    /**
     * 发布帖子
     *
     * @param param 创建参数
     * @return 帖子 ID
     */
    Long createPost(PostCreateParam param);

    /**
     * 时间线分页查询
     *
     * @param param 查询参数
     * @return 分页帖子列表
     */
    IPage<PostVo> getTimelinePosts(PostQueryParam param);

    /**
     * 地图区域查询
     *
     * @param param 查询参数
     * @return 帖子列表
     */
    List<PostVo> getMapPosts(PostQueryParam param);

    /**
     * 获取帖子详情
     *
     * @param id 帖子 ID
     * @return 帖子详情
     */
    PostDetailVo getPostDetail(Long id);

    /**
     * 删除帖子
     *
     * @param id 帖子 ID
     */
    void deletePost(Long id);
}
```

**Step 8: 创建 PostServiceImpl.java**

```java
package com.happylife.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.happylife.common.ServiceException;
import com.happylife.dto.PostCreateParam;
import com.happylife.dto.PostQueryParam;
import com.happylife.entity.Favorite;
import com.happylife.entity.Post;
import com.happylife.mapper.FavoriteMapper;
import com.happylife.mapper.PostMapper;
import com.happylife.service.PostService;
import com.happylife.util.UserContext;
import com.happylife.vo.PostDetailVo;
import com.happylife.vo.PostVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 13:35
 * @Version 1.0
 * @Description 帖子服务实现
 */
@Slf4j
@Service("postService")
public class PostServiceImpl implements PostService {

    @Resource
    private PostMapper postMapper;

    @Resource
    private FavoriteMapper favoriteMapper;

    @Override
    public Long createPost(PostCreateParam param) {
        Long userId = UserContext.getUserId();
        Post post = new Post()
                .setUserId(userId)
                .setTitle(param.getTitle())
                .setDescription(param.getDescription())
                .setImages(param.getImages())
                .setVideoUrl(param.getVideoUrl())
                .setLocationName(param.getLocationName())
                .setLatitude(param.getLatitude())
                .setLongitude(param.getLongitude())
                .setTags(param.getTags());

        if (null != param.getDate()) {
            post.setPostDate(LocalDate.parse(param.getDate(), DateTimeFormatter.ISO_LOCAL_DATE));
        }

        postMapper.insertWithPoint(post);
        return post.getId();
    }

    @Override
    public IPage<PostVo> getTimelinePosts(PostQueryParam param) {
        Long userId = UserContext.getUserId();
        Page<Post> page = new Page<>(param.getPage(), param.getSize());
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<Post>()
                .orderByDesc(Post::getPostDate);
        IPage<Post> postPage = postMapper.selectPage(page, wrapper);

        return postPage.convert(post -> convertToVo(post, userId));
    }

    @Override
    public List<PostVo> getMapPosts(PostQueryParam param) {
        Long userId = UserContext.getUserId();
        List<Post> posts = postMapper.selectByMapBounds(
                param.getMinLng(), param.getMinLat(),
                param.getMaxLng(), param.getMaxLat());
        return CollectionUtils.isEmpty(posts)
                ? new ArrayList<>()
                : posts.stream().map(post -> convertToVo(post, userId)).collect(Collectors.toList());
    }

    @Override
    public PostDetailVo getPostDetail(Long id) {
        Long userId = UserContext.getUserId();
        Post post = postMapper.selectById(id);
        if (null == post) {
            throw new ServiceException("帖子不存在");
        }

        postMapper.updateById(new Post().setId(id).setViewCount(post.getViewCount() + 1));

        boolean isFavorited = checkFavorited(userId, id);
        return new PostDetailVo()
                .setId(post.getId())
                .setTitle(post.getTitle())
                .setDescription(post.getDescription())
                .setImages(post.getImages())
                .setVideoUrl(post.getVideoUrl())
                .setLocationName(post.getLocationName())
                .setLatitude(post.getLatitude())
                .setLongitude(post.getLongitude())
                .setPostDate(null != post.getPostDate() ? post.getPostDate().toString() : null)
                .setTags(post.getTags())
                .setViewCount(post.getViewCount())
                .setLikeCount(post.getLikeCount())
                .setIsFavorited(isFavorited)
                .setCreateTime(null != post.getCreateTime() ? post.getCreateTime().toString() : null);
    }

    @Override
    public void deletePost(Long id) {
        Long userId = UserContext.getUserId();
        Post post = postMapper.selectById(id);
        if (null == post) {
            throw new ServiceException("帖子不存在");
        }
        if (!Objects.equals(post.getUserId(), userId)) {
            throw new ServiceException("无权限删除");
        }
        postMapper.deleteById(id);
    }

    // =================================================================================================================

    /**
     * 转换帖子实体为列表 VO
     *
     * @param post   帖子实体
     * @param userId 当前用户 ID
     * @return 帖子列表 VO
     */
    private PostVo convertToVo(Post post, Long userId) {
        String coverImage = CollectionUtils.isEmpty(post.getImages()) ? null : post.getImages().get(0);
        boolean isFavorited = checkFavorited(userId, post.getId());
        return new PostVo()
                .setId(post.getId())
                .setTitle(post.getTitle())
                .setDescription(post.getDescription())
                .setCoverImage(coverImage)
                .setLocationName(post.getLocationName())
                .setLatitude(post.getLatitude())
                .setLongitude(post.getLongitude())
                .setPostDate(null != post.getPostDate() ? post.getPostDate().toString() : null)
                .setTags(post.getTags())
                .setLikeCount(post.getLikeCount())
                .setIsFavorited(isFavorited);
    }

    /**
     * 检查用户是否收藏了帖子
     *
     * @param userId 用户 ID
     * @param postId 帖子 ID
     * @return 是否收藏
     */
    private boolean checkFavorited(Long userId, Long postId) {
        if (null == userId) {
            return false;
        }
        Long count = favoriteMapper.selectCount(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getPostId, postId));
        return count > 0;
    }
}
```

**Step 9: 创建 PostController.java**

```java
package com.happylife.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.happylife.common.Result;
import com.happylife.dto.PostCreateParam;
import com.happylife.dto.PostQueryParam;
import com.happylife.service.PostService;
import com.happylife.vo.PostDetailVo;
import com.happylife.vo.PostVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 13:50
 * @Version 1.0
 * @Description 帖子控制器
 */
@Tag(name = "帖子模块")
@RestController
@RequestMapping("/posts")
public class PostController {

    @Resource
    private PostService postService;

    @Operation(summary = "发布帖子")
    @PostMapping
    public Result<Long> createPost(@Valid @RequestBody PostCreateParam param) {
        return Result.ok(postService.createPost(param));
    }

    @Operation(summary = "查询帖子列表")
    @GetMapping
    public Result<?> getPosts(PostQueryParam param) {
        if (Objects.equals("map", param.getType())) {
            List<PostVo> posts = postService.getMapPosts(param);
            return Result.ok(posts);
        }
        IPage<PostVo> page = postService.getTimelinePosts(param);
        return Result.ok(page);
    }

    @Operation(summary = "获取帖子详情")
    @GetMapping("/{id}")
    public Result<PostDetailVo> getPostDetail(@PathVariable Long id) {
        return Result.ok(postService.getPostDetail(id));
    }

    @Operation(summary = "删除帖子")
    @DeleteMapping("/{id}")
    public Result<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return Result.ok();
    }
}
```

**Step 10: 验证**

Run: `mvn clean compile -q`
Expected: BUILD SUCCESS

---

### Task 7: 收藏互动模块

**Files:**
- Create: `src/main/java/com/happylife/mapper/FavoriteMapper.java`
- Create: `src/main/java/com/happylife/dto/FavoriteParam.java`
- Create: `src/main/java/com/happylife/service/FavoriteService.java`
- Create: `src/main/java/com/happylife/service/impl/FavoriteServiceImpl.java`
- Create: `src/main/java/com/happylife/controller/InteractionController.java`

**Step 1: 创建 FavoriteMapper.java**

```java
package com.happylife.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.happylife.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 14:00
 * @Version 1.0
 * @Description 收藏 Mapper
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
```

**Step 2: 创建 FavoriteParam.java**

```java
package com.happylife.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 14:05
 * @Version 1.0
 * @Description 收藏请求参数
 */
@Data
@Schema(title = "收藏请求参数")
public class FavoriteParam {

    @NotBlank(message = "操作类型不能为空")
    @Schema(title = "操作类型：favorite / unfavorite")
    private String action;
}
```

**Step 3: 创建 FavoriteService.java**

```java
package com.happylife.service;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 14:10
 * @Version 1.0
 * @Description 收藏服务接口
 */
public interface FavoriteService {

    /**
     * 收藏/取消收藏
     *
     * @param postId 帖子 ID
     * @param action 操作类型: favorite / unfavorite
     */
    void toggleFavorite(Long postId, String action);
}
```

**Step 4: 创建 FavoriteServiceImpl.java**

```java
package com.happylife.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.happylife.common.ServiceException;
import com.happylife.entity.Favorite;
import com.happylife.entity.Post;
import com.happylife.mapper.FavoriteMapper;
import com.happylife.mapper.PostMapper;
import com.happylife.service.FavoriteService;
import com.happylife.util.UserContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 14:15
 * @Version 1.0
 * @Description 收藏服务实现
 */
@Slf4j
@Service("favoriteService")
public class FavoriteServiceImpl implements FavoriteService {

    @Resource
    private FavoriteMapper favoriteMapper;

    @Resource
    private PostMapper postMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleFavorite(Long postId, String action) {
        Long userId = UserContext.getUserId();
        Post post = postMapper.selectById(postId);
        if (null == post) {
            throw new ServiceException("帖子不存在");
        }

        if (Objects.equals("favorite", action)) {
            Long count = favoriteMapper.selectCount(
                    new LambdaQueryWrapper<Favorite>()
                            .eq(Favorite::getUserId, userId)
                            .eq(Favorite::getPostId, postId));
            if (count > 0) {
                return;
            }
            Favorite favorite = new Favorite().setUserId(userId).setPostId(postId);
            favoriteMapper.insert(favorite);
            postMapper.updateById(new Post().setId(postId).setLikeCount(post.getLikeCount() + 1));
        } else if (Objects.equals("unfavorite", action)) {
            favoriteMapper.delete(
                    new LambdaQueryWrapper<Favorite>()
                            .eq(Favorite::getUserId, userId)
                            .eq(Favorite::getPostId, postId));
            int newCount = Math.max(0, post.getLikeCount() - 1);
            postMapper.updateById(new Post().setId(postId).setLikeCount(newCount));
        }
    }
}
```

**Step 5: 创建 InteractionController.java**

```java
package com.happylife.controller;

import com.happylife.common.Result;
import com.happylife.dto.FavoriteParam;
import com.happylife.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 14:20
 * @Version 1.0
 * @Description 互动控制器
 */
@Tag(name = "互动模块")
@RestController
@RequestMapping("/posts")
public class InteractionController {

    @Resource
    private FavoriteService favoriteService;

    @Operation(summary = "收藏/取消收藏")
    @PostMapping("/{id}/favorite")
    public Result<Void> toggleFavorite(@PathVariable Long id, @Valid @RequestBody FavoriteParam param) {
        favoriteService.toggleFavorite(id, param.getAction());
        return Result.ok();
    }
}
```

**Step 6: 验证**

Run: `mvn clean compile -q`
Expected: BUILD SUCCESS

---

### Task 8: 文件上传凭证模块

**Files:**
- Create: `src/main/java/com/happylife/config/OssProperties.java`
- Create: `src/main/java/com/happylife/vo/OssPolicyVo.java`
- Create: `src/main/java/com/happylife/service/FileService.java`
- Create: `src/main/java/com/happylife/service/impl/FileServiceImpl.java`
- Create: `src/main/java/com/happylife/controller/FileController.java`

**Step 1: 创建 OSS 配置属性 OssProperties.java**

```java
package com.happylife.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 14:30
 * @Version 1.0
 * @Description 阿里云 OSS 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssProperties {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
}
```

**Step 2: 创建 OssPolicyVo.java**

```java
package com.happylife.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 14:35
 * @Version 1.0
 * @Description OSS 上传凭证
 */
@Data
@Accessors(chain = true)
@Schema(title = "OSS 上传凭证")
public class OssPolicyVo {

    @Schema(title = "AccessKey ID")
    private String accessId;

    @Schema(title = "Base64 编码的 Policy")
    private String policy;

    @Schema(title = "签名")
    private String signature;

    @Schema(title = "上传目录")
    private String dir;

    @Schema(title = "OSS 上传地址")
    private String host;

    @Schema(title = "过期时间")
    private Long expire;
}
```

**Step 3: 创建 FileService.java**

```java
package com.happylife.service;

import com.happylife.vo.OssPolicyVo;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 14:40
 * @Version 1.0
 * @Description 文件服务接口
 */
public interface FileService {

    /**
     * 获取 OSS 上传凭证
     *
     * @return 上传凭证
     */
    OssPolicyVo getUploadPolicy();
}
```

**Step 4: 创建 FileServiceImpl.java**

```java
package com.happylife.service.impl;

import com.happylife.config.OssProperties;
import com.happylife.service.FileService;
import com.happylife.vo.OssPolicyVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 14:45
 * @Version 1.0
 * @Description 文件服务实现（OSS 直传签名）
 */
@Slf4j
@Service("fileService")
public class FileServiceImpl implements FileService {

    private static final long EXPIRE_TIME = 300L;

    @Resource
    private OssProperties ossProperties;

    @Override
    public OssPolicyVo getUploadPolicy() {
        String dir = "posts/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/";
        String host = "https://" + ossProperties.getBucketName() + "." + ossProperties.getEndpoint();
        long expireEndTime = System.currentTimeMillis() / 1000 + EXPIRE_TIME;

        String policyJson = "{\"expiration\":\"" + getExpiration(expireEndTime) + "\","
                + "\"conditions\":[[\"content-length-range\",0,10485760],"
                + "[\"starts-with\",\"$key\",\"" + dir + "\"]]}";

        String policyBase64 = Base64.getEncoder().encodeToString(policyJson.getBytes(StandardCharsets.UTF_8));
        String signature = hmacSha1(ossProperties.getAccessKeySecret(), policyBase64);

        return new OssPolicyVo()
                .setAccessId(ossProperties.getAccessKeyId())
                .setPolicy(policyBase64)
                .setSignature(signature)
                .setDir(dir)
                .setHost(host)
                .setExpire(expireEndTime);
    }

    // =================================================================================================================

    /**
     * 计算 HMAC-SHA1 签名
     *
     * @param key  密钥
     * @param data 待签名数据
     * @return Base64 编码签名
     */
    private String hmacSha1(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            log.error("签名计算异常：", e);
            throw new RuntimeException("签名计算失败");
        }
    }

    /**
     * 生成 ISO8601 格式过期时间
     *
     * @param expireEndTime 过期时间戳(秒)
     * @return ISO8601 格式字符串
     */
    private String getExpiration(long expireEndTime) {
        java.time.Instant instant = java.time.Instant.ofEpochSecond(expireEndTime);
        return instant.toString();
    }
}
```

**Step 5: 创建 FileController.java**

```java
package com.happylife.controller;

import com.happylife.common.Result;
import com.happylife.service.FileService;
import com.happylife.vo.OssPolicyVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author NIUNIU
 * @Date 2026/03/01 14:50
 * @Version 1.0
 * @Description 文件控制器
 */
@Tag(name = "文件模块")
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileService fileService;

    @Operation(summary = "获取 OSS 上传凭证")
    @PostMapping("/upload/policy")
    public Result<OssPolicyVo> getUploadPolicy() {
        return Result.ok(fileService.getUploadPolicy());
    }
}
```

**Step 6: 验证**

Run: `mvn clean compile -q`
Expected: BUILD SUCCESS

---
