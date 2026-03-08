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
