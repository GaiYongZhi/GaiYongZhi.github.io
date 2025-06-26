package com.lambda;


import java.util.ArrayList;
import java.util.List;
/**
 * @author GYZ14576
 * @version 1.0.0
 * @description
 * @date 2025/3/20 16:58
 */
public class LambdaWrapper<T> extends LambdaMethod<T>{
    private final List<Condition> conditions = new ArrayList<>();

    public  static <R> LambdaWrapper<R> of(Class<R> clazz){
        return new LambdaWrapper<>();
    }

    // 添加等于条件
    public LambdaWrapper<T> eq(SFunction<T, ?> function, Object value) {
        Condition condition =  getColumnName(function);
        condition.setValue(value);
        conditions.add(condition);
        return this;
    }
    // 获取所有条件
    public List<Condition> getConditions() {
        return conditions;
    }

}

