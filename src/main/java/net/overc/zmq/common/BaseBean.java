package net.overc.zmq.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;

/**
 * Date: 5/30/18
 *
 * @author Vitalii Siryi
 */
public class BaseBean implements Serializable {

    private final static String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    public String toJson() {
        Gson gson = get();
        return gson.toJson(this);
    }

    public static <E extends BaseBean> E fromJson(String json, Class<E> clazz) {
        Gson gson = get();
        return gson.fromJson(json, clazz);
    }

    private static Gson gson;

    private static Gson get(){
        if (gson == null) {
            gson = new GsonBuilder().setDateFormat(ISO_DATE_FORMAT).create();
        }
        return gson;
    }

}
