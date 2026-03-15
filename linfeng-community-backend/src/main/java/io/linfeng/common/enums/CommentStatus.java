package io.linfeng.common.enums;

/**
 * 用户类型
 */
public enum CommentStatus {
    /**
     * 正常显示
     */
    NORMAL(1),
    /**
     * 不显示
     */
    OFF(0),
    /**
     * 待审核
     */
    AUDIT(2);

    private int value;

    CommentStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}