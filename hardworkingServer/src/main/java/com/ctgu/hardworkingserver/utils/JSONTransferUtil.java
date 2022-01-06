package com.ctgu.hardworkingserver.utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletResponse;

public class JSONTransferUtil {

    public static Object transferJsonToBean(String JSON,String classPath)
    {
        try {
            Class aClass=Class.forName(classPath);
            JSONObject jsonObject=JSONObject.fromObject(JSON);
            return JSONObject.toBean(jsonObject,aClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String transferBeanToJson(Object obj)
    {
        return JSONObject.fromObject(obj).toString();
    }

    public static String transferBeanToJsonArray(Object obj){return JSONArray.fromObject(obj).toString();}


    public static void setResp(HttpServletResponse resp)
    {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Expose-Headers","Authorization");
    }
}
