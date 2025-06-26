package com.cnki.openai.chat.chat.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author GYZ14576
 * @version 1.0.0
 * @description
 * @date 2023/3/30 10:32
 */
@Slf4j
public class JsonUtils3 {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        //忽略不识别的字段（json属性与目标实体存在属性上的差异
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //允许原始值为null
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        //允许将枚举序列化/反序列化为数字
        objectMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
    }

    private JsonUtils3() {
    }

    public static ObjectMapper getMapper() {
        return objectMapper;
    }

    @SneakyThrows
    public static <T> T readValue(String json, Class<T> clazz) {
        return getMapper().readValue(json, clazz);

    }
    @SneakyThrows
    public static <T> T readValue(File jsonFile, Class<T> clazz) {
        return getMapper().readValue(jsonFile, clazz);
    }
    @SneakyThrows
    public static <T> T readValue(URL jsonURL, Class<T> clazz)  {
        return getMapper().readValue(jsonURL, clazz);
    }

    /**
     * json 转换为 Map
     *
     * @param json
     * @return Map
     */
    @SneakyThrows
    public static HashMap readValue(String json)  {
        return getMapper().readValue(json, HashMap.class);
    }


    /**
     * 反序列化带有泛型的 JSON 字符串到对象
     * @param json
     * @param typeReference
     * @return
     * @param <T>
     * @throws IOException
     */
    @SneakyThrows
    public static <T> T readValue(String json, TypeReference<T> typeReference) {
        return getMapper().readValue(json, typeReference);
    }

    /**
     * json 转换为 java List集合
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> List<T> readValueToList(String json, Class<T> clazz) {
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
        return getMapper().readValue(json, listType);
    }


    /**
     * 将任意一个对象转为json字符串
     * @param obj
     * @return
     * @throws JsonProcessingException
     */
    @SneakyThrows
    public static String toJson(Object obj)   {
        return getMapper().writeValueAsString(obj);

    }

    public static class StringHelper{
        /**
         * 替换不间断空格符
         * @param str
         * @return
         */
        public String replace(String str){
            // UnicodeUtil为 hutool包下
//            log.debug("\\u00a0字符过滤并进行过滤,原本参数转为unicode字符串打印为:" + UnicodeUtil.toUnicode(str));
            String newStr = str.replace("\u00a0", " ");
            return newStr;
        }
        /**
         * json字符串格式化
         * @param str
         * @return
         */
        public static String format(String str){
            try {
                return getMapper()
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(
                                getMapper().readTree(str)
                        );
            } catch (JsonProcessingException e) {

                return str;
            }
        }
    }

}
