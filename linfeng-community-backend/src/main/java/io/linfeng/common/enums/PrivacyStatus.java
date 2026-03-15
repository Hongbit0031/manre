package io.linfeng.common.enums;

/**
 * 圈子 公开 私密
 */
public enum PrivacyStatus {
    /**
     * 公开
     */
    PUBLIC(0),
    /**
     * 私密
     */
    PRIVACY(1);

    private int value;

    PrivacyStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}