package com.sms.smsdemo.dto;

import lombok.Data;

/**
 * @author: chaojinpeng
 * @since: 2022/12/6 11:18
 * @description:
 */
@Data
public class SmsResponseDTO {

    private Integer http_status_code;
    private Integer code;
    private String msg;
    private String detail;
}
