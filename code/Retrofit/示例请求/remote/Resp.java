package com.gai.kbase.remote;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author GYZ14576
 * @version 1.0.0
 * @description
 * @date 2024/6/20 16:27
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Resp<T> implements Serializable {


        private Integer code;

        private String message;

        private T content;

        private Boolean success;

        public static <T> Resp<T> success(T data) {
                return new Resp<T>(0, "请求成功", data,true);
        }

        public static <T> Resp<T> error(Integer code, String msg) {
                return new Resp<>(code, msg, null,false);
        }
}
