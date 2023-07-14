package io.livevideo.server.utils;

import java.util.HashMap;
import java.util.Map;


public class myResult {

    public static Map<String,Object> getResultAsMap(boolean result, String msg, Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("result", result);
        map.put("msg", msg);
        map.put("data", data);
        return map;
    }

}
