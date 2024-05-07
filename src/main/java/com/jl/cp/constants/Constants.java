package com.jl.cp.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author LeJiang
 * @CreateOn 2019/6/19 ^ 下午2:54
 * @Path /
 * @ContentType Content-Type/application-json
 * @Remark
 */
public class Constants {
    /**
     * 大乐透查询历史信息url
     */
    public static final String URL_HISTORY = "http://api.jisuapi.com/caipiao/history?appkey=eb60caaefbb40d74&appsecret=XSEJFWjTkZhEghgZMc9tQ7kgbbiBX32M";

    /**
     * 大乐透查询接口
     */
    public static final String URL_QUERY = "http://api.jisuapi.com/caipiao/query?appkey=f97b238f39605adf&caipiaoid=";


    public static final Map<String,String> WU_XING = new HashMap<>();

    static {
        WU_XING.put("金","09,10,21,22,33");
        WU_XING.put("木","03,04,15,16,27,28");
        WU_XING.put("水","01,12,13,24,25");
        WU_XING.put("火","06,07,18,19,30,31");
        WU_XING.put("土","02,05,08,11,14,17,20,23,26,29,32");
    }



}
