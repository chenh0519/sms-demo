package com.sms.smsdemo.controller;

import com.sms.smsdemo.service.impl.SmsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

/**
 * @author: chaojinpeng
 * @since: 2022/12/5 18:38
 * @description:
 */
@RestController
@RequestMapping("/sms")
public class SmsController {

    @Resource
    private SmsServiceImpl smsService;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @PostMapping("/singleSend")
    public void singleSend(@RequestParam String mobile) {
        threadPoolTaskExecutor.execute(() -> {
            smsService.singleSend(mobile);
        });
    }
}
