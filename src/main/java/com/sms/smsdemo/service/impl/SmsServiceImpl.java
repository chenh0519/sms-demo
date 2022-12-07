package com.sms.smsdemo.service.impl;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.sms.smsdemo.config.SmsProperties;
import com.sms.smsdemo.dto.SmsResponseDTO;
import com.sms.smsdemo.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: chaojinpeng
 * @since: 2022/12/5 18:24
 * @description:
 */
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    private static final String ENCODING = "UTF-8";


    @Autowired
    private SmsProperties smsProperties;

    /**
     * value：抛出指定异常才会重试
     * include：和value一样，默认为空，当exclude也为空时，默认所有异常
     * exclude：指定不处理的异常
     * maxAttempts：最大重试次数，默认3次
     * backoff：重试等待策略，
     * 默认使用@Backoff，@Backoff的value默认为1000L； 以毫秒为单位的延迟（默认 1000）
     * multiplier（指定延迟倍数）默认为0，表示固定暂停1秒后进行重试，如果把multiplier设置为1.5，则第一次重试为2秒，第二次为3秒，第三次为4.5秒。
     *
     * @param mobile 手机号码
     */
    @Override
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000L, multiplier = 1.5), recover = "singleSendRecover")
    public void singleSend(String mobile) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("apikey", smsProperties.getApikey());
        params.put("text", StrUtil.format(smsProperties.getText(), smsProperties.getSign()));
        params.put("mobile", mobile);

        log.info("[SMS] Single Send Request url:{}, params:{}", smsProperties.getUrl(), params);
        String body = null;
        try {
            body = this.post(smsProperties.getUrl(), params);
        } catch (Exception e) {
            log.error("[SMS] Request execute Error, e:{}", ExceptionUtil.stacktraceToString(e));
            throw new RuntimeException(StrUtil.format("Request execute Error, e:{}", e.getMessage()));
        }
        log.info("[SMS] Single Send Response body:{}", body);

        SmsResponseDTO response = this.response(body);
        System.out.println(response);
    }

    /**
     * value：抛出指定异常才会重试
     * include：和value一样，默认为空，当exclude也为空时，默认所有异常
     * exclude：指定不处理的异常
     * maxAttempts：最大重试次数，默认3次
     * backoff：重试等待策略，
     * 默认使用@Backoff，@Backoff的value默认为1000L； 以毫秒为单位的延迟（默认 1000）
     * multiplier（指定延迟倍数）默认为0，表示固定暂停1秒后进行重试，如果把multiplier设置为1.5，则第一次重试为2秒，第二次为3秒，第三次为4.5秒。
     *
     * @param mobile 手机号码
     */
    @Override
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000L, multiplier = 1.5), recover = "singleSendRecover")
    public void dynamicSend(String mobile, String name, String enddate) throws UnsupportedEncodingException {
        HashMap<String, Object> params = new HashMap<>();
        params.put("apikey", smsProperties.getApikey());
        params.put("mobile", mobile);
        params.put("tpl_id", smsProperties.getId1());
        params.put("tpl_value", URLEncoder.encode("#name#", ENCODING) + "=" + URLEncoder.encode(name, ENCODING) + "&" + URLEncoder.encode("#enddate#", ENCODING) + "=" + URLEncoder.encode(enddate, ENCODING));

        log.info("[SMS] Dynamic Send Request url:{}, params:{}", smsProperties.getUrl1(), params);
        String body = null;
        try {
            body = this.post(smsProperties.getUrl1(), params);
        } catch (Exception e) {
            log.error("[SMS] Request execute Error, e:{}", ExceptionUtil.stacktraceToString(e));
            throw new RuntimeException(StrUtil.format("Request execute Error, e:{}", e.getMessage()));
        }
        log.info("[SMS] Dynamic Send Response body:{}", body);

        SmsResponseDTO response = this.response(body);
        System.out.println(response);
    }

    /**
     * @Recover注解，用于@Retryable重试失败后处理方法。
     * 该注解来开启重试失败后调用的方法(注意,需跟重处理方法在同一个类中)，此注解注释的方法参数一定要是@Retryable抛出的异常，否则无法识别，可以在该方法中进行日志处理。
     * 如果不需要回调方法，可以直接不写回调方法，那么实现的效果是，重试次数完了后，如果还是没成功没符合业务判断，就抛出异常。
     * 可以看到传参里面写的是 Exception e，这个是作为回调的接头暗号（重试次数用完了，还是失败，我们抛出这个Exception e通知触发这个回调方法）。
     * 注意事项：
     * 方法的返回值必须与@Retryable方法一致
     * 方法的第一个参数，必须是Throwable类型的，建议是与@Retryable配置的异常一致，其他的参数，需要哪个参数，写进去就可以了（@Recover方法中有的）
     * 该回调方法与重试方法写在同一个实现类里面
     * @param e
     * @param mobile
     * @return
     */
    @Recover
    public String singleSendRecover(Exception e, String mobile){
        log.info("[SMS] Single Send Errpr mobile:{}, e:{}", mobile, ExceptionUtil.stacktraceToString(e));
        //记日志到数据库 或者 执行其它处理
        return "发送失败";
    }

    private String post(String url, Map<String, Object> params) {
        String response = HttpUtil.createPost(url)
                .form(params)
                .header("Accept", "application/json;charset=utf-8")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .setConnectionTimeout(smsProperties.getConnectionTimeout())
                .setReadTimeout(smsProperties.getReadTimeout())
                .execute()
                .body();
        return response;
    }

    private SmsResponseDTO response(String response) {
        if (StrUtil.isBlank(response)) {
            log.error("[SMS] 未响应");
            return null;
        }
        return JSONUtil.toBean(response, SmsResponseDTO.class);
    }

}
