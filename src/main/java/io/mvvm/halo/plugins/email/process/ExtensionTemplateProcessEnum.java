package io.mvvm.halo.plugins.email.process;

/**
 * @description:
 * @author: pan
 **/
public enum ExtensionTemplateProcessEnum {

    /**
     * 评论模板
     */
    Comment("comment"),
    /**
     * 回复模板
     */
    Reply("reply"),
    /**
     * 审核模板
     */
    Audit("audit"),
    ;

    private final String value;

    ExtensionTemplateProcessEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ExtensionTemplateProcessEnum convertFrom(String template) {
        for (ExtensionTemplateProcessEnum e : values()) {
            if (e.getValue().equals(template)) {
                return e;
            }
        }
        return null;
    }

}