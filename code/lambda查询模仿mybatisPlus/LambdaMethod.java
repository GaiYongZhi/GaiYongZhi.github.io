package com.lambda;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author GYZ14576
 * @version 1.0.0
 * @description
 * @date 2025/3/21 9:18
 */
public class LambdaMethod<T> {

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class JavaBean implements Serializable {
        private static final long serialVersionUID = 1L;
        private String fieldName;
        private String methodName;
        private String column;
    }
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Condition {
        private  String column;
        private  String fieldName;
        private  String methodName;
        private  Object value;
    }
    // 解析 Lambda 表达式，获取字段名及注解值
    protected Condition getColumnName(SFunction<T, ?> function) {
        try {
            // 通过序列化获取方法信息
            SerializedLambda lambda = getSerializedLambda(function);
            String methodName = lambda.getImplMethodName();

            // 方法名转字段名（例如：getName -> name）
            String fieldName = methodToFieldName(methodName);

            // 获取字段上的注解
            Class<?> clazz = Class.forName(lambda.getImplClass().replace("/", "."));
            Field field = clazz.getDeclaredField(fieldName);
            Column column = field.getAnnotation(Column.class);
            // 优先使用注解中的列名，否则使用字段名
            return Condition.builder()
                    .column(column.value())
                    .fieldName(fieldName)
                    .methodName(methodName)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("解析 Lambda 表达式失败", e);
        }
    }

    // 方法名转字段名（处理 get/is 前缀）
    protected String methodToFieldName(String methodName) {
        if (methodName.startsWith("get")) {
            return lowerFirst(methodName.substring(3));
        } else if (methodName.startsWith("is")) {
            return lowerFirst(methodName.substring(2));
        }
        throw new IllegalArgumentException("无效的 getter 方法: " + methodName);
    }

    // 首字母小写
    private String lowerFirst(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    // 通过反射获取 SerializedLambda
    private SerializedLambda getSerializedLambda(SFunction<T, ?> function) throws Exception {
        if (function == null) {
            throw new IllegalArgumentException("Lambda不能为null");
        }
        Method writeReplace = function.getClass()
                .getDeclaredMethod("writeReplace");
        writeReplace.setAccessible(true);
        SerializedLambda  invoke =(SerializedLambda)  writeReplace.invoke(function);
        return invoke;
    }

    // 条件内部类

}
