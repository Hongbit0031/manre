package io.linfeng.common.enums;

/**
 * 用户类型
 */
public enum UserTypeStatus {
    /**
     * 普通用户
     */
    COMMON(0),
    /**
     * 官方用户
     */
    OFFICIAL(1),
    /**
     * 虚拟用户
     */
    VIRTUALLY(2);

    private int value;

    UserTypeStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}