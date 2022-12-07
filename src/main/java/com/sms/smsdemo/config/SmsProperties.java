package com.sms.smsdemo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author: chaojinpeng
 * @since: 2022/12/6 10:24
 * @description:
 */
@Data
@Component
@ConfigurationProperties("sms")
public class SmsProperties {

    private String apikey;

    private String sign;

    private String text;

    private String url;

    private Integer connectionTimeout;

    private Integer readTimeout;
}
