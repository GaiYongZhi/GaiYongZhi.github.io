package com.lambda;

import lombok.Data;

import java.io.Serializable;

/**
 * @author GYZ14576
 * @version 1.0.0
 * @description
 * @date 2025/3/20 16:53
 */
// 实体类


// 使用工具类构建条件
public class Test {
    public static void main(String[] args) {
        LambdaWrapper<User> wrapper = LambdaWrapper.of(User.class)
                .eq(User::getName, "Alice")
                .eq(User::getEmail, "@example.com")
                .eq(User::getName, "Alice");
        // 输出条件
        wrapper.getConditions().forEach(System.out::println);
    }

    @Data
    public static class User implements Serializable {
    private static final long serialVersionUID = 1L;

        @Column("user_name") // 绑定到自定义列名
        private String name;

        @Column("email_address")
        private String email;

    }
}
