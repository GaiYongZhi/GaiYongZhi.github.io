> JsonUtils

```java
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author GYZ14576
 * @version 1.0.0
 * @description
 * @date 2023/3/30 10:32
 */
@Slf4j
public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static class DatePattern{
        public static final String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
        public static final String NORM_DATE_PATTERN = "yyyy-MM-dd";
        public static final String NORM_TIME_PATTERN = "HH:mm:ss";

        public static final String GTM8 = "GMT+8";
    }

    static {
        //1)序列化

        // 禁用 遇到空对象报错
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        //允许C和C++样式注释：
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        // 允许出现特殊字符和转义符
        //mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);这个已经过时。
        objectMapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);

        //允许字段名没有引号（可以进一步减小json体积）：
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

        //允许单引号：
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        //Include.NON_NULL 属性为NULL 不序列化
        //ALWAYS // 默认策略，任何情况都执行序列化
        //NON_EMPTY // null、集合数组等没有内容、空字符串等，都不会被序列化
        //NON_DEFAULT // 如果字段是默认值，就不会被序列化
        //NON_ABSENT // null的不会序列化，但如果类型是AtomicReference，依然会被序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);


        //序列化结果格式化，美化输出
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        //针对时间序列化方式
        // 日期格式
        objectMapper.setDateFormat(new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN));
        //时区设置 GMT+8
        //map.put("CTT", "Asia/Shanghai");
        objectMapper.setTimeZone(TimeZone.getTimeZone(DatePattern.GTM8));

        //针对java8 LocalDateTime LocalDate LocalTime
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        javaTimeModule.addSerializer(new LocalTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));
        javaTimeModule.addSerializer(new LocalDateSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));

        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));

        objectMapper.registerModule(javaTimeModule);

        //2)反序列化

        //忽略不识别的字段（json属性与目标实体存在属性上的差异
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //允许原始值为null
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        //允许将枚举序列化/反序列化为数字
        objectMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
        //反序列化时，遇到忽略属性不要抛出异常：
        objectMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
    }

    private JsonUtils() {
    }

    public static ObjectMapper getMapper() {
        return objectMapper;
    }

    @SneakyThrows
    public static <T> T readValue(String json, Class<T> clazz)  {
        return objectMapper.readValue(json, clazz);

    }
    @SneakyThrows
    public static <T> T readValue(File jsonFile, Class<T> clazz)  {
        return objectMapper.readValue(jsonFile, clazz);
    }

    @SneakyThrows
    public static <T> T readValue(URL jsonURL, Class<T> clazz)  {
        return objectMapper.readValue(jsonURL, clazz);
    }

    /**
     * json 转换为 Map
     *
     * @param json
     * @return Map
     */
    @SneakyThrows
    public static Map readValue(String json) {
        return objectMapper.readValue(json, Map.class);
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
    public static <T> List<T> readValueToList(String json, Class<T> clazz){
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
        return objectMapper.readValue(json, listType);
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
        return objectMapper.readValue(json, typeReference);
    }
    /**
     * 将任意一个对象转为json字符串
     * @param obj
     * @return
     * @throws JsonProcessingException
     */
    @SneakyThrows
    public static String toJson(Object obj) {
        return objectMapper.writeValueAsString(obj);

    }

    public static class Helper{
        /**
         * 替换不间断空格符
         * @param str
         * @return
         */
        public static String replace(String str){
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

```


- 版本2  JsonUtils3.java

```java
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

```
