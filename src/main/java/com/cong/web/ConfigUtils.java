package com.cong.web;

import arc.util.serialization.BaseJsonWriter;
import arc.util.serialization.JsonReader;
import arc.util.serialization.JsonValue;
import arc.util.serialization.JsonWriter;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigUtils {

    private static File json() throws IOException {
        File json = new File("config.json");
        if (!json.exists()) {
            json.createNewFile();
        }
        return json;
    }

    public static void set(String path, Object data) {
        try {
            String jStr = Files.readString(json().toPath());
            if (!jStr.isEmpty()) {
                JsonReader jr = new JsonReader();
                JsonValue json = jr.parse(jStr);
                boolean has = false;
                for (JsonValue v : json) {
                    if (v.name().equalsIgnoreCase(path)) {
                        has = true;
                        if (data instanceof List) {
                            JsonValue value = new JsonValue(JsonValue.ValueType.array);
                            ((List<?>) data).forEach(d -> {
                                value.addChild(new JsonValue(d.toString()));
                            });
                            json.remove(path);
                            json.addChild(path, value);
                        } else
                            v.set(data.toString());
                        break;
                    }
                }
                if (!has) {
                    if (data instanceof List) {
                        JsonValue value = new JsonValue(JsonValue.ValueType.array);
                        ((List<?>) data).forEach(d -> {
                            value.addChild(new JsonValue(d.toString()));
                        });
                        json.addChild(path, value);
                    } else
                        json.addChild(path, new JsonValue(data.toString()));
                }
                PrintWriter pw = new PrintWriter(new FileWriter(json()));
                pw.print(json.toJson(JsonWriter.OutputType.json));
                pw.close();
            } else {
                StringWriter sw = new StringWriter();
                BaseJsonWriter jw = new JsonWriter(sw).object();
                if (data instanceof List) {
                    BaseJsonWriter arr = jw.array(path);
                    ((List<?>) data).forEach(d -> {
                        try {
                            arr.value(d.toString());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else
                    jw.set(path, data);
                jw.close();

                PrintWriter pw = new PrintWriter(new FileWriter(json()));
                pw.print(sw.toString());
                pw.close();

                sw.flush();
                sw.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getString(String path, String def) throws IOException {
        String jStr = Files.readString(json().toPath());
        JsonReader jr = new JsonReader();
        if (!jStr.isEmpty()) {
            for (JsonValue value : jr.parse(jStr)) {
                if (value.name().equals(path)) {
                    return value.asString();
                }
            }
        }
        return def;
    }

    public static String getContent() throws IOException {
        return Files.readString(json().toPath());
    }
}
