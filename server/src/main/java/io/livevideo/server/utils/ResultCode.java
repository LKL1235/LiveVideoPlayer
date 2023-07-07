package io.livevideo.server.utils;
import lombok.Getter;

/**
 * @description:
 * @author:LKL1235
 * @date:2022/11/24 19:12
 **/
@Getter
public enum ResultCode {

    SUCCESS(200, "成功"),
    FAILURE(500, "失败"),
    UNKNOWN_ERROR(404, "未知错误");

    // 结果状态码
    private Integer code;
    // 结果消息
    private String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}