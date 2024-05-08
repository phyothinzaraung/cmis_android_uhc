package com.koekoetech.clinic.helper;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import io.realm.RealmList;

/**
 * Created by ZMN on 4/24/18.
 **/
public class RealmListConverter<T> implements JsonSerializer<RealmList<T>>, JsonDeserializer<RealmList<T>> {

    @Override
    public RealmList<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        RealmList<T> stringRealmList = new RealmList<>();
        JsonArray jsonArray = json.getAsJsonArray();
        for (JsonElement je : jsonArray) {
            T str = context.deserialize(je, new TypeToken<T>() {
            }.getType());
            stringRealmList.add(str);
        }
        return stringRealmList;
    }

    @Override
    public JsonElement serialize(RealmList<T> src, Type typeOfSrc, JsonSerializationContext context) {
        if (src != null && !src.isEmpty()) {
            JsonArray jsonArray = new JsonArray();
            for (T obj : src) {
                jsonArray.add(context.serialize(obj));
            }
            return jsonArray;
        }
        return null;
    }
}
