package com.hzjytech.coffeeme.http;

/**
 * Created by hehongcan on 2017/11/16.
 */

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.SignUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * 自定义请求RequestBody
 */
public class JsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    /**
     * 构造器
     */

    public JsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }


    @Override
    public RequestBody convert(T value) throws IOException {
        //加密
     /*   JsonObject jo = new Gson().fromJson(value.toString(), JsonObject.class);
        Map<String, String> map = new TreeMap<>();
        Set<Map.Entry<String, JsonElement>> entries = jo.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            map.put(entry.getKey(),entry.getValue().toString());
        }
        String signString = SignUtils.createSignString(JPushInterface
                .getRegistrationID(
                        context), time, map);
        StringBuilder builder = new StringBuilder();
        builder.append("{")
                .append("\"")
                .append(Configurations.DEVICE_ID + "\":\"" + JPushInterface
                        .getRegistrationID(
                                context) + "\",\"")
                .append(Configurations.TIMESTAMP + "\":\"" + time + "\",")
                .append(paramsStr.substring(1, paramsStr.length() - 1) + ",\"")
                .append(Configurations.SIGN+"\":\""+signString)
                .append("\"}");
        APIBodyData data = new APIBodyData();
        LogUtil.i("xiaozhang", "request中传递的json数据：" + value.toString());
        data.setData(XXTEA.Encrypt(value.toString(), HttpConstant.KEY));*/
        String postBody = gson.toJson(value); //对象转化成json
        LogUtil.i("xiaozhang", "转化后的数据：" + postBody);
        return RequestBody.create(MEDIA_TYPE, postBody);
    }

}
