package com.sms.smsdemo.service;

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
}
