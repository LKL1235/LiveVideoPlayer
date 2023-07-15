package io.livevideo.server.utils;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
public class MyResult {

    private boolean result;
    private String msg;
    private Object data;

    //所有成员变量的getters and setters
    public boolean getResult() {
        return result;
    }
    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String message) {
        this.msg = message;
    }

    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }

}
