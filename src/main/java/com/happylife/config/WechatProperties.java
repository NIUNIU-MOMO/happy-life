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
