package com.liyan.rpc.protocol;

import lombok.Getter;

/**
 * 协议消息的状态枚举
 */
@Getter
public enum ProtocolMessageStatusEnum {
    OK("ok", 20),
    BAD_REQUEST("ok", 20),
    BAD_RESPONSE("ok", 20);
    private final String text;
    private final int value;

    ProtocolMessageStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据value获取枚举
     * @param value
     * @return
     */
    public static ProtocolMessageStatusEnum getByValue(int value) {
        for (ProtocolMessageStatusEnum statusEnum : values()) {
            if (statusEnum.value == value) {
                return statusEnum;
            }
        }
        return null;
    }
}
