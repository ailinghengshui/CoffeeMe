package com.hzjytech.coffeeme.http;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.entities.CreatOrderBean;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeMap;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

import static com.alibaba.fastjson.util.IOUtils.UTF8;

/**
 * Created by hehongcan on 2017/8/23.
 */

class SignInterceptor implements Interceptor {
    private final Context context;
    public static final MediaType JSON = MediaType.parse("application/json:charset=utf-8");

    public SignInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String time = TimeUtil.getCurrentTimeString();
        //获取到request
        Request request = chain.request();

        //获取到方法
        String method = request.method();
        Map<String, String> params = parseParams(request);
        if (params == null) {
            params = new HashMap<>();
        }
        try {
            //get请求的封装
            if (method.equals("GET")) {
                //获取到请求地址api
                HttpUrl httpUrlurl = request.url();

                Map<String, String> rootMap = new TreeMap<>();
                //通过请求地址(最初始的请求地址)获取到参数列表
                Set<String> parameterNames = httpUrlurl.queryParameterNames();
                for (String key : parameterNames) {
                    //循环参数列表
                    rootMap.put(key, httpUrlurl.queryParameter(key));
                }
                rootMap.put(Configurations.SIGN,
                        SignUtils.createSignString(JPushInterface.getRegistrationID(context),
                                time,
                                rootMap));

                //通过请求地址(最初始的请求地址)获取到参数列表
                rootMap.put(Configurations.DEVICE_ID, JPushInterface.getRegistrationID(context));
                rootMap.put(Configurations.TIMESTAMP, time);
                String url = httpUrlurl.toString();
                int index = url.indexOf("?");
                if (index > 0) {
                    url = url.substring(0, index);
                }
                if (rootMap.size() > 0) {
                    url += "?";
                    Set<Map.Entry<String, String>> entries = rootMap.entrySet();
                    for (Map.Entry<String, String> entry : entries) {
                        url += (entry.getKey() + "=" + entry.getValue() + "&");
                    }
                }

                request = request.newBuilder()
                        .url(url.substring(0, url.lastIndexOf("&")))
                        .build();  //重新构建请求


                //post请求的封装
            } else {
                if (method.equals("POST") || method.equals("PUT")) {
                    if (request.body() instanceof FormBody) {
                        Map<String, String> rootMap = new TreeMap<>();
                        FormBody.Builder bodyBuilder = new FormBody.Builder();
                        bodyBuilder.add(Configurations.DEVICE_ID,
                                JPushInterface.getRegistrationID(context));
                        bodyBuilder.add(Configurations.TIMESTAMP, time);
                        Set<Map.Entry<String, String>> entries = params.entrySet();
                        for (Map.Entry<String, String> entry : entries) {
                            bodyBuilder.add(entry.getKey(), entry.getValue());
                            rootMap.put(entry.getKey(),entry.getValue());
                        }
                        bodyBuilder.add(Configurations.SIGN,SignUtils.createSignString( JPushInterface.getRegistrationID(context),time,rootMap));
                        request = request.newBuilder()
                                .method(method, bodyBuilder.build())
                                .build();
                    } else {
                        RequestBody requestBody = request.body();
                        Buffer buffer = new Buffer();
                        requestBody.writeTo(buffer);
                        Charset charset = Charset.forName("UTF-8");
                        MediaType contentType = requestBody.contentType();
                        if (contentType != null) {
                            charset = contentType.charset(UTF8);
                        }
                        String paramsStr = buffer.readString(charset);
                        JSONObject fastObject = JSONObject.parseObject(paramsStr,
                                JSONObject.class);
                        Map<String, String> map = new TreeMap<>();
                        Set<Map.Entry<String, Object>> entries = fastObject.entrySet();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(Configurations.DEVICE_ID,
                                JPushInterface.getRegistrationID(context));
                        jsonObject.put(Configurations.TIMESTAMP, time);
                        for (Map.Entry<String, Object> entry : entries) {
                            map.put(entry.getKey(),entry.getValue().toString());
                            jsonObject.put(entry.getKey(),entry.getValue().toString());
                        }
                        String signString = SignUtils.createSignString(JPushInterface
                                .getRegistrationID(
                                        context), time, map);
                        jsonObject.put(Configurations.SIGN,signString);
                        RequestBody body = RequestBody.create(MediaType.parse(
                                "application/json; " + "charset=UTF-8"), jsonObject.toString());
                        request = request.newBuilder()
                                .method(method, body)
                                .build();

                    }
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        //最后通过chain.proceed(request)进行返回
        return chain.proceed(request);
    }

    /**
     * 解析请求参数
     *
     * @param request
     * @return
     */
    public static Map<String, String> parseParams(Request request) {
        //GET POST DELETE PUT PATCH
        String method = request.method();
        Map<String, String> params = null;
        if ("GET".equals(method)) {
            params = doGet(request);
        } else if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method) ||
                "PATCH".equals(
                method)) {
            RequestBody body = request.body();
            if (body != null && body instanceof FormBody) {
                params = doForm(request);
            }
        }
        return params;
    }

    /**
     * 获取get方式的请求参数
     *
     * @param request
     * @return
     */
    private static Map<String, String> doGet(Request request) {
        Map<String, String> params = null;
        HttpUrl url = request.url();
        Set<String> strings = url.queryParameterNames();
        if (strings != null) {
            Iterator<String> iterator = strings.iterator();
            params = new HashMap<>();
            int i = 0;
            while (iterator.hasNext()) {
                String name = iterator.next();
                String value = url.queryParameterValue(i);
                params.put(name, value);
                i++;
            }
        }
        return params;
    }

    /**
     * 获取表单的请求参数
     *
     * @param request
     * @return
     */
    private static Map<String, String> doForm(Request request) {
        Map<String, String> params = null;
        FormBody body = null;
        try {
            body = (FormBody) request.body();
        } catch (ClassCastException c) {
        }
        if (body != null) {
            int size = body.size();
            if (size > 0) {
                params = new HashMap<>();
                for (int i = 0; i < size; i++) {
                    params.put(body.name(i), body.value(i));
                }
            }
        }
        return params;
    }
    public static Map getValue(Object thisObj)
    {
        Map map = new TreeMap();
        Class c;
        try
        {
            c = Class.forName(thisObj.getClass().getName());
            Method[] m = c.getMethods();
            for (int i = 0; i < m.length; i++)
            {
                String method = m[i].getName();
                if (method.startsWith("get"))
                {
                    try{
                        Object value = m[i].invoke(thisObj);
                        if (value != null)
                        {
                            String key=method.substring(3);
                            key=key.substring(0,1).toUpperCase()+key.substring(1);
                            map.put(method, value);
                        }
                    }catch (Exception e) {
                        // TODO: handle exception
                        System.out.println("error:"+method);
                    }
                }
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
            e.printStackTrace();
        }
        return map;
    }
}

