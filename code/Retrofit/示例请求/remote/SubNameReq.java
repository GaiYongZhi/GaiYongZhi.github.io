package com.gai.kbase.remote;

import lombok.Data;

import java.io.Serializable;

/**
 * @author GYZ14576
 * @version 1.0.0
 * @description
 * @date 2024/6/20 16:36
 */
@Data
public class SubNameReq implements Serializable {
    private static final long serialVersionUID = -2347273150725543936L;
    private String name;
}
