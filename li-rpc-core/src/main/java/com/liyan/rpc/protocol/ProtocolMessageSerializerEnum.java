package com.liyan.rpc.protocol;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 协议消息的类型枚举
 */
@Getter
public enum ProtocolMessageSerializerEnum {
    JDK( 0,"jdk"),
    JSON( 1,"json"),
    KRYO( 2,"kryo"),
    HESSIAN( 3,"hessian");

    private final int key;
    private final String value;

    ProtocolMessageSerializerEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }
    /**
     * 获取值列表
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item->item.value).collect(Collectors.toList());
    }



    /**
     * 根据key获取枚举
     * @param key
     * @return
     */
    public static ProtocolMessageSerializerEnum getByKey(int key) {
        for (ProtocolMessageSerializerEnum statusEnum : values()) {
            if (statusEnum.key == key) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 根据value获取枚举
     * @param value
     * @return
     */
    public static ProtocolMessageSerializerEnum getByValue(String value) {
        for (ProtocolMessageSerializerEnum statusEnum : values()) {
            if (statusEnum.value.equals( value)) {
                return statusEnum;
            }
        }
        return null;
    }
}
