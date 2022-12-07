package com.sms.smsdemo.service;

import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;

/**
 * @author: chaojinpeng
 * @since: 2022/12/7 08:53
 * @description:
 */
public interface SmsService {

    /**
     * 单发短信
     *
     * @param mobile
     */
    void singleSend(String mobile);

    void dynamicSend(String mobile, String name, String enddate) throws UnsupportedEncodingException;
}
